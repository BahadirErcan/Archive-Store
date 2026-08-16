/*
 * SPDX-FileCopyrightText: 2026 Archive OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.store.compose.composition

import androidx.compose.runtime.compositionLocalOf
import com.archive.store.data.model.NetworkStatus

/**
 * CompositionLocal carrying the current device network status. Provided once at the
 * activity root from a single [com.archive.store.data.providers.NetworkProvider] subscription,
 * so any screen can read `LocalNetworkStatus.current` without injecting the provider
 * or duplicating the flow collection.
 *
 * Uses [compositionLocalOf] (not static) so only readers recompose on change.
 */
val LocalNetworkStatus = compositionLocalOf { NetworkStatus.AVAILABLE }
