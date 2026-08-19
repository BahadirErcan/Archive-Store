/*
 * SPDX-FileCopyrightText: 2026 Archive OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.data.model

import com.archive.store.data.room.download.Download

/**
 * An archived app combined with its live download record so the UI can reflect
 * downloading / installing / installed states without re-fetching the catalogue.
 */
data class ArchiveAppItem(
    val app: ArchiveApp,
    val download: Download? = null,
    val isInstalled: Boolean = false
)
