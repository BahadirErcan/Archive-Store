/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.compose.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.archive.store.BuildConfig
import com.archive.store.data.room.favourite.Favourite
import com.archive.store.data.room.favourite.Favourite.Mode

/**
 * Preview provider for composable working with [Favourite]
 */
class FavouritePreviewProvider() : PreviewParameterProvider<Favourite> {

    override val values: Sequence<Favourite>
        get() = sequenceOf(
            Favourite(
                packageName = BuildConfig.APPLICATION_ID,
                displayName = "Archive Store",
                iconURL = "",
                added = 0L,
                mode = Mode.MANUAL
            )
        )
}
