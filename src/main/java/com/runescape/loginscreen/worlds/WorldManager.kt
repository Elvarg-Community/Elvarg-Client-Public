package com.runescape.loginscreen.worlds

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.runescape.Client
import com.runescape.cache.graphics.ImageCache
import com.runescape.draw.Rasterizer2D
import com.runescape.engine.GameEngine
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
    var worldsLoaded : Boolean = false
    var worlds : MutableList<WorldData> = emptyList<WorldData>().toMutableList()
    var worldSelectStatus = "Click to switch"
    var selectedWorld : WorldData? = null
    val WORLDS_PER_PAGE = 168

    fun openWorldSectionScreen(switchView : Boolean = false) {
        worldSelectStatus = "Loading..."
        var worldContent = ""
        if(loadOnline) {
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
        Rasterizer2D.drawBox(0,0, GameEngine.canvasWidth, GameEngine.canvasHeight,0x000000)
        ImageCache.get(27).drawAdvancedSprite(0,0)

        Rasterizer2D.drawBox(59,35,646,456,0xFFFFF)

        val worldListX = 56
        val worldListY = 35;

        val pages = worlds.size / WORLDS_PER_PAGE
        val currentPage = 0
        val currentIndex = WORLDS_PER_PAGE * currentPage


        val startIndex = if(currentPage == 0) 0 else WORLDS_PER_PAGE * currentPage
        val endIndex = if(currentPage == 0) WORLDS_PER_PAGE else (WORLDS_PER_PAGE * currentPage) + WORLDS_PER_PAGE

        for(index in startIndex until endIndex) {
            if (worlds.getOrNull(index) != null) {
                val world = worlds[index]

                val worldButtonX = worldListX
                val worldButtonY = worldListY

                ImageCache.get(31).drawSprite(worldButtonX,worldButtonY)

                Client.instance.newBoldFont.drawBasicString(world.name, worldButtonX + 1,worldButtonY + 14, 0x000000)

                Client.instance.newSmallFont.drawCenteredString("?",
                    worldButtonX + 37 + (37 / 2),worldButtonY + 15,
                    0xFFFFFF,0
                )

            }
        }

    }

}