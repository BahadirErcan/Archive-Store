/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.compose.composable

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.archive.store.IsolatedTest
import org.junit.Test

class TopAppBarTest : IsolatedTest() {

    @Test
    fun testTitleWithNoNavigationAction() {
        setContent {
            TopAppBar(title = "About", showNavigationIcon = false)
        }

        composeTestRule.onNodeWithText("About")
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Back")
            .assertDoesNotExist()
    }
}
