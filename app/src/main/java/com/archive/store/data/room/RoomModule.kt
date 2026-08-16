package com.archive.store.data.room

import android.content.Context
import androidx.room.Room
import com.archive.store.data.room.MigrationHelper.MIGRATION_10_11
import com.archive.store.data.room.MigrationHelper.MIGRATION_11_12
import com.archive.store.data.room.MigrationHelper.MIGRATION_1_2
import com.archive.store.data.room.MigrationHelper.MIGRATION_2_3
import com.archive.store.data.room.MigrationHelper.MIGRATION_3_4
import com.archive.store.data.room.MigrationHelper.MIGRATION_4_5
import com.archive.store.data.room.MigrationHelper.MIGRATION_5_6
import com.archive.store.data.room.MigrationHelper.MIGRATION_6_7
import com.archive.store.data.room.MigrationHelper.MIGRATION_7_8
import com.archive.store.data.room.MigrationHelper.MIGRATION_8_9
import com.archive.store.data.room.MigrationHelper.MIGRATION_9_10
import com.archive.store.data.room.account.AccountConverter
import com.archive.store.data.room.account.AccountDao
import com.archive.store.data.room.account.AppAccountBindingDao
import com.archive.store.data.room.download.DownloadConverter
import com.archive.store.data.room.download.DownloadDao
import com.archive.store.data.room.exodus.TrackerDao
import com.archive.store.data.room.favourite.FavouriteDao
import com.archive.store.data.room.review.ReviewDao
import com.archive.store.data.room.update.IgnoredUpdateDao
import com.archive.store.data.room.update.UpdateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    private const val DATABASE = "archive_database"

    @Singleton
    @Provides
    fun providesRoomInstance(
        @ApplicationContext context: Context,
        downloadConverter: DownloadConverter,
        accountConverter: AccountConverter
    ): ArchiveDatabase = Room.databaseBuilder(context, ArchiveDatabase::class.java, DATABASE)
        .addMigrations(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5,
            MIGRATION_5_6,
            MIGRATION_6_7,
            MIGRATION_7_8,
            MIGRATION_8_9,
            MIGRATION_9_10,
            MIGRATION_10_11,
            MIGRATION_11_12
        )
        .addTypeConverter(downloadConverter)
        .addTypeConverter(accountConverter)
        .build()

    @Provides
    fun providesDownloadDao(archiveDatabase: ArchiveDatabase): DownloadDao =
        archiveDatabase.downloadDao()

    @Provides
    fun providesFavouriteDao(archiveDatabase: ArchiveDatabase): FavouriteDao =
        archiveDatabase.favouriteDao()

    @Provides
    fun providesUpdateDao(archiveDatabase: ArchiveDatabase): UpdateDao = archiveDatabase.updateDao()

    @Provides
    fun providesIgnoredUpdateDao(archiveDatabase: ArchiveDatabase): IgnoredUpdateDao =
        archiveDatabase.ignoredUpdateDao()

    @Provides
    fun providesReviewDao(archiveDatabase: ArchiveDatabase): ReviewDao = archiveDatabase.reviewDao()

    @Provides
    fun providesAccountDao(archiveDatabase: ArchiveDatabase): AccountDao =
        archiveDatabase.accountDao()

    @Provides
    fun providesAppAccountBindingDao(archiveDatabase: ArchiveDatabase): AppAccountBindingDao =
        archiveDatabase.appAccountBindingDao()

    @Provides
    fun providesTrackerDao(archiveDatabase: ArchiveDatabase): TrackerDao =
        archiveDatabase.trackerDao()
}
