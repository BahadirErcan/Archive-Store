/*
 * SPDX-FileCopyrightText: 2026 Archive OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.data.model

import android.graphics.Bitmap
import com.archive.store.compose.ui.commons.InstalledAppMeta

data class BlacklistAppItem(
    override val packageName: String,
    val displayName: String,
    val versionName: String,
    val versionCode: Long,
    val icon: Bitmap,
    val isFiltered: Boolean,
    override val firstInstallTime: Long = 0L,
    override val lastUpdateTime: Long = 0L,
    override val sizeBytes: Long = 0L,
    override val isSystem: Boolean = false,
    override val installer: String? = null
) : InstalledAppMeta {
    override val label: String get() = displayName
}
