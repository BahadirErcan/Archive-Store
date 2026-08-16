package com.archive.store.util

import android.content.Context
import com.archive.Constants

object FlavouredUtil : IFlavouredUtil {

    override val defaultDispensers = setOf(Constants.URL_DISPENSER)

    override fun promptMicroGInstall(context: Context): Boolean = false
}
