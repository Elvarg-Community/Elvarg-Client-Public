package com.runescape.loginscreen.worlds

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.runescape.Client
import com.runescape.cache.graphics.ImageCache
import com.runescape.draw.Rasterizer2D
import com.runescape.engine.GameEngine
import com.runescape.engine.impl.MouseHandler
import com.runescape.loginscreen.LoginState
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import java.lang.reflect.Type
import java.util.*

object WorldManager {

    var CLIENT = OkHttpClient()
    var GSON: Gson = Gson()

    val loadOnline = false
    val worldJsonUrl = ""
    var worldsLoaded: Boolean = false
    var worlds: MutableList<WorldData> = emptyList<WorldData>().toMutableList()
    var worldSelectStatus = "Click to switch"
    var selectedWorld: WorldData? = null

    fun openWorldSectionScreen(switchView: Boolean = false) {
        worldSelectStatus = "Loading..."
        var worldContent = ""
        if (loadOnline) {
            val url: HttpUrl = worldJsonUrl.toHttpUrlOrNull()?.newBuilder()?.build()!!
            try {
                CLIENT.newCall(Request.Builder().url(url).build()).execute().use { res ->
                    if (res.body != null) {
                        worldContent = res.body!!.string()
                    } else {
                        worldSelectStatus = "Error"
                        worldsLoaded = false
                    }
                }
            } catch (e: IOException) {
                worldSelectStatus = "Error"
                worldsLoaded = false
            }
        } else {
            worldContent = File("./worldData.json").inputStream().reader().readText()
        }


        val types: Type = object : TypeToken<ArrayList<WorldData?>?>() {}.type
        worlds = GSON.fromJson<ArrayList<WorldData>>(worldContent, types)
        worldSelectStatus = "Click to switch"
        worldsLoaded = true
        if (switchView) {
            Client.loginScreen.loginState = LoginState.WORLD_SELECT
        }
    }

    fun renderWorldSelect() {
        Rasterizer2D.drawBox(0, 0, GameEngine.canvasWidth, GameEngine.canvasHeight, 0x000000)
        ImageCache.get(27).drawAdvancedSprite(0, 0)

        if (Client.instance.newclickInRegion(GameEngine.canvasWidth - 57, 4, 50, 16)) {
            Client.loginScreen.loginState = LoginState.LOGIN
        }

        val worldListX = 56
        val worldListY = 35

        var xOffset = 0
        var yOffset = 0

        worlds.forEachIndexed { index, world ->

            val worldButtonX = worldListX + xOffset
            val worldButtonY = worldListY + yOffset

            ImageCache.get(31).drawSprite(worldButtonX, worldButtonY)

            if(Client.instance.newclickInRegion(worldButtonX,worldButtonY,ImageCache.get(31))) {
                Client.server = world.ip
                Client.loginScreen.loginState = LoginState.LOGIN
            }

            if (Client.instance.mouseInRegion(worldButtonX, worldButtonY, ImageCache.get(31))) {
                drawTooltip(world)
            }

            Client.instance.newBoldFont.drawBasicString(world.name, worldButtonX + 1, worldButtonY + 14, 0x111111)

            Client.instance.newSmallFont.drawCenteredString(
                "?",
                worldButtonX + 37 + (37 / 2), worldButtonY + 15,
                0xFFFFFF, 0
            )

            if (index % 7 == 0) {
                yOffset += 19
                xOffset = 0
            } else {
                xOffset += 94
                yOffset = 0
            }
        }
    }

    fun drawTooltip(world: WorldData) {
        val height = (10 * world.description.split("<br>").size) + 4
        var width = 2
        world.description.split("<br>").forEach {
            val textWidth = Client.instance.smallText.getTextWidth(it)
            if (textWidth >= width) {
                width = textWidth
            }
        }

        width += 6

        val x = MouseHandler.mouseX - (width / 2)
        val y = MouseHandler.mouseY + 20

        Rasterizer2D.drawBoxOutline(x, y, width + 2, height + 2, 0x000000)
        Rasterizer2D.drawBox(x + 1, y + 1, width, height, 0xFFFFA0)
        world.description.split("<br>").forEachIndexed { index, text ->
            Client.instance.newSmallFont.drawCenteredString(text, x + (width / 2), y + 12 + (10 * index), 0x111111)
        }

    }

}