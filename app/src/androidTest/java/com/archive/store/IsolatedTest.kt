/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import com.archive.store.compose.theme.ArchiveTheme
import org.junit.Rule

/**
 * Class that provides helper methods to test isolated composable
 */
abstract class IsolatedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Sets given composable as content with default theme
     */
    fun setContent(content: @Composable () -> Unit) {
        composeTestRule.setContent {
            ArchiveTheme(content = content)
        }
    }
}
