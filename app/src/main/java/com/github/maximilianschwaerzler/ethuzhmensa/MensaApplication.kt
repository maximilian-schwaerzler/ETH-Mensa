/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * The application class for initializing Hilt dependency injection.
 */
@HiltAndroidApp
class MensaApplication : Application()