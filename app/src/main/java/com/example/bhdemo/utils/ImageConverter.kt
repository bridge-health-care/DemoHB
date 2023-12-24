package com.example.bhdemo.utils

import android.graphics.Bitmap
import java.nio.ByteBuffer

class ImageConverter {

    companion object {
        public fun toGrayscale(mImageBuffer: ByteArray, mImageWidth: Int, mImageHeight: Int): Bitmap? {
            val Bits = ByteArray(mImageBuffer.size * 4)
            for (i in mImageBuffer.indices) {
                Bits[i * 4 + 2] = mImageBuffer[i]
                Bits[i * 4 + 1] = Bits[i * 4 + 2]
                Bits[i * 4] = Bits[i * 4 + 1] // Invert the source bits
                Bits[i * 4 + 3] = -1 // 0xff, that's the alpha.
            }
            val bmpGrayscale = Bitmap.createBitmap(mImageWidth, mImageHeight, Bitmap.Config.ARGB_8888)
            //Bitmap bm contains the fingerprint img
            bmpGrayscale.copyPixelsFromBuffer(ByteBuffer.wrap(Bits))
            return bmpGrayscale
        }
    }
}