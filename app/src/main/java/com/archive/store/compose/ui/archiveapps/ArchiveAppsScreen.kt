/*
 * SPDX-FileCopyrightText: 2026 Archive OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.compose.ui.archiveapps

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.archive.store.R
import com.archive.store.compose.composable.Placeholder

@Composable
fun ArchiveAppsScreen() {
    Placeholder(
        painter = painterResource(R.mipmap.ic_launcher_monochrome),
        message = stringResource(R.string.title_archive_apps)
    )
}