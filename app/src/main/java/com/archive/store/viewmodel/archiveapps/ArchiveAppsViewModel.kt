/*
 * SPDX-FileCopyrightText: 2026 Archive OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.viewmodel.archiveapps

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.archive.store.data.helper.DownloadHelper
import com.archive.store.data.model.ArchiveApp
import com.archive.store.data.model.ArchiveAppItem
import com.archive.store.data.model.NetworkStatus
import com.archive.store.data.network.ArchiveAppsRepository
import com.archive.store.data.providers.NetworkProvider
import com.archive.store.data.room.suite.ExternalApk
import com.archive.store.util.PackageUtil
import com.aurora.gplayapi.data.models.PlayFile
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

sealed interface ArchiveAppsUiState {
    data object Loading : ArchiveAppsUiState
    data class Success(val items: List<ArchiveAppItem>) : ArchiveAppsUiState
    data class Error(val message: String) : ArchiveAppsUiState
}

sealed interface ArchiveAppDetailsUiState {
    data object Loading : ArchiveAppDetailsUiState
    data class Success(val details: ArchiveAppDetails) : ArchiveAppDetailsUiState
    data object Error : ArchiveAppDetailsUiState
}

/** Fetched text blocks shown on the archive app details screen. */
data class ArchiveAppDetails(
    val description: String = "",
    val features: List<String> = emptyList(),
    val changelog: String = ""
)

@HiltViewModel
class ArchiveAppsViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val repository: ArchiveAppsRepository,
    private val downloadHelper: DownloadHelper,
    networkProvider: NetworkProvider
) : ViewModel() {

    var uiState: ArchiveAppsUiState by mutableStateOf(ArchiveAppsUiState.Loading)
        private set

    var detailsUiState: ArchiveAppDetailsUiState by mutableStateOf(ArchiveAppDetailsUiState.Loading)
        private set

    private var cachedApps: List<ArchiveAppItem> = emptyList()

    init {
        combine(
            downloadHelper.downloadsList,
            networkProvider.status.onStart { emit(NetworkStatus.AVAILABLE) }
        ) { downloads, _ ->
            val byPackage = downloads.associateBy { it.packageName }
            cachedApps = cachedApps.map { it.copy(download = byPackage[it.app.packageName]) }
            if (cachedApps.isNotEmpty()) uiState = ArchiveAppsUiState.Success(cachedApps)
        }.launchIn(viewModelScope)

        refresh()
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = ArchiveAppsUiState.Loading
            try {
                val apps = repository.fetchApps()
                cachedApps = apps.map { app ->
                    ArchiveAppItem(
                        app = app,
                        download = downloadHelper.getDownload(app.packageName),
                        isInstalled = PackageUtil.isInstalled(context, app.packageName)
                    )
                }
                uiState = ArchiveAppsUiState.Success(cachedApps)
            } catch (e: Exception) {
                uiState = ArchiveAppsUiState.Error(e.message ?: "Failed to load archive apps")
            }
        }
    }

    fun retry() {
        uiState = ArchiveAppsUiState.Loading
        refresh()
    }

    /**
     * Fetches the description, features and changelog assets of [app] into [detailsUiState].
     * Missing assets degrade gracefully to empty sections.
     */
    fun loadDetails(app: ArchiveApp) {
        detailsUiState = ArchiveAppDetailsUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            detailsUiState = try {
                ArchiveAppDetailsUiState.Success(
                    ArchiveAppDetails(
                        description = repository.fetchText(app.description),
                        features = repository.fetchFeatures(app.features),
                        changelog = repository.fetchText(app.changelog)
                    )
                )
            } catch (_: Exception) {
                ArchiveAppDetailsUiState.Error
            }
        }
    }

    fun download(app: ArchiveApp) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!app.canDownload) return@launch
            val externalApk = ExternalApk(
                packageName = app.packageName,
                versionCode = app.versionCode,
                versionName = app.versionName,
                displayName = app.name,
                iconURL = repository.resolve(app.icon),
                developerName = "Archive OSS",
                fileList = listOf(
                    PlayFile(
                        url = repository.resolve(app.apk),
                        name = app.apk.substringAfterLast('/'),
                        size = app.size,
                        sha256 = app.sha256
                    )
                )
            )
            downloadHelper.enqueueStandalone(externalApk)
        }
    }

    fun cancel(app: ArchiveApp) {
        viewModelScope.launch(Dispatchers.IO) {
            downloadHelper.cancelDownload(app.packageName)
        }
    }
}
