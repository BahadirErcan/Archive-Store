/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.compose.ui.preferences.installation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.archive.store.R
import com.archive.store.compose.composable.InstallerListItem
import com.archive.store.compose.composable.TopAppBar
import com.archive.store.compose.preview.ThemePreviewProvider
import com.archive.store.compose.ui.commons.MicroGInstallerPrerequisiteDialog
import com.archive.store.data.installer.AppInstaller
import com.archive.store.data.installer.SessionInstaller
import com.archive.store.data.model.Installer
import com.archive.store.data.model.InstallerInfo
import com.archive.store.viewmodel.preferences.InstallerViewModel

@Composable
fun InstallerScreen(viewModel: InstallerViewModel = hiltViewModel()) {
    val currentInstallerId by viewModel.currentInstaller.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        viewModel.error.collect { error ->
            snackBarHostState.showSnackbar(message = error)
        }
    }

    ScreenContent(
        snackBarHostState = snackBarHostState,
        currentInstaller = Installer.entries[currentInstallerId],
        availableInstallers = AppInstaller.getAvailableInstallersInfo(LocalContext.current),
        onInstallerSelected = { installer -> viewModel.save(installer) }
    )
}

@Composable
private fun ScreenContent(
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
    currentInstaller: Installer = Installer.SESSION,
    availableInstallers: List<InstallerInfo> = emptyList(),
    onInstallerSelected: (installer: Installer) -> Unit = {}
) {
    val snackBarHostState = remember { snackBarHostState }
    var showMicroGPrerequisite by remember { mutableStateOf(false) }

    if (showMicroGPrerequisite) {
        MicroGInstallerPrerequisiteDialog(
            onConfirm = {
                showMicroGPrerequisite = false
                onInstallerSelected(Installer.MICROG)
            },
            onDismiss = { showMicroGPrerequisite = false }
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopAppBar(
                title = stringResource(R.string.pref_install_mode_title)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(vertical = dimensionResource(R.dimen.spacing_medium))
        ) {
            items(items = availableInstallers, key = { i -> i.id }) { installerInfo ->
                InstallerListItem(
                    installerInfo = installerInfo,
                    isSelected = installerInfo.installer == currentInstaller,
                    onClick = {
                        if (installerInfo.installer == Installer.MICROG) {
                            showMicroGPrerequisite = true
                        } else {
                            onInstallerSelected(installerInfo.installer)
                        }
                    }
                )
            }
        }
    }
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview
@Composable
private fun InstallerScreenPreview() {
    ScreenContent(
        availableInstallers = listOf(SessionInstaller.installerInfo)
    )
}
