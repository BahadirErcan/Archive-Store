/*
 * SPDX-FileCopyrightText: 2023-2025 The Calyx Institute
 * SPDX-FileCopyrightText: 2024 Archive OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.viewmodel.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.archive.extensions.TAG
import com.archive.store.ArchiveApp
import com.archive.store.data.event.AuthEvent
import com.aurora.gplayapi.data.models.App
import com.aurora.gplayapi.exceptions.GooglePlayException
import com.aurora.gplayapi.helpers.AppDetailsHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = MoreViewModel.Factory::class)
class MoreViewModel @AssistedInject constructor(
    @Assisted private val dependencies: List<String>,
    private val appDetailsHelper: AppDetailsHelper
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(dependencies: List<String>): MoreViewModel
    }

    private val _dependentApps = MutableStateFlow<List<App>?>(emptyList())
    val dependentApps = _dependentApps.asStateFlow()

    init {
        fetchDependencies()
    }

    private fun fetchDependencies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _dependentApps.value = appDetailsHelper.getAppByPackageName(dependencies)
            } catch (exception: GooglePlayException.AuthException) {
                Log.w(TAG, "Dependencies fetch returned ${exception.code}, redirecting to Splash")
                ArchiveApp.events.send(AuthEvent.SessionExpired())
            } catch (exception: Exception) {
                Log.e(TAG, "Failed to fetch dependencies", exception)
                _dependentApps.value = null
            }
        }
    }
}
