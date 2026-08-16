/*
 * SPDX-FileCopyrightText: 2025 Archive OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.data.model

data class ExternalItem(
    val packageName: String,
    val displayName: String,
    val iconURL: String,
    val size: Long,
    val status: InstallStatus,
    val progress: Int = 0,
    val speed: Long = 0L,
    val timeRemaining: Long = -1L
)
