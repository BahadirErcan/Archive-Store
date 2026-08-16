/*
 * SPDX-FileCopyrightText: 2026 Archive OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.compose.navigation

import com.archive.store.data.model.PermissionType
import com.archive.store.data.room.update.Update
import com.aurora.gplayapi.data.models.Category
import com.aurora.gplayapi.data.models.StreamCluster

/**
 * All navigation actions available to composable screens.
 * Screens emit one of these via a single `onNavigateTo: (Destination) -> Unit` callback.
 */
sealed class Destination {
    data class Splash(val packageName: String? = null) : Destination()
    data class Main(val initialTab: Int) : Destination()

    data class AppDetails(val packageName: String) : Destination()
    data class DevProfile(val devId: String) : Destination()
    data class AppUpdate(val update: Update) : Destination()

    data object Search : Destination()
    data object Downloads : Destination()

    data class StreamBrowse(val cluster: StreamCluster) : Destination()
    data class ExpandedStreamBrowse(val title: String, val browseUrl: String) : Destination()
    data class CategoryBrowse(val category: Category) : Destination()
    data class PermissionRationale(val permissions: Set<PermissionType>) : Destination()

    data object Accounts : Destination()
    data class GoogleLogin(val addAccount: Boolean = false) : Destination()
    data object About : Destination()
    data object Favourite : Destination()
    data object Spoof : Destination()
    data object Installed : Destination()
    data object Blacklist : Destination()

    data object Settings : Destination()
    data object InstallationPreference : Destination()
    data object Installer : Destination()
    data object NetworkPreference : Destination()
    data object Dispenser : Destination()
    data object UIPreference : Destination()
    data object NotificationPreference : Destination()
    data object UpdatesPreference : Destination()
    data object SourceFilters : Destination()
    data object SecurityPreference : Destination()
}
