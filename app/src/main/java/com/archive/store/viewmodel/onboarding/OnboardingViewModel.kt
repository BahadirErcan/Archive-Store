/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.viewmodel.onboarding

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.archive.Constants.FLAVOUR_HUAWEI
import com.archive.Constants.PACKAGE_NAME_GMS
import com.archive.Constants.PACKAGE_NAME_PLAY_STORE
import com.archive.extensions.TAG
import com.archive.extensions.areNotificationsEnabled
import com.archive.extensions.isIgnoringBatteryOptimizations
import com.archive.store.ArchiveApp
import com.archive.store.BuildConfig
import com.archive.store.data.event.InstallerEvent
import com.archive.store.data.helper.UpdateHelper
import com.archive.store.data.model.UpdateMode
import com.archive.store.data.providers.BlacklistProvider
import com.archive.store.data.work.CacheWorker
import com.archive.store.util.FlavouredUtil
import com.archive.store.util.PackageUtil
import com.archive.store.util.Preferences
import com.archive.store.util.Preferences.PREFERENCE_AUTO_DELETE
import com.archive.store.util.Preferences.PREFERENCE_DEFAULT_SELECTED_TAB
import com.archive.store.util.Preferences.PREFERENCE_DISPENSER_URLS
import com.archive.store.util.Preferences.PREFERENCE_FILTER_ARCHIVE_ONLY
import com.archive.store.util.Preferences.PREFERENCE_FILTER_FDROID
import com.archive.store.util.Preferences.PREFERENCE_FOR_YOU
import com.archive.store.util.Preferences.PREFERENCE_INSTALLER_ID
import com.archive.store.util.Preferences.PREFERENCE_INTRO
import com.archive.store.util.Preferences.PREFERENCE_THEME_STYLE
import com.archive.store.util.Preferences.PREFERENCE_UPDATES_AUTO
import com.archive.store.util.Preferences.PREFERENCE_UPDATES_CHECK_INTERVAL
import com.archive.store.util.Preferences.PREFERENCE_UPDATES_EXTENDED
import com.archive.store.util.Preferences.PREFERENCE_VENDING_VERSION
import com.archive.store.util.save
import com.jakewharton.processphoenix.ProcessPhoenix
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class OnboardingUiState(
    val isMicroBundleChecked: Boolean = false,
    val isMicroGBundleInstalled: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    val updateHelper: UpdateHelper,
    val blacklistProvider: BlacklistProvider,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val isMicroGPromptRequired = FlavouredUtil.promptMicroGInstall(context)

    var uiState by mutableStateOf(OnboardingUiState())
        private set

    init {
        ArchiveApp.events.installerEvent.onEach {
            when (it) {
                is InstallerEvent.Installed -> confirmBundleInstall()
                else -> {}
            }
        }.launchIn(ArchiveApp.scope)
    }

    fun onMicrogTOSChecked(value: Boolean) {
        uiState = uiState.copy(isMicroBundleChecked = value)
    }

    fun finishOnboarding() {
        Log.i(TAG, "Finishing onboarding with defaults")
        context.saveDefaultPreferences()

        if (BuildConfig.FLAVOR == FLAVOUR_HUAWEI) {
            blacklistProvider.blacklist(PACKAGE_NAME_GMS)
            blacklistProvider.blacklist(PACKAGE_NAME_PLAY_STORE)
        }

        setupAutoUpdates()
        CacheWorker.scheduleAutomatedCacheCleanup(context)
        Preferences.putBooleanNow(context, PREFERENCE_INTRO, true)

        // Restart the app to ensure all permissions are granted
        ProcessPhoenix.triggerRebirth(context)
    }

    private fun confirmBundleInstall() {
        if (PackageUtil.isMicroGBundleInstalled(context)) {
            uiState = uiState.copy(isMicroGBundleInstalled = true)
        }
    }

    private fun setupAutoUpdates() {
        val updateMode = when {
            context.isIgnoringBatteryOptimizations() -> UpdateMode.CHECK_AND_INSTALL
            context.areNotificationsEnabled() -> UpdateMode.CHECK_AND_NOTIFY
            else -> UpdateMode.DISABLED
        }

        context.save(PREFERENCE_UPDATES_AUTO, updateMode.ordinal)
        context.save(PREFERENCE_UPDATES_CHECK_INTERVAL, 3)
        updateHelper.scheduleAutomatedCheck()
    }

    private fun Context.saveDefaultPreferences() {
        /*Filters*/
        save(PREFERENCE_FILTER_ARCHIVE_ONLY, false)
        save(PREFERENCE_FILTER_FDROID, true)

        /*Network*/
        save(PREFERENCE_DISPENSER_URLS, FlavouredUtil.defaultDispensers)
        save(PREFERENCE_VENDING_VERSION, 0)

        /*Customization*/
        save(PREFERENCE_THEME_STYLE, 0)
        save(PREFERENCE_DEFAULT_SELECTED_TAB, 0)
        save(PREFERENCE_FOR_YOU, true)

        /*Installer*/
        save(PREFERENCE_AUTO_DELETE, true)
        save(PREFERENCE_INSTALLER_ID, 0)

        /*Updates*/
        save(PREFERENCE_UPDATES_EXTENDED, false)
    }
}
