package com.example.homehealth.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

fun compressImageToBase64(
    context: Context,
    uri: Uri,
    maxSizeKb: Int = 300
): String {
    val inputStream = context.contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream)

    var quality = 90
    val outputStream = ByteArrayOutputStream()

    do {
        outputStream.reset()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        quality -= 10
    } while (outputStream.size() / 1024 > maxSizeKb && quality > 20)

    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}
