/* Copyright (c) Meta Platforms, Inc. and affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.example.sharetowhatsappstatus

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sharetowhatsappstatus.ColorUtils.toHexString

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainApp()
        }
    }

    // selection
    var backgroundMedia by mutableStateOf<Uri?>(null)
        private set
    var foregroundMedia by mutableStateOf<Uri?>(null)
        private set
    var bgColor by mutableStateOf<Color?>(null)
        private set
    var gradientTop by mutableStateOf<Color?>(null)
        private set
    var gradientBottom by mutableStateOf<Color?>(null)
        private set

    // media picker
    private val bgMediaPicker =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            backgroundMedia =
                it?.let { FileUtils().copyFileToInternalStorage(applicationContext, it) }
        }
    private val fgMediaPicker =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            foregroundMedia =
                it?.let { FileUtils().copyFileToInternalStorage(applicationContext, it) }
        }

    private fun dumpSelection() =
        """Selected parameters
            
            Foreground media : $foregroundMedia
            
            Background media: $backgroundMedia
            
            Background color: $bgColor
            
            Gradient top: $gradientTop
            
            Gradient bottom: $gradientBottom
        """.trimMargin()


    @Composable
    fun MainApp() {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                // Title
                Text(
                    text = "Whatsapp Status Example",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                MediaPickerSection()
                ColorPickerSection()

                var outputText by rememberSaveable { mutableStateOf("") }
                // Clear all button
                Button(
                    onClick = {
                        foregroundMedia = null
                        backgroundMedia = null
                        bgColor = null
                        gradientTop = null
                        gradientBottom = null
                        outputText = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                ) {
                    Text(text = "Clear Selection", color = Color.White)
                }

                // Selection view
                TextField(
                    value = outputText,
                    onValueChange = { /* Do not allow user edits */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Use weight to take up remaining space
                    label = { Text("Selected Data") },
                    readOnly = true,
                    singleLine = false,
                )

                // Fire request
                Button(
                    onClick = {
                        outputText = dumpSelection()
                        launchStatusIntent()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Post Status")
                }
            }
        }
    }

    @Composable
    fun ColorPickerSection() {

        var colorPickerDialogState by rememberSaveable {
            mutableStateOf(ColorPickerDialogState.NONE)
        }

        Button(
            onClick = { colorPickerDialogState = ColorPickerDialogState.BACKGROUND },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = bgColor ?: Color.White
            )
        ) {
            Text(text = "Pick Background Color")
        }

        Button(
            onClick = { colorPickerDialogState = ColorPickerDialogState.GRADIENT_TOP },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = gradientTop ?: Color.White
            )
        ) {
            Text(text = "Pick Gradient Top")
        }

        Button(
            onClick = { colorPickerDialogState = ColorPickerDialogState.GRADIENT_BOTTOM },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = gradientBottom ?: Color.White
            )
        ) {
            Text(text = "Pick Gradient Bottom")
        }

        if (colorPickerDialogState != ColorPickerDialogState.NONE) {
            ColorPickerDialog(
                onDialogClose = { colorPickerDialogState = ColorPickerDialogState.NONE },
                initialColor = getColor(colorPickerDialogState),
                onColorSelected = { color: Color ->
                    setColor(colorPickerDialogState, color)
                }
            )
        }
    }


    @Composable
    fun MediaPickerSection() {
        // Background media picker
        Button(
            onClick = {
                bgMediaPicker.launch(
                    PickVisualMediaRequest.Builder().setMediaType(
                        ActivityResultContracts.PickVisualMedia.ImageAndVideo
                    ).build()
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Pick BG Media")
        }
        // Foreground media picker
        Button(
            onClick = {
                fgMediaPicker.launch(
                    PickVisualMediaRequest.Builder().setMediaType(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    ).build()
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Pick FG Media")
        }
    }

    private fun getColor(state: ColorPickerDialogState): Color {
        val color = when (state) {
            ColorPickerDialogState.BACKGROUND -> bgColor
            ColorPickerDialogState.GRADIENT_TOP -> gradientTop
            ColorPickerDialogState.GRADIENT_BOTTOM -> gradientBottom
            else -> Color.White
        }

        return color ?: Color.White
    }

    private fun setColor(state: ColorPickerDialogState, color: Color) {
        when (state) {
            ColorPickerDialogState.BACKGROUND -> bgColor = color
            ColorPickerDialogState.GRADIENT_TOP -> gradientTop = color
            ColorPickerDialogState.GRADIENT_BOTTOM -> gradientBottom = color
            else -> {}
        }
    }

    private fun launchStatusIntent() {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            setData(Uri.parse(WHATSAPP_STATUS_DEEPLINK))
            setPackage(CONSUMER_PACKAGE_NAME)

            backgroundMedia?.let {
                putExtra(Intent.EXTRA_STREAM, it)
                grantUriPermission(
                    CONSUMER_PACKAGE_NAME,
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            foregroundMedia?.let {
                putExtra(FOREGROUND_MEDIA_URI_EXTRA_KEY, it)
                grantUriPermission(
                    CONSUMER_PACKAGE_NAME,
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            putExtra(WHATSAPP_SHARE_TYPE_EXTRA_KEY, "SHARE_TO_STATUS")
            putExtra(SOURCE_APP_PACKAGE_NAME_EXTRA_KEY, packageName)
            bgColor?.let {
                putExtra(BACKGROUND_COLOR_EXTRA_KEY, it.toHexString())
            }

            gradientTop?.let {
                putExtra(BACKGROUND_GRADIENT_TOP_EXTRA_KEY, it.toHexString())
            }

            gradientBottom?.let {
                putExtra(BACKGROUND_GRADIENT_BOTTOM_EXTRA_KEY, it.toHexString())
            }
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        startActivityForResult(intent, 0)
    }

    companion object {
        enum class ColorPickerDialogState {
            NONE,
            BACKGROUND,
            GRADIENT_TOP,
            GRADIENT_BOTTOM
        }

        private const val WHATSAPP_STATUS_DEEPLINK: String = "https://wa.me/status"
        private const val WHATSAPP_SHARE_TYPE_EXTRA_KEY: String = "share_type"
        private const val BACKGROUND_COLOR_EXTRA_KEY = "background_color"
        private const val BACKGROUND_GRADIENT_TOP_EXTRA_KEY = "color_gradient_top"
        private const val BACKGROUND_GRADIENT_BOTTOM_EXTRA_KEY = "color_gradient_bottom"
        private const val SOURCE_APP_NAME_EXTRA_KEY = "source_app_name"
        private const val SOURCE_APP_PACKAGE_NAME_EXTRA_KEY = "source_app_package_name"
        private const val FOREGROUND_MEDIA_URI_EXTRA_KEY = "foreground_media"
        private const val CONSUMER_PACKAGE_NAME = "com.whatsapp"
    }
}
