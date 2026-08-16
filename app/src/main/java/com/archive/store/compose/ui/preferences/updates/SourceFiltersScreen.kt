/*
 * SPDX-FileCopyrightText: 2026 Archive OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.compose.ui.preferences.updates

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.archive.store.R
import com.archive.store.compose.composable.TopAppBar
import com.archive.store.compose.preview.ThemePreviewProvider
import com.archive.store.util.Preferences
import com.archive.store.util.Preferences.PREFERENCE_FILTER_ARCHIVE_ONLY
import com.archive.store.util.Preferences.PREFERENCE_FILTER_INSTALLERS
import com.archive.store.util.save
import com.archive.store.viewmodel.all.UpdatesViewModel

@Composable
fun SourceFiltersScreen(viewModel: UpdatesViewModel = hiltViewModel()) {
    ScreenContent(onCheckUpdatesNow = { viewModel.updateHelper.checkUpdatesNow() })
}

@Composable
private fun ScreenContent(onCheckUpdatesNow: () -> Unit = {}) {
    val context = LocalContext.current

    var archiveOnly by remember {
        mutableStateOf(Preferences.getBoolean(context, PREFERENCE_FILTER_ARCHIVE_ONLY))
    }
    var installers by remember {
        mutableStateOf(Preferences.getStringSet(context, PREFERENCE_FILTER_INSTALLERS))
    }
    var showAddDialog by remember { mutableStateOf(false) }

    // Trigger a fresh check on the way out so removed/added installers take effect
    // without waiting for the next periodic run.
    DisposableEffect(Unit) {
        onDispose { onCheckUpdatesNow() }
    }

    if (showAddDialog) {
        AddInstallerDialog(
            existing = installers,
            onAdd = { name ->
                installers = installers + name
                context.save(PREFERENCE_FILTER_INSTALLERS, installers)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = stringResource(R.string.pref_source_filters_title)) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            item {
                ListItem(
                    modifier = Modifier.clickable {
                        archiveOnly = !archiveOnly
                        context.save(PREFERENCE_FILTER_ARCHIVE_ONLY, archiveOnly)
                    },
                    headlineContent = { Text(stringResource(R.string.source_filters_all)) },
                    supportingContent = {
                        Text(stringResource(R.string.pref_source_filters_desc_all))
                    },
                    trailingContent = {
                        Switch(
                            checked = archiveOnly,
                            onCheckedChange = { checked ->
                                archiveOnly = checked
                                context.save(PREFERENCE_FILTER_ARCHIVE_ONLY, checked)
                            }
                        )
                    }
                )
            }
            item { HorizontalDivider() }
            item {
                ListItem(
                    headlineContent = {
                        Text(stringResource(R.string.source_filters_installers_header))
                    }
                )
            }
            if (installers.isEmpty()) {
                item {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.source_filters_installers_empty),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            } else {
                items(items = installers.toList(), key = { it }) { name ->
                    ListItem(
                        headlineContent = { Text(name) },
                        trailingContent = {
                            IconButton(
                                onClick = {
                                    installers = installers - name
                                    context.save(PREFERENCE_FILTER_INSTALLERS, installers)
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_delete_forever),
                                    contentDescription = stringResource(
                                        R.string.source_filters_remove
                                    )
                                )
                            }
                        }
                    )
                }
            }
            item {
                ListItem(
                    modifier = Modifier.clickable { showAddDialog = true },
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = null
                        )
                    },
                    headlineContent = { Text(stringResource(R.string.source_filters_add)) }
                )
            }
        }
    }
}

@Composable
private fun AddInstallerDialog(
    existing: Set<String>,
    onAdd: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var value by remember { mutableStateOf("") }
    val trimmed = value.trim()
    val canAdd = trimmed.isNotEmpty() && trimmed !in existing

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.source_filters_add)) },
        text = {
            Column {
                OutlinedTextField(
                    value = value,
                    label = { Text(stringResource(R.string.source_filters_add_dialog_hint)) },
                    onValueChange = { value = it },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onAdd(trimmed) }, enabled = canAdd) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview
@Composable
private fun SourceFiltersScreenPreview() {
    ScreenContent()
}
