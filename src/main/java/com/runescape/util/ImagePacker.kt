package com.runescape.util

import com.runescape.Client
import com.runescape.sign.SignLink
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import java.util.zip.GZIPOutputStream
import javax.imageio.ImageIO


object ImagePacker {

    const val REPACK_SPRITES = false

    val validExtensions = listOf("png","jpg")

    fun init() {

        var bytesWriten = 0

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

            val pack = packRaw(bytes)
            bytesWriten += pack.size;
            Client.instance.indices[5].writeFile(pack.size, pack, index)

            println("Packing Sprite $index")
        }

        println("Bytes Written ${bytesWriten}")

    }

    private fun packRaw(b: ByteArray): ByteArray {
        val baos = ByteArrayOutputStream()
        val zos = GZIPOutputStream(baos)
        zos.write(b)
        zos.close()
        return baos.toByteArray()
    }

}

fun main() {
    ImagePacker.init()
}