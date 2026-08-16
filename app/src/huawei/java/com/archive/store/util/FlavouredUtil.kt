package com.archive.store.util

import android.content.Context
import com.archive.extensions.isHuawei

object FlavouredUtil : IFlavouredUtil {

    override val defaultDispensers: Set<String> = emptySet()

    override fun promptMicroGInstall(context: Context): Boolean = isHuawei &&
        PackageUtil.hasSupportedAppGallery(context) &&
        !PackageUtil.isMicroGBundleInstalled(context)
}
