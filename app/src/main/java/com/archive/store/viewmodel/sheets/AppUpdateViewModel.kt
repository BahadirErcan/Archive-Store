/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.viewmodel.sheets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.archive.store.data.AccountRepository
import com.archive.store.data.helper.DownloadHelper
import com.archive.store.data.helper.UpdateHelper
import com.archive.store.data.providers.BlacklistProvider
import com.archive.store.data.room.update.Update
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AppUpdateViewModel @Inject constructor(
    val blacklistProvider: BlacklistProvider,
    private val updateHelper: UpdateHelper,
    private val downloadHelper: DownloadHelper,
    accountRepository: AccountRepository
) : ViewModel() {

    val accounts = accountRepository.accounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun ignoreAllUpdates(packageName: String) {
        viewModelScope.launch { updateHelper.ignoreAll(packageName) }
    }

    fun ignoreVersion(packageName: String, versionCode: Long) {
        viewModelScope.launch { updateHelper.ignoreVersion(packageName, versionCode) }
    }

    /** Downloads this update using a chosen account, recording the per-app binding. */
    fun updateWithAccount(update: Update, accountId: String) {
        viewModelScope.launch { downloadHelper.enqueueUpdate(update, accountId) }
    }
}
