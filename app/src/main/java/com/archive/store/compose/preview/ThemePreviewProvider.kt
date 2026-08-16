/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.compose.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import coil3.compose.LocalAsyncImagePreviewHandler
import com.archive.store.compose.theme.ArchiveTheme

/**
 * Preview provider for default theme and remote image handling
 */
class ThemePreviewProvider : PreviewWrapperProvider {

    @Composable
    override fun Wrap(content: @Composable (() -> Unit)) {
        ArchiveTheme {
            CompositionLocalProvider(
                value = LocalAsyncImagePreviewHandler provides coilPreviewProvider,
                content = content
            )
        }
    }
}
