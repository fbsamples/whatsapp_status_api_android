/* Copyright (c) Meta Platforms, Inc. and affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.example.sharetowhatsappstatus

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.example.sharetowhatsappstatus.ColorUtils.colorSaver
import com.example.sharetowhatsappstatus.ColorUtils.fromHex
import com.example.sharetowhatsappstatus.ColorUtils.toHexString
import kotlinx.coroutines.launch

@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDialogClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDialogClose,
        title = { Text("Choose a Color") },
        modifier = Modifier.padding(10.dp),
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ColorButton(
                        color = Color.Red,
                        onColorSelected = onColorSelected,
                        initialColor = initialColor
                    )
                    ColorButton(
                        color = Color.Green,
                        onColorSelected = onColorSelected,
                        initialColor = initialColor
                    )
                    ColorButton(
                        color = Color.Blue,
                        onColorSelected = onColorSelected,
                        initialColor = initialColor
                    )
                    ColorButton(
                        color = Color.Yellow,
                        onColorSelected = onColorSelected,
                        initialColor = initialColor
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ColorButton(
                        color = Color.Cyan,
                        onColorSelected = onColorSelected,
                        initialColor = initialColor
                    )
                    ColorButton(
                        color = Color.Magenta,
                        onColorSelected = onColorSelected,
                        initialColor = initialColor
                    )
                    ColorButton(
                        color = Color.LightGray,
                        onColorSelected = onColorSelected,
                        initialColor = initialColor
                    )
                    ColorButton(
                        color = Color.DarkGray,
                        onColorSelected = onColorSelected,
                        initialColor = initialColor
                    )
                }
                var manualColor by rememberSaveable { mutableStateOf(initialColor.toHexString()) }
                val scope = rememberCoroutineScope()
                var backgroundColor by rememberSaveable(stateSaver = colorSaver) {
                    mutableStateOf(
                        Color.White
                    )
                }


                OutlinedTextField(
                    value = manualColor,
                    onValueChange = { manualColor = it },
                    label = { Text("Hex Color (e.g., #RRGGBB)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        try {
                            val newColor = Color.fromHex(manualColor)
                            onColorSelected(newColor)
                            backgroundColor = newColor
                            scope.launch {
                                onDialogClose()
                            }

                        } catch (e: IllegalArgumentException) {
                            // Handle invalid color format (e.g., show a toast)
                            Log.e("ColorPicker", "Invalid color format: ${e.message}")
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)

                ) {
                    Text("Set Color")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDialogClose) {
                Text("Done")
            }
        }
    )
}

@Composable
fun ColorButton(color: Color, onColorSelected: (Color) -> Unit, initialColor: Color) {
    Button(
        onClick = { onColorSelected(color) },
        colors = ButtonDefaults.buttonColors(backgroundColor = color),
        modifier = Modifier
            .size(40.dp)
            .padding(1.dp),
    ) {
        if (color == initialColor) {
            Text("✓", color = if (color.luminance() > 0.5) Color.Black else Color.White)
        }

    }
}
