package com.archive.store.data.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller.EXTRA_UNARCHIVE_PACKAGE_NAME
import android.util.Log
import androidx.core.content.getSystemService
import com.archive.extensions.TAG
import com.archive.extensions.isVAndAbove
import com.archive.store.ArchiveApp
import com.archive.store.data.helper.DownloadHelper
import com.archive.store.data.providers.AccountProvider
import com.archive.store.util.NotificationUtil
import com.aurora.gplayapi.helpers.AppDetailsHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Triggers re-install/unarchive of a previously archived app on Android 15+ devices.
 */
@AndroidEntryPoint
class UnarchivePackageReceiver : BroadcastReceiver() {

    @Inject
    lateinit var appDetailsHelper: AppDetailsHelper

    @Inject
    lateinit var downloadHelper: DownloadHelper

    override fun onReceive(context: Context?, intent: Intent?) {
        if (isVAndAbove && context != null && intent?.action == Intent.ACTION_UNARCHIVE_PACKAGE) {
            val packageName = intent.getStringExtra(EXTRA_UNARCHIVE_PACKAGE_NAME)!!
            Log.i(TAG, "Received request to unarchive $packageName")

            ArchiveApp.scope.launch(Dispatchers.IO) {
                if (!AccountProvider.isLoggedIn(context)) {
                    Log.e(TAG, "Failed to authenticate user!")
                    with(context.getSystemService<NotificationManager>()!!) {
                        notify(
                            packageName.hashCode(),
                            NotificationUtil.getUnarchiveAuthNotification(context, packageName)
                        )
                    }
                    return@launch
                }

                val app = appDetailsHelper.getAppByPackageName(packageName)
                downloadHelper.enqueueApp(app)
            }
        }
    }
}
