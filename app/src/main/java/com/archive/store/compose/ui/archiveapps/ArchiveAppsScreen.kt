/*
 * SPDX-FileCopyrightText: 2026 Archive OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.compose.ui.archiveapps

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.archive.store.R
import com.archive.store.compose.composable.ContainedLoadingIndicator
import com.archive.store.compose.composable.Placeholder
import com.archive.store.compose.composable.app.AnimatedAppIcon
import com.archive.store.compose.navigation.Destination
import com.archive.store.data.model.ArchiveAppItem
import com.archive.store.data.network.resolveArchiveAsset
import com.archive.store.util.CommonUtil
import com.archive.store.viewmodel.archiveapps.ArchiveAppsUiState
import com.archive.store.viewmodel.archiveapps.ArchiveAppsViewModel

@Composable
fun ArchiveAppsScreen(
    onNavigateTo: (Destination) -> Unit = {},
    viewModel: ArchiveAppsViewModel = hiltViewModel()
) {
    when (val state = viewModel.uiState) {
        ArchiveAppsUiState.Loading -> ContainedLoadingIndicator()

        is ArchiveAppsUiState.Error -> Placeholder(
            painter = painterResource(R.drawable.ic_apps_outage),
            message = stringResource(R.string.archive_apps_error),
            actionLabel = stringResource(R.string.action_retry),
            onAction = viewModel::retry
        )

        is ArchiveAppsUiState.Success -> {
            if (state.items.isEmpty()) {
                Placeholder(
                    painter = painterResource(R.drawable.ic_apps_outage),
                    message = stringResource(R.string.archive_apps_empty)
                )
            } else {
                ArchiveAppsList(
                    items = state.items,
                    onAppClick = { onNavigateTo(Destination.ArchiveAppDetails(it.app)) },
                    onDownload = viewModel::download,
                    onCancel = viewModel::cancel
                )
            }
        }
    }
}

@Composable
private fun ArchiveAppsList(
    items: List<ArchiveAppItem>,
    onAppClick: (ArchiveAppItem) -> Unit,
    onDownload: (com.archive.store.data.model.ArchiveApp) -> Unit,
    onCancel: (com.archive.store.data.model.ArchiveApp) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
    ) {
        items(items = items, key = { it.app.id }) { item ->
            ArchiveAppListItem(
                item = item,
                onClick = { onAppClick(item) },
                onDownload = { onDownload(item.app) },
                onCancel = { onCancel(item.app) }
            )
        }
    }
}

@Composable
private fun ArchiveAppListItem(
    item: ArchiveAppItem,
    onClick: () -> Unit,
    onDownload: () -> Unit,
    onCancel: () -> Unit
) {
    val action = archiveAppAction(item)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = dimensionResource(R.dimen.spacing_medium),
                vertical = dimensionResource(R.dimen.spacing_small)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.requiredSize(dimensionResource(R.dimen.icon_size_medium))) {
            AnimatedAppIcon(
                modifier = Modifier.requiredSize(dimensionResource(R.dimen.icon_size_medium)),
                iconUrl = resolveArchiveAsset(item.app.icon),
                progress = action.progressOf(item.download).toFloat(),
                inProgress = action == ArchiveAppAction.PROGRESS ||
                    action == ArchiveAppAction.INSTALLING
            )
        }
        Spacer(Modifier.width(dimensionResource(R.dimen.spacing_medium)))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.app.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${item.app.versionName}  •  ${CommonUtil.addSiPrefix(item.app.size)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(dimensionResource(R.dimen.spacing_small)))
        when (action) {
            ArchiveAppAction.COMING_SOON -> {
                OutlinedButton(onClick = {}, enabled = false) {
                    Text(stringResource(R.string.archive_coming_soon))
                }
            }

            ArchiveAppAction.DOWNLOAD, ArchiveAppAction.REINSTALL,
            ArchiveAppAction.RETRY -> {
                Button(onClick = onDownload) {
                    Text(action.label())
                }
            }

            ArchiveAppAction.PROGRESS, ArchiveAppAction.INSTALLING -> {
                OutlinedButton(onClick = onCancel) {
                    Text(stringResource(R.string.action_cancel))
                }
            }

            ArchiveAppAction.INSTALLED -> {
                OutlinedButton(onClick = {}, enabled = false) {
                    Text(stringResource(R.string.title_installed))
                }
            }
        }
    }
}
