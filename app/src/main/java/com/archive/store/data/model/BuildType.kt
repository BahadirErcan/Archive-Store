package com.archive.store.data.model

import com.archive.store.BuildConfig

/**
 * Class representing build types for Archive Store
 */
enum class BuildType(val packageName: String) {
    RELEASE("com.archive.store"),
    NIGHTLY("com.archive.store.nightly"),
    DEBUG("com.archive.store.debug");

    companion object {

        /**
         * Returns current build type
         */
        @Suppress("KotlinConstantConditions")
        val CURRENT: BuildType
            get() = when (BuildConfig.BUILD_TYPE) {
                "release" -> RELEASE
                "nightly" -> NIGHTLY
                else -> DEBUG
            }

        /**
         * Returns package names for all possible build types
         */
        val PACKAGE_NAMES: List<String>
            get() = BuildType.entries.map { it.packageName }
    }
}
