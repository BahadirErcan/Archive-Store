/*
 * SPDX-FileCopyrightText: 2026 Archive OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.compose.ui.archiveapps

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.archive.store.R
import com.archive.store.data.model.ArchiveAppItem
import com.archive.store.data.model.DownloadStatus
import com.archive.store.data.room.download.Download

/** The action an archive app card/detail can offer the user right now. */
internal enum class ArchiveAppAction {
    DOWNLOAD,
    REINSTALL,
    RETRY,
    PROGRESS,
    INSTALLING,
    INSTALLED,
    COMING_SOON
}

/** Maps a live [ArchiveAppItem] onto its current actionable state. */
internal fun archiveAppAction(item: ArchiveAppItem): ArchiveAppAction = when {
    !item.app.canDownload -> ArchiveAppAction.COMING_SOON
    item.download == null -> {
        if (item.isInstalled) ArchiveAppAction.REINSTALL else ArchiveAppAction.DOWNLOAD
    }

    else -> when (item.download.status) {
        DownloadStatus.QUEUED, DownloadStatus.PURCHASING, DownloadStatus.DOWNLOADING,
        DownloadStatus.VERIFYING -> ArchiveAppAction.PROGRESS

        DownloadStatus.INSTALLING -> ArchiveAppAction.INSTALLING
        DownloadStatus.INSTALLED -> ArchiveAppAction.INSTALLED
        DownloadStatus.COMPLETED -> ArchiveAppAction.REINSTALL
        DownloadStatus.FAILED, DownloadStatus.CANCELLED,
        DownloadStatus.UNAVAILABLE -> ArchiveAppAction.RETRY
    }
}

/** True when the action is a user-triggerable state (as opposed to progress/installed). */
internal val ArchiveAppAction.isActive: Boolean
    get() = this == ArchiveAppAction.DOWNLOAD ||
        this == ArchiveAppAction.REINSTALL ||
        this == ArchiveAppAction.RETRY

/** Download progress of the underlying row, 0..100. */
internal fun ArchiveAppAction.progressOf(download: Download?): Int =
    if (this == ArchiveAppAction.PROGRESS) (download?.progress ?: 0).coerceIn(0, 100) else 0

@Composable
internal fun ArchiveAppAction.label(): String = when (this) {
    ArchiveAppAction.DOWNLOAD -> stringResource(R.string.archive_download)
    ArchiveAppAction.REINSTALL -> stringResource(R.string.archive_reinstall)
    ArchiveAppAction.RETRY -> stringResource(R.string.action_retry)
    ArchiveAppAction.PROGRESS -> stringResource(R.string.status_downloading)
    ArchiveAppAction.INSTALLING -> stringResource(R.string.status_installing)
    ArchiveAppAction.INSTALLED -> stringResource(R.string.title_installed)
    ArchiveAppAction.COMING_SOON -> stringResource(R.string.archive_coming_soon)
}
