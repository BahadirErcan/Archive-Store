/*
 * SPDX-FileCopyrightText: 2026 Archive OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.compose.ui.archiveapps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.archive.store.R
import com.archive.store.compose.composable.Placeholder
import com.archive.store.compose.composable.app.AnimatedAppIcon
import com.archive.store.compose.navigation.Destination
import com.archive.store.data.model.ArchiveAppItem
import com.archive.store.data.network.resolveArchiveAsset
import com.archive.store.viewmodel.archiveapps.ArchiveAppsUiState
import com.archive.store.viewmodel.archiveapps.ArchiveAppsViewModel

@Composable
fun ArchiveAppsScreen(
    onNavigateTo: (Destination) -> Unit = {},
    viewModel: ArchiveAppsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    when (val state = uiState) {
        ArchiveAppsUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ArchiveAppsUiState.Error -> Placeholder(
            painter = painterResource(R.drawable.archiveappsicon),
            message = stringResource(R.string.archive_apps_error),
            actionLabel = stringResource(R.string.action_retry),
            onAction = viewModel::retry
        )

        is ArchiveAppsUiState.Success -> {
            if (state.items.isEmpty()) {
                Placeholder(
                    painter = painterResource(R.drawable.archiveappsicon),
                    message = stringResource(R.string.archive_apps_empty)
                )
            } else {
                ArchiveAppsGrid(
                    items = state.items,
                    onAppClick = { onNavigateTo(Destination.ArchiveAppDetails(it.app)) },
                    onDownload = viewModel::download
                )
            }
        }
    }
}

@Composable
private fun ArchiveAppsGrid(
    items: List<ArchiveAppItem>,
    onAppClick: (ArchiveAppItem) -> Unit,
    onDownload: (com.archive.store.data.model.ArchiveApp) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = dimensionResource(R.dimen.grid_min_width)),
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.spacing_medium),
            vertical = dimensionResource(R.dimen.spacing_small)
        ),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium)),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items = items, key = { it.app.id }) { item ->
            ArchiveAppCard(
                item = item,
                onClick = { onAppClick(item) },
                onDownload = { onDownload(item.app) }
            )
        }
    }
}

@Composable
private fun ArchiveAppCard(item: ArchiveAppItem, onClick: () -> Unit, onDownload: () -> Unit) {
    // Cards are non-interactive for coming-soon entries; the grid reads better when they
    // look identical to the rest but simply don't navigate.
    Card(
        onClick = onClick,
        enabled = item.app.canDownload,
        elevation = CardDefaults.cardElevation(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        val action = archiveAppAction(item)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.spacing_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
        ) {
            AnimatedAppIcon(
                modifier = Modifier.size(dimensionResource(R.dimen.icon_size_large)),
                iconUrl = resolveArchiveAsset(item.app.icon),
                progress = action.progressOf(item.download).toFloat(),
                inProgress = action == ArchiveAppAction.PROGRESS ||
                    action == ArchiveAppAction.INSTALLING
            )
            Text(
                text = item.app.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "v${item.app.versionName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Button(
                onClick = onDownload,
                enabled = action.isActive,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.button_height_compact))
            ) {
                Text(
                    text = action.label(),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
