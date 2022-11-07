package com.runescape.loginscreen

import com.runescape.Client
import com.runescape.cache.graphics.ImageCache

class LoginScreen(client : Client) {

    fun drawLogin() {
        ImageCache.get(5).drawSprite(0,0)
    }


    fun handleInput() {

    }

}