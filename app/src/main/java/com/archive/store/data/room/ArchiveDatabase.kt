package com.archive.store.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.archive.store.data.room.account.Account
import com.archive.store.data.room.account.AccountConverter
import com.archive.store.data.room.account.AccountDao
import com.archive.store.data.room.account.AppAccountBinding
import com.archive.store.data.room.account.AppAccountBindingDao
import com.archive.store.data.room.download.Download
import com.archive.store.data.room.download.DownloadConverter
import com.archive.store.data.room.download.DownloadDao
import com.archive.store.data.room.exodus.TrackerDao
import com.archive.store.data.room.exodus.TrackerEntity
import com.archive.store.data.room.favourite.Favourite
import com.archive.store.data.room.favourite.FavouriteDao
import com.archive.store.data.room.review.LocalReview
import com.archive.store.data.room.review.ReviewDao
import com.archive.store.data.room.update.IgnoredUpdate
import com.archive.store.data.room.update.IgnoredUpdateDao
import com.archive.store.data.room.update.Update
import com.archive.store.data.room.update.UpdateDao

@Database(
    entities = [
        Download::class,
        Favourite::class,
        Update::class,
        IgnoredUpdate::class,
        LocalReview::class,
        Account::class,
        AppAccountBinding::class,
        TrackerEntity::class
    ],
    version = 12,
    exportSchema = true
)
@TypeConverters(DownloadConverter::class, AccountConverter::class)
abstract class ArchiveDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao
    abstract fun favouriteDao(): FavouriteDao
    abstract fun updateDao(): UpdateDao
    abstract fun ignoredUpdateDao(): IgnoredUpdateDao
    abstract fun reviewDao(): ReviewDao
    abstract fun accountDao(): AccountDao
    abstract fun appAccountBindingDao(): AppAccountBindingDao
    abstract fun trackerDao(): TrackerDao
}
