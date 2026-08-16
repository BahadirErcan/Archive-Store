/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.compose.ui.spoof.navigation

import androidx.annotation.StringRes
import com.archive.store.R

/**
 * Pages that are shown in SpoofScreen
 */
enum class SpoofPage(@StringRes val localized: Int) {
    DEVICE(R.string.title_device),
    LOCALE(R.string.title_language)
}
