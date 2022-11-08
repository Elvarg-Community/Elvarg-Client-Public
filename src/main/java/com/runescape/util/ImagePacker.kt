package com.runescape.util

import com.runescape.Client
import com.runescape.sign.SignLink
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.GZIPOutputStream
import javax.imageio.ImageIO


object ImagePacker {

    private const val REPACK_SPRITES = true

    private val validExtensions = listOf("png","jpg")

    fun init() {

        var bytesWritten = 0

        val imageLocation = File(SignLink.findcachedir() + "/index6/")

        if (!REPACK_SPRITES) return

        imageLocation.listFiles()!!.filter {
                file -> validExtensions.contains(file.extension)
        }.forEach {
            val image = ImageIO.read(it.inputStream())
            val index = it.nameWithoutExtension.toInt()

            val baos = ByteArrayOutputStream()
            ImageIO.write(image, "png", baos)
            val bytes = baos.toByteArray()

            val pack = toGzip(bytes)
            bytesWritten += pack.size;
            Client.instance.indices[5].writeFile(pack.size, pack, index)

            println("Packing Sprite $index")
        }

        println("Bytes Written $bytesWritten")

    }

    private fun toGzip(b: ByteArray): ByteArray {
        val byteStream = ByteArrayOutputStream(b.size)
        byteStream.use { output ->
            val zipStream = GZIPOutputStream(output)
            zipStream.use { stream ->
                stream.write(b)
            }
        }
        return byteStream.toByteArray()
    }

}

fun main() {
    ImagePacker.init()
}