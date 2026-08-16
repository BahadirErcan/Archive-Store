/*
 * SPDX-FileCopyrightText: 2025 Archive OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.data

data class PageResult<T>(
    val items: List<T>,
    val hasMore: Boolean = items.isNotEmpty()
)
