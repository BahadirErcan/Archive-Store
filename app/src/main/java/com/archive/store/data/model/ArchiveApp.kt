/*
 * SPDX-FileCopyrightText: 2026 Archive OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An archived app entry served by the Archive Apps website (`apps.json`).
 *
 * All asset fields are web-relative paths resolved against the site base URL
 * ([com.archive.Constants.URL_ARCHIVE_APPS]) by the repository. A blank [apk] or
 * [comingSoon] marks an app that has not been published yet.
 */
@Parcelize
@Serializable
data class ArchiveApp(
    val id: Int = 0,
    val name: String = "",
    @SerialName("package_name") val packageName: String = "",
    @SerialName("version_name") val versionName: String = "",
    @SerialName("version_code") val versionCode: Long = 0,
    val size: Long = 0,
    val sha256: String = "",
    @SerialName("updated_on") val updatedOn: String = "",
    val icon: String = "",
    val description: String = "",
    val features: String = "",
    val changelog: String = "",
    val screenshots: List<String> = emptyList(),
    val apk: String = "",
    @SerialName("coming_soon") val comingSoon: Boolean = false
) : Parcelable {
    val canDownload: Boolean
        get() = !comingSoon && apk.isNotBlank()
}

@Serializable
data class ArchiveAppList(val apps: List<ArchiveApp> = emptyList())
