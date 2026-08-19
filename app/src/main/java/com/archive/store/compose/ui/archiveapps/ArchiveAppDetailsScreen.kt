/*
 * SPDX-FileCopyrightText: 2026 Archive OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.compose.ui.archiveapps

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.archive.store.R
import com.archive.store.compose.composable.SectionHeader
import com.archive.store.compose.composable.TopAppBar
import com.archive.store.compose.composable.app.AnimatedAppIcon
import com.archive.store.compose.composable.details.ScreenshotListItem
import com.archive.store.compose.ui.details.composable.Changelog
import com.archive.store.data.model.ArchiveApp
import com.archive.store.data.model.ArchiveAppItem
import com.archive.store.data.network.resolveArchiveAsset
import com.archive.store.util.CommonUtil
import com.archive.store.util.PackageUtil
import com.archive.store.viewmodel.archiveapps.ArchiveAppDetailsUiState
import com.archive.store.viewmodel.archiveapps.ArchiveAppsUiState
import com.archive.store.viewmodel.archiveapps.ArchiveAppsViewModel

/**
 * Details for one archived app: description, features, changelog, screenshots and the
 * download/cancel action. Missing text sections are skipped.
 */
@Composable
fun ArchiveAppDetailsScreen(app: ArchiveApp, viewModel: ArchiveAppsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val uiState = viewModel.uiState
    val detailsUiState = viewModel.detailsUiState

    val item = remember(uiState, app.id) {
        (uiState as? ArchiveAppsUiState.Success)?.items?.find { it.app.id == app.id }
            ?: ArchiveAppItem(
                app = app,
                download = null,
                isInstalled = PackageUtil.isInstalled(context, app.packageName)
            )
    }

    LaunchedEffect(app.id) {
        viewModel.loadDetails(app)
    }

    Scaffold(
        topBar = { TopAppBar(title = app.name) }
    ) { paddingValues ->
        ArchiveAppDetailsContent(
            item = item,
            detailsUiState = detailsUiState,
            onDownload = { viewModel.download(app) },
            onCancel = { viewModel.cancel(app) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
private fun ArchiveAppDetailsContent(
    item: ArchiveAppItem,
    detailsUiState: ArchiveAppDetailsUiState,
    onDownload: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val app = item.app
    val action = archiveAppAction(item)
    var screenshotIndex by rememberSaveable { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
    ) {
        item {
            DetailsHeader(item = item, action = action)
        }

        item {
            DownloadActions(action = action, onDownload = onDownload, onCancel = onCancel)
        }

        when (detailsUiState) {
            is ArchiveAppDetailsUiState.Success -> {
                val details = detailsUiState.details
                if (details.description.isNotBlank()) {
                    item {
                        SectionHeader(title = stringResource(R.string.archive_description))
                        Text(
                            text = details.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = dimensionResource(R.dimen.spacing_medium))
                        )
                    }
                }
                if (details.features.isNotEmpty()) {
                    item {
                        SectionHeader(title = stringResource(R.string.archive_features))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = dimensionResource(R.dimen.spacing_medium)),
                            verticalArrangement = Arrangement.spacedBy(
                                dimensionResource(R.dimen.spacing_small)
                            )
                        ) {
                            details.features.forEach { feature ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(
                                        dimensionResource(R.dimen.spacing_small)
                                    ),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "•",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = feature,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1F)
                                    )
                                }
                            }
                        }
                    }
                }
                if (details.changelog.isNotBlank()) {
                    item {
                        Changelog(changelog = details.changelog)
                    }
                }
            }

            ArchiveAppDetailsUiState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(R.dimen.spacing_large)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.requiredSize(
                                dimensionResource(R.dimen.icon_size_small)
                            )
                        )
                    }
                }
            }

            ArchiveAppDetailsUiState.Error -> Unit
        }

        if (app.screenshots.isNotEmpty()) {
            item {
                SectionHeader(title = stringResource(R.string.archive_screenshots))
                LazyRow(
                    contentPadding = PaddingValues(
                        horizontal = dimensionResource(R.dimen.spacing_medium)
                    ),
                    horizontalArrangement = Arrangement.spacedBy(
                        dimensionResource(R.dimen.spacing_small)
                    )
                ) {
                    items(items = app.screenshots, key = { it }) { url ->
                        ScreenshotListItem(
                            modifier = Modifier
                                .height(dimensionResource(R.dimen.screenshot_height))
                                .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_small)))
                                .clickable {
                                    screenshotIndex = app.screenshots.indexOf(url)
                                },
                            url = resolveArchiveAsset(url)
                        )
                    }
                }
            }
        }
    }

    screenshotIndex?.let { index ->
        ScreenshotDialog(
            urls = app.screenshots,
            initialIndex = index,
            onDismiss = { screenshotIndex = null }
        )
    }
}

@Composable
private fun DetailsHeader(item: ArchiveAppItem, action: ArchiveAppAction) {
    val app = item.app
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.spacing_medium)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedAppIcon(
            modifier = Modifier.requiredSize(dimensionResource(R.dimen.icon_size_large)),
            iconUrl = resolveArchiveAsset(app.icon),
            progress = action.progressOf(item.download).toFloat(),
            inProgress = action == ArchiveAppAction.PROGRESS ||
                action == ArchiveAppAction.INSTALLING
        )
        Column(
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.spacing_small)
            )
        ) {
            Text(
                text = app.name,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = buildString {
                    append(stringResource(R.string.version, app.versionName, app.versionCode))
                    if (app.size > 0) {
                        append("  •  ").append(CommonUtil.addSiPrefix(app.size))
                    }
                    if (app.sha256.isNotBlank()) {
                        append("\n")
                            .append(stringResource(R.string.archive_sha256))
                            .append(" ")
                            .append(app.sha256.take(16))
                            .append("…")
                    }
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DownloadActions(
    action: ArchiveAppAction,
    onDownload: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.spacing_medium)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
    ) {
        if (action == ArchiveAppAction.PROGRESS || action == ArchiveAppAction.INSTALLING) {
            FilledTonalButton(
                modifier = Modifier.weight(1F),
                onClick = onCancel,
                enabled = action == ArchiveAppAction.PROGRESS
            ) {
                Text(
                    text = stringResource(R.string.action_cancel),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Button(
            modifier = Modifier.weight(1F),
            onClick = onDownload,
            enabled = action.isActive
        ) {
            Text(
                text = action.label(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ScreenshotDialog(urls: List<String>, initialIndex: Int, onDismiss: () -> Unit) {
    val pagerState = rememberPagerState(initialPage = initialIndex) { urls.size }
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(ZOOM_HEIGHT_RATIO)
                .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_medium)))
        ) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                ScreenshotListItem(
                    modifier = Modifier.fillMaxSize(),
                    url = resolveArchiveAsset(urls[page])
                )
            }
        }
    }
}

private const val ZOOM_HEIGHT_RATIO = 0.75F
