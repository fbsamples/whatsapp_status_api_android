/* Copyright (c) Meta Platforms, Inc. and affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.example.sharetowhatsappstatus

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class FileUtils {
    private fun getFileName(contentResolver: ContentResolver, uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameColumnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameColumnIndex != -1) {
                    return it.getString(displayNameColumnIndex)
                }
            }
        }
        return uri.lastPathSegment!!
    }

    fun copyFileToInternalStorage(context: Context, uri: Uri): Uri {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Failed to open input stream")

        val file = File(context.filesDir, getFileName(contentResolver, uri))
        val outputStream = FileOutputStream(file)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        val contentUri =
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        return Uri.parse("content://${contentUri.authority}${contentUri.path}")
    }
}
