/* Copyright (c) Meta Platforms, Inc. and affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.example.sharetowhatsappstatus

import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.graphics.Color

object ColorUtils {
    val colorSaver = listSaver<Color?, Any>(
        save = { color ->
            if (color == null) emptyList<Color>()
            else
                listOf(color.red, color.green, color.blue, color.alpha)
        },
        restore = { values ->
            if (values.isNotEmpty())
                Color(
                    red = values[0] as Float,
                    green = values[1] as Float,
                    blue = values[2] as Float,
                    alpha = values[3] as Float,
                )
            else
                null
        }
    )

    // Extension function to convert Color to hex string
    fun Color.toHexString(): String {
        return "#%02X%02X%02X%02X".format(
            (alpha * 255).toInt(), (red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt()
        )
    }

    // Extension function to convert hex string to Color
    fun Color.Companion.fromHex(hex: String): Color {
        require(hex.length == 7 && hex[0] == '#') { "Invalid hex color format.  Must be #RRGGBB" }
        val r = hex.substring(1, 3).toInt(16) / 255f
        val g = hex.substring(3, 5).toInt(16) / 255f
        val b = hex.substring(5, 7).toInt(16) / 255f
        return Color(r, g, b)
    }
}
