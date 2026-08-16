/*
 * SPDX-FileCopyrightText: 2026 Archive OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.data.room.exodus

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exodus_tracker")
data class TrackerEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val url: String,
    val signature: String,
    val date: String,
    val categories: String
)
