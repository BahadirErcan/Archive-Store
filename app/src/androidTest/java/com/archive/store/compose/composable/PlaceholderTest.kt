/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.compose.composable

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.archive.store.IsolatedTest
import com.archive.store.R
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaceholderTest : IsolatedTest() {

    @Test
    fun testPlaceholderWithoutActionHandling() {
        setContent {
            Placeholder(
                painter = painterResource(R.drawable.ic_apps_outage),
                message = "An error occurred!",
                actionLabel = "Retry"
            )
        }

        composeTestRule.onNodeWithText("An error occurred!")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Retry")
            .assertDoesNotExist()
    }

    @Test
    fun testPlaceholderActionClick() {
        var clicked = false
        setContent {
            Placeholder(
                painter = painterResource(R.drawable.ic_apps_outage),
                message = "An error occurred!",
                actionLabel = "Retry",
                onAction = { clicked = true }
            )
        }

        composeTestRule.onNodeWithText("Retry")
            .assertIsDisplayed()
            .performClick()

        assertTrue(clicked)
    }
}
