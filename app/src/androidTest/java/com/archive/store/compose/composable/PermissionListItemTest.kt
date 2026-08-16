/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.compose.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithText
import com.archive.store.IsolatedTest
import com.archive.store.R
import com.archive.store.data.model.Permission
import com.archive.store.data.model.PermissionType
import org.junit.Test

class PermissionListItemTest : IsolatedTest() {

    private val permission: Permission
        @Composable
        get() = Permission(
            PermissionType.STORAGE_MANAGER,
            stringResource(R.string.onboarding_permission_esm),
            stringResource(R.string.onboarding_permission_esa_desc)
        )

    @Test
    fun testPermissionNotGranted() {
        setContent {
            PermissionListItem(permission = permission.copy(isGranted = false))
        }

        composeTestRule.onNodeWithText("Grant")
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun testPermissionGranted() {
        setContent {
            PermissionListItem(permission = permission.copy(isGranted = true))
        }

        composeTestRule.onNodeWithText("Granted")
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }
}
