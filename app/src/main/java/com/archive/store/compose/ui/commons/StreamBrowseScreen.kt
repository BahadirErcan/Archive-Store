/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.compose.ui.commons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.archive.extensions.emptyPagingItems
import com.archive.store.R
import com.archive.store.compose.composable.ContainedLoadingIndicator
import com.archive.store.compose.composable.Placeholder
import com.archive.store.compose.composable.TopAppBar
import com.archive.store.compose.composable.app.LargeAppListItem
import com.archive.store.compose.navigation.Destination
import com.archive.store.compose.preview.AppPreviewProvider
import com.archive.store.compose.preview.ThemePreviewProvider
import com.archive.store.viewmodel.browse.StreamBrowseViewModel
import com.aurora.gplayapi.data.models.App
import com.aurora.gplayapi.data.models.StreamCluster
import kotlin.random.Random
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun StreamBrowseScreen(
    streamCluster: StreamCluster,
    onNavigateTo: (Destination) -> Unit,
    viewModel: StreamBrowseViewModel = hiltViewModel(
        creationCallback = { factory: StreamBrowseViewModel.Factory ->
            factory.create(streamCluster)
        }
    )
) {
    val apps = viewModel.apps.collectAsLazyPagingItems()

    ScreenContent(
        title = streamCluster.clusterTitle,
        apps = apps,
        onNavigateTo = onNavigateTo
    )
}

@Composable
private fun ScreenContent(
    title: String = String(),
    apps: LazyPagingItems<App> = emptyPagingItems(),
    onNavigateTo: (Destination) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(title = title)
        }
    ) { paddingValues ->
        when (apps.loadState.refresh) {
            is LoadState.Loading -> ContainedLoadingIndicator()

            is LoadState.Error -> {
                Placeholder(
                    modifier = Modifier.padding(paddingValues),
                    painter = painterResource(R.drawable.ic_refresh),
                    message = stringResource(R.string.error),
                    actionLabel = stringResource(R.string.action_retry),
                    onAction = { apps.retry() }
                )
            }

            else -> {
                if (apps.itemCount == 0) {
                    Placeholder(
                        modifier = Modifier.padding(paddingValues),
                        painter = painterResource(R.drawable.ic_disclaimer),
                        message = stringResource(R.string.no_apps_available)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        verticalArrangement = Arrangement.spacedBy(
                            dimensionResource(R.dimen.spacing_medium)
                        )
                    ) {
                        items(
                            count = apps.itemCount,
                            key = { Uuid.random().toString() }
                        ) { index ->
                            apps[index]?.let { app ->
                                LargeAppListItem(
                                    app = app,
                                    onClick = {
                                        onNavigateTo(Destination.AppDetails(app.packageName))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview
@Composable
private fun StreamBrowseScreenPreview(@PreviewParameter(AppPreviewProvider::class) app: App) {
    val apps = List(10) { app.copy(id = Random.nextInt()) }
    val pagedApps = MutableStateFlow(PagingData.from(apps)).collectAsLazyPagingItems()
    ScreenContent(
        apps = pagedApps
    )
}
