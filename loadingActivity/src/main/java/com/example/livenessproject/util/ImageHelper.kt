package com.example.livenessproject.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.widget.ImageView
import java.io.ByteArrayOutputStream

class ImageHelper {
    companion object {
        fun imageViewToBase64(imageView: ImageView): String {
            var bitmap = (imageView.drawable as BitmapDrawable).bitmap
            while(bitmap.width > 1300) {
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, false)
            }
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream)
            val byteArray = stream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.NO_WRAP) //NO_WRAP because the string will be used in json, which does not recognize \n
        }

        fun ExtendedBase64ToBitmap(extendedBase64: String): Bitmap {
            val photoData = extendedBase64.substring(extendedBase64.indexOf(",") + 1)
            val decodedString = Base64.decode(photoData, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }
    }
}