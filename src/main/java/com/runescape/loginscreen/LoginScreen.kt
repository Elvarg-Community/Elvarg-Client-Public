package com.runescape.loginscreen

import com.runescape.Client
import com.runescape.Configuration
import com.runescape.UserPreferences
import com.runescape.cache.graphics.ImageCache
import com.runescape.draw.Rasterizer2D
import com.runescape.engine.GameEngine
import com.runescape.engine.impl.KeyHandler
import com.runescape.loginscreen.worlds.WorldManager
import com.runescape.loginscreen.worlds.WorldManager.openWorldSectionScreen
import com.runescape.loginscreen.worlds.WorldManager.worldList
import com.runescape.util.MiscUtils
import com.runescape.util.StringUtils
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ObservableValue
import org.apache.commons.lang3.time.StopWatch
import kotlin.math.ceil
import kotlin.system.exitProcess

const val BACKGROUND_ACTIVE_TIME_SECONDS = 10

class LoginScreen(val client : Client) {


    fun setUp() {
        fadeToBlack.addListener { observable: ObservableValue<out Number>?, oldVal: Number?, newVal: Number ->
            if (newVal.toDouble() >= 256) {
                backgroundSprite = backgroundSprite()
                backgroundStopWatch!!.reset()
                backgroundStopWatch!!.start()
            }
            if (newVal.toDouble() <= 0) {
                fadeToBlack.set(256.0)
            }

        }
    }

    private var backgroundStopWatch: StopWatch? = null
    var loginState = LoginState.LOGIN
    var opacity = 0
    private var backgroundSprite = LoginBackground.NORMAL.spriteID

    private val eulaText = listOf(
        "Before using this app, please read and accept our",
        "@gol@terms of use@whi@, @gol@privacy policy@whi@, and @gol@end user licence",
        "@gol@agreement (EULA)@whi@.",
        "By accepting, you agree to these documents."
    )

    fun drawLogin() {
        if (WorldManager.selectedWorld == null) {
            openWorldSectionScreen()
            WorldManager.selectedWorld = worldList.first()
            WorldManager.selectedWorld = worldList.first()
        }

        val centerX = GameEngine.canvasWidth / 2
        val centerY = GameEngine.canvasHeight / 2
        val alpha = if (Client.preferences.loginBackground != LoginBackground.FADING_BACKGROUNDS) 226 else opacity
        handleBackgrounds()
        if (Client.preferences.loginBackground != LoginBackground.ANIMATED_GAME_WORLD) {
            Rasterizer2D.drawBox(0,0,GameEngine.canvasWidth,GameEngine.canvasHeight,0x000000)
            if (backgroundSprite != -1) {
                ImageCache.get(backgroundSprite).drawAdvancedSprite(centerX - (766 / 2),centerY - (503 / 2),alpha)
            }
            Client.loginScreenRunesAnimation.draw(centerX - (766 / 2) -15, 0,Client.tick,alpha)
            Client.loginScreenRunesAnimation.draw(GameEngine.canvasWidth - 110, 0,Client.tick,alpha)
        }
        ImageCache.get(0).drawAdvancedSprite(centerX - (444 / 2),centerY - (GameEngine.canvasHeight / 2) + 17)
        ImageCache.get(1).drawSprite(centerX - (360 / 2),centerY - (200 / 2) + 21)

        val loginBoxX = centerX - (360 / 2)
        val loginBoxY = centerY - (200 / 2) + 21

        ImageCache.get(if(Client.preferences.enableMusic) 25 else 26).drawAdvancedSprite(GameEngine.canvasWidth - 38 - 5,GameEngine.canvasHeight - 45 + 7)
        if(WorldManager.loadedWorlds) {
            ImageCache.get(3).drawAdvancedSprite(centerX - (GameEngine.canvasWidth / 2) + 5,GameEngine.canvasHeight - 45 + 8)
            client.newBoldFont.drawCenteredString("World: ${WorldManager.selectedWorld?.name}", centerX - (GameEngine.canvasWidth / 2) + 5 + (100 / 2),GameEngine.canvasHeight - 45 + 23,0xFFFFFF,1)
            client.newSmallFont.drawCenteredString(WorldManager.worldStatusText, centerX - (GameEngine.canvasWidth / 2) + 5 + (100 / 2),GameEngine.canvasHeight - 45 + 38,0xFFFFFF,1)

        }

        when(loginState) {
            LoginState.EULA -> {
                eulaText.forEachIndexed { index, line ->
                    client.newRegularFont.drawCenteredString(line,loginBoxX + (360 / 2), loginBoxY + 43 + (20 * index),0xFFFFFF,1)
                }
                listOf("Accept","Decline").forEachIndexed { index, buttonText ->
                    val buttonX = loginBoxX + 28 + if(index == 1) 160 else 0
                    ImageCache.get(2).drawSprite(buttonX,loginBoxY + 121)
                    client.newBoldFont.drawCenteredString(buttonText,buttonX + (147 / 2) - 1, loginBoxY + 146,0xFFFFFF,1)
                }
            }
            LoginState.WELCOME -> {

                client.newBoldFont.drawCenteredString("Welcome to ${Configuration.CLIENT_NAME}",loginBoxX + (360 / 2), loginBoxY + 82,0xFFFF00,1)

                listOf("New User","Existing User").forEachIndexed { index, buttonText ->
                    val buttonX = loginBoxX + 28 + if(index == 1) 160 else 0
                    ImageCache.get(2).drawSprite(buttonX - 1,loginBoxY + 100)
                    client.newBoldFont.drawCenteredString(buttonText,buttonX + (147 / 2) - 3, loginBoxY + 124,0xFFFFFF,1)
                }

            }
            LoginState.LOGIN -> {

                if (client.firstLoginMessage.isNotEmpty()) {
                    client.newBoldFont.drawCenteredString(client.firstLoginMessage, loginBoxX + (360 / 2), loginBoxY + 56 - 17, 0xFFFF00, 1)
                    client.newBoldFont.drawCenteredString(client.secondLoginMessage, loginBoxX + (360 / 2), loginBoxY + 70 - 17, 0xFFFF00, 1)
                } else {
                    client.newBoldFont.drawCenteredString(client.secondLoginMessage, loginBoxX + (360 / 2), loginBoxY + 63 - 17, 0xFFFF00, 1)
                }

                listOf("Login","Cancel").forEachIndexed { index, buttonText ->
                    val buttonX = loginBoxX + 28 + if(index == 1) 160 else 0
                    ImageCache.get(2).drawSprite(buttonX,loginBoxY + 131)
                    client.newBoldFont.drawCenteredString(buttonText,buttonX + (147 / 2) - 1, loginBoxY + 156,0xFFFFFF,1)
                }

                client.newBoldFont.drawBasicString("Login: ", loginBoxX + 70, loginBoxY + 83, 0xFFFFFF, 1)
                client.newBoldFont.drawBasicString("Password: ", loginBoxX + 72, loginBoxY + 98, 0xFFFFFF, 1)

                client.newBoldFont.drawBasicString(
                    (if(!Client.preferences.hiddenUsername) client.myUsername else StringUtils.passwordAsterisks(client.myUsername)) + flash(0),
                    loginBoxX + 110, loginBoxY + 83,
                    0xFFFFFF, 1
                )

                client.newBoldFont.drawBasicString(
                    StringUtils.passwordAsterisks(client.myPassword) + flash(1),
                    loginBoxX + 123 + 20, loginBoxY + 98,
                    0xFFFFFF, 1
                )
                ImageCache.get(if(!Client.preferences.rememberUsername) 21 else 23).drawHoverSprite(loginBoxX + 63,loginBoxY + 107,ImageCache.get(if(!Client.preferences.rememberUsername) 22 else 24))
                ImageCache.get(if(!Client.preferences.hiddenUsername) 21 else 23).drawHoverSprite(loginBoxX + 204,loginBoxY + 107,ImageCache.get(if(!Client.preferences.hiddenUsername) 22 else 24))

                client.newSmallFont.drawBasicString(
                    "Remember Username",
                    loginBoxX + 63 + 22, loginBoxY + 119,
                    0xFFFF00, 1
                )

                client.newSmallFont.drawBasicString(
                    "Hide Username",
                    loginBoxX + 204 + 22, loginBoxY + 119,
                    0xFFFF00, 1
                )

            }
            LoginState.WORLD_SELECT -> WorldManager.renderWorldSelect()
            else -> {}
        }

    }

    fun handleInput() {
        val centerX = GameEngine.canvasWidth / 2
        val centerY = GameEngine.canvasHeight / 2
        val loginBoxX = centerX - (360 / 2)
        val loginBoxY = centerY - (200 / 2) + 21

        if(client.newclickInRegion(centerX - (GameEngine.canvasWidth / 2) + 5,GameEngine.canvasHeight - 45 + 8,ImageCache.get(3))) {
            openWorldSectionScreen(true)
        }

        if(client.newclickInRegion(GameEngine.canvasWidth - 38 - 5,GameEngine.canvasHeight - 45 + 7,ImageCache.get(25))) {
            Client.preferences.enableMusic = !Client.preferences.enableMusic
        }

        when(loginState) {
            LoginState.EULA -> {
                repeat(2) {
                    val buttonX = loginBoxX + 28 + if(it == 1) 160 else 0
                    if(client.newclickInRegion(buttonX + (147 / 2) - 1, loginBoxY + 146,ImageCache.get(2))) {
                        when(it) {
                            0 -> {
                                loginState = LoginState.LOGIN
                                Client.preferences.eulaAccepted = true
                                UserPreferences.save()
                            }
                            1 -> exitProcess(0)
                        }
                    }
                }
            }
            LoginState.WELCOME -> {
                repeat(2) {
                    val buttonX = loginBoxX + 28 + if(it == 1) 160 else 0
                    if(client.newclickInRegion(buttonX - 1,loginBoxY + 100,ImageCache.get(2))) {
                        when(it) {
                            0 -> MiscUtils.launchURL("https://www.google.com/")
                            1 -> loginState = LoginState.LOGIN
                        }
                    }
                }
            }
            LoginState.LOGIN -> {
                repeat(2) {
                    val buttonX = loginBoxX + 28 + if(it == 1) 160 else 0
                    if(client.newclickInRegion(buttonX,loginBoxY + 131,ImageCache.get(2))) {
                        when(it) {
                            0 -> client.login(client.myUsername,client.myPassword, false)
                            1 -> loginState = LoginState.WELCOME
                        }
                    }
                }

                if(client.newclickInRegion(loginBoxX + 110, loginBoxY + 70,200,15)) {
                    client.loginScreenCursorPos = 0
                }

                if(client.newclickInRegion(loginBoxX + 140, loginBoxY + 87,200,15)) {
                    client.loginScreenCursorPos = 1
                }


                if(client.newclickInRegion(loginBoxX + 63,loginBoxY + 107,ImageCache.get(21))) {
                    Client.preferences.rememberUsername = !Client.preferences.rememberUsername
                }

                if(client.newclickInRegion(loginBoxX + 204,loginBoxY + 107,ImageCache.get(21))) {
                    Client.preferences.hiddenUsername = !Client.preferences.hiddenUsername
                }

                do {
                    val typed: Int = KeyHandler.instance.readChar()
                    if (typed == -1) break
                    var valid = false
                    for (key in validUserPassChars) {
                        if (typed != key.code) {
                            continue
                        }
                        valid = true
                        break
                    }

                    if (client.loginScreenCursorPos == 0) {
                        if (typed == 8 && client.myUsername.isNotEmpty()) client.myUsername = client.myUsername.substring(0, client.myUsername.length - 1)
                        if (typed == 9 || typed == 10 || typed == 13) client.loginScreenCursorPos = 1
                        if (valid) client.myUsername += typed.toChar()
                        if (client.myUsername.length > 12) client.myUsername = client.myUsername.substring(0, 12)
                    } else if (client.loginScreenCursorPos == 1) {
                        if (typed == 8 && client.myPassword.isNotEmpty()) client.myPassword = client.myPassword.substring(0, client.myPassword.length - 1)
                        if (typed == 9 || typed == 10 || typed == 13) {
                            if (client.myPassword.isNotEmpty()) {
                                client.loginFailures = 0
                                client.login(client.myUsername, client.myPassword,false)
                            } else {
                                client.loginScreenCursorPos = 0
                            }
                        }
                        if (valid) client.myPassword += typed.toChar()
                        if (client.myPassword.length > 15) client.myPassword = client.myPassword.substring(0, 15)
                    }
                } while (true)
                return
            }

            else -> {}
        }
    }

    private fun backgroundSprite() : Int {
        return when(Client.preferences.loginBackground) {
            LoginBackground.FADING_BACKGROUNDS -> LoginBackground.values().filter {
                it.spriteID != -1
            }.toList().shuffled().first().spriteID
            else -> Client.preferences.loginBackground.spriteID
        }
    }

    private fun handleBackgrounds() {
        when(Client.preferences.loginBackground) {
            LoginBackground.FADING_BACKGROUNDS -> {
                if(backgroundStopWatch == null) {
                    backgroundStopWatch = StopWatch()
                    backgroundStopWatch!!.start()
                }

                val end: Long = backgroundStopWatch!!.startTime + 1000L * BACKGROUND_ACTIVE_TIME_SECONDS
                val increment: Long = (end - backgroundStopWatch!!.startTime) / 100
                if (increment > 0) {
                    fadeToBlack.set(fadeToBlack.get() - 0.9)
                    opacity = ceil(fadeToBlack.get()).toInt()
                }
            }
            LoginBackground.ANIMATED_GAME_WORLD -> {
                Client.instance.cinematicScene.render()
            }
            else -> backgroundSprite = backgroundSprite()
        }
    }

    private fun flash(state: Int): String = if ((client.loginScreenCursorPos == state) and (Client.tick % 40 < 20)) "@yel@|" else ""

    private val validUserPassChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\u00a3$%^&*()-_=+[{]};:'@#~,<.>/?\\| "

    private val fadeToBlack: DoubleProperty = SimpleDoubleProperty(256.0)

}
