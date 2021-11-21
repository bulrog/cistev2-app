package com.bulrog59.ciste2dot0.camera.util

import android.graphics.ImageFormat
import android.graphics.YuvImage
import androidx.camera.core.ImageProxy
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

class ConvertPicture {
    companion object{
        private fun nV21toJPEG(nv21: ByteArray, width: Int, height: Int, quality: Int): ByteArray {
            val out = ByteArrayOutputStream()
            val yuv = YuvImage(nv21, ImageFormat.NV21, width, height, null)
            yuv.compressToJpeg(android.graphics.Rect(0, 0, width, height), quality, out)
            return out.toByteArray()
        }

        private fun yUV420toNV21(imageProxy: ImageProxy): ByteArray {
            val crop: android.graphics.Rect = imageProxy.cropRect
            val format: Int = imageProxy.format
            val width: Int = crop.width()
            val height: Int = crop.height()
            val planes: Array<ImageProxy.PlaneProxy> = imageProxy.planes
            val data = ByteArray(width * height * ImageFormat.getBitsPerPixel(format) / 8)
            val rowData = ByteArray(planes[0].rowStride)
            var channelOffset = 0
            var outputStride = 1
            for (i in planes.indices) {
                when (i) {
                    0 -> {
                        channelOffset = 0
                        outputStride = 1
                    }
                    1 -> {
                        channelOffset = width * height + 1
                        outputStride = 2
                    }
                    2 -> {
                        channelOffset = width * height
                        outputStride = 2
                    }
                }
                val buffer: ByteBuffer = planes[i].buffer
                val rowStride: Int = planes[i].rowStride
                val pixelStride: Int = planes[i].pixelStride
                val shift = if (i == 0) 0 else 1
                val w = width shr shift
                val h = height shr shift
                buffer.position(rowStride * (crop.top shr shift) + pixelStride * (crop.left shr shift))
                for (row in 0 until h) {
                    var length: Int
                    if (pixelStride == 1 && outputStride == 1) {
                        length = w
                        buffer[data, channelOffset, length]
                        channelOffset += length
                    } else {
                        length = (w - 1) * pixelStride + 1
                        buffer[rowData, 0, length]
                        for (col in 0 until w) {
                            data[channelOffset] = rowData[col * pixelStride]
                            channelOffset += outputStride
                        }
                    }
                    if (row < h - 1) {
                        buffer.position(buffer.position() + rowStride - length)
                    }
                }
            }
            return data
        }

        fun getPicture(imageProxy: ImageProxy): Mat {
            //Here the format is ImageFormat.YUV_420_888 (format 0x23=35) which is a "Multi-plane Android YUV 420 format"
            //when we take image capture then we get a JPEG (0x100=256)
            //so we need to convert the buffer to jpeg
            val jpg2 = nV21toJPEG(
                yUV420toNV21(imageProxy),
                imageProxy.width,
                imageProxy.height,
                100
            )
            val img = Imgcodecs.imdecode(MatOfByte(*jpg2), Imgcodecs.IMREAD_UNCHANGED)
            val rotatedImg = Mat()
            Core.rotate(img, rotatedImg, Core.ROTATE_90_CLOCKWISE)
            return rotatedImg
        }
    }


}