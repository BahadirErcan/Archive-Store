package com.archive.store.data.room.favourite

import com.archive.store.BuildConfig
import kotlinx.serialization.Serializable

@Serializable
data class ImportExport(
    val favourites: List<Favourite>,
    val archiveStoreVersion: Int = BuildConfig.VERSION_CODE
)
