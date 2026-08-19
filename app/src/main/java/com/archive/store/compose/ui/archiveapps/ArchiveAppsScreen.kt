/*
 * SPDX-FileCopyrightText: 2026 Archive OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.compose.ui.archiveapps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.archive.store.R
import com.archive.store.compose.composable.ArchiveListItem
import com.archive.store.compose.composable.Placeholder
import com.archive.store.compose.composable.ShimmerAppRow
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
        ArchiveAppsUiState.Loading -> ArchiveAppsLoading()

        is ArchiveAppsUiState.Error -> Placeholder(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.ic_apps_outage),
            message = stringResource(R.string.archive_apps_error),
            actionLabel = stringResource(R.string.action_retry),
            onAction = viewModel::retry
        )

        is ArchiveAppsUiState.Success -> {
            if (state.items.isEmpty()) {
                Placeholder(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(R.drawable.ic_apps_outage),
                    message = stringResource(R.string.archive_apps_empty)
                )
            } else {
                ArchiveAppsList(
                    items = state.items,
                    onAppClick = { onNavigateTo(Destination.ArchiveAppDetails(it.app)) }
                )
            }
        }
    }
}

@Composable
private fun ArchiveAppsLoading() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = dimensionResource(R.dimen.spacing_small)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xsmall))
    ) {
        items(8) { ShimmerAppRow() }
    }
}

@Composable
private fun ArchiveAppsList(items: List<ArchiveAppItem>, onAppClick: (ArchiveAppItem) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = dimensionResource(R.dimen.spacing_small)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xsmall))
    ) {
        items(items = items, key = { it.app.id }) { item ->
            ArchiveAppListItem(item = item, onClick = { onAppClick(item) })
        }
    }
}

@Composable
private fun ArchiveAppListItem(item: ArchiveAppItem, onClick: () -> Unit) {
    val action = archiveAppAction(item)
    ArchiveListItem(
        headline = item.app.name,
        supporting = "${item.app.versionName}  •  ${CommonUtil.addSiPrefix(item.app.size)}",
        tertiary = if (action == ArchiveAppAction.COMING_SOON) {
            stringResource(R.string.archive_coming_soon)
        } else {
            null
        },
        headlineStyle = MaterialTheme.typography.bodyMedium,
        onClick = onClick,
        leading = {
            AnimatedAppIcon(
                modifier = Modifier.requiredSize(dimensionResource(R.dimen.icon_size_medium)),
                iconUrl = resolveArchiveAsset(item.app.icon),
                progress = action.progressOf(item.download).toFloat(),
                inProgress = action == ArchiveAppAction.PROGRESS ||
                    action == ArchiveAppAction.INSTALLING
            )
        }
    )
}
