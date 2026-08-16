/*
 * SPDX-FileCopyrightText: 2026 Archive OSS
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.compose.composable

import android.text.format.DateUtils
import android.text.format.Formatter
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.archive.store.R
import com.archive.store.compose.composable.app.AnimatedAppIcon
import com.archive.store.compose.preview.AppPreviewProvider
import com.archive.store.compose.preview.ThemePreviewProvider
import com.archive.store.compose.theme.colorGreen
import com.archive.store.compose.theme.colorRed
import com.archive.store.data.model.DownloadStatus
import com.archive.store.data.room.download.Download
import com.archive.store.util.CommonUtil.getETAString
import com.aurora.gplayapi.data.models.App

@Composable
fun DownloadListItem(modifier: Modifier = Modifier, download: Download, onClick: () -> Unit = {}) {
    val context = LocalContext.current

    val statusLine = stringResource(download.status.localized)
    val tertiaryLine = if (download.status in DownloadStatus.running) {
        val progress = "${download.progress}%"
        val speed = "${Formatter.formatShortFileSize(context, download.speed)}/s"
        val eta = getETAString(context, download.timeRemaining)
        "$progress • $speed • $eta"
    } else {
        DateUtils.formatDateTime(context, download.downloadedAt, DateUtils.FORMAT_SHOW_DATE)
    }

    ArchiveListItem(
        modifier = modifier,
        headline = download.displayName,
        supporting = statusLine,
        tertiary = tertiaryLine,
        headlineStyle = MaterialTheme.typography.bodyMedium,
        onClick = onClick,
        leading = {
            AnimatedAppIcon(
                modifier = Modifier.requiredSize(dimensionResource(R.dimen.icon_size_medium)),
                iconUrl = download.iconURL,
                inProgress = download.isRunning,
                progress = download.progress.toFloat()
            )
        },
        trailing = when (download.status) {
            DownloadStatus.COMPLETED, DownloadStatus.INSTALLED -> {
                {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = stringResource(R.string.download_completed),
                        tint = colorGreen
                    )
                }
            }

            DownloadStatus.CANCELLED, DownloadStatus.FAILED
            -> {
                {
                    Icon(
                        painter = painterResource(R.drawable.ic_cancel),
                        contentDescription = stringResource(R.string.download_canceled),
                        tint = colorRed
                    )
                }
            }

            else -> {
                null
            }
        }
    )
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun DownloadListItemPreview(@PreviewParameter(AppPreviewProvider::class) app: App) {
    DownloadListItem(download = Download.fromApp(app))
}
