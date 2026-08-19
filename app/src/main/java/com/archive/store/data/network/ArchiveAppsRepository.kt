/*
 * SPDX-FileCopyrightText: 2026 Archive OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.data.network

import com.archive.Constants
import com.archive.store.data.model.ArchiveApp
import com.archive.store.data.model.ArchiveAppList
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json

/**
 * Resolves a web-relative Archive Apps asset path against the site base URL. Absolute URLs
 * pass through unchanged. Shared by the repository and the UI so icon/text URLs agree.
 */
fun resolveArchiveAsset(path: String): String =
    if (path.startsWith("http")) path else "${Constants.URL_ARCHIVE_APPS}/$path"

/**
 * Fetches the archived app catalogue and its assets from the Archive Apps website.
 *
 * `apps.json` and every referenced asset are addressed with web-relative paths which are
 * resolved against [Constants.URL_ARCHIVE_APPS] here, keeping the site relocatable.
 */
@Singleton
class ArchiveAppsRepository @Inject constructor(
    private val httpClient: HttpClient,
    private val json: Json
) {

    /**
     * Downloads and parses the app catalogue.
     * @throws IOException when the request fails or the payload is unusable
     */
    suspend fun fetchApps(): List<ArchiveApp> {
        val response = httpClient.call("${Constants.URL_ARCHIVE_APPS}/apps.json")
        response.use {
            if (!it.isSuccessful) throw IOException("apps.json fetch failed: HTTP ${it.code}")
            val body = it.body?.string() ?: throw IOException("Empty apps.json response")
            return runCatching { json.decodeFromString<ArchiveAppList>(body).apps }
                .getOrElse { throw IOException("Failed to parse apps.json", it) }
        }
    }

    /**
     * Fetches a plain-text asset (description, changelog). Returns an empty string when the
     * resource is missing so callers don't have to handle nulls.
     */
    suspend fun fetchText(path: String): String {
        if (path.isBlank()) return ""
        val response = httpClient.call(resolve(path))
        return response.use { if (it.isSuccessful) it.body?.string().orEmpty() else "" }
    }

    /**
     * Fetches the features list (a JSON array of strings). Falls back to an empty list when
     * the resource is missing or malformed.
     */
    suspend fun fetchFeatures(path: String): List<String> {
        if (path.isBlank()) return emptyList()
        val response = httpClient.call(resolve(path))
        return response.use {
            if (!it.isSuccessful) return@use emptyList()
            val body = it.body?.string() ?: return@use emptyList()
            runCatching { json.decodeFromString<List<String>>(body) }.getOrDefault(emptyList())
        }
    }

    /** Resolves a web-relative path against the site base URL; absolute URLs pass through. */
    fun resolve(path: String): String = resolveArchiveAsset(path)
}
