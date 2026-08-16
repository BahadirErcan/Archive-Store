/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.archive.extensions

import com.archive.Constants.PACKAGE_NAME_GMS
import com.aurora.gplayapi.data.models.App

fun App.requiresGMS() = dependencies.dependentPackages.contains(PACKAGE_NAME_GMS)
