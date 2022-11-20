package com.runescape

import com.beust.klaxon.Klaxon
import com.runescape.draw.Rasterizer3D
import com.runescape.loginscreen.LoginBackground
import com.runescape.sign.SignLink
import net.runelite.rs.api.RSClientPreferences
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

data class PreferencesData(
    var brightnessState : Double = 0.8,
    var enableMusic : Boolean = true,
    var escapeCloseInterface : Boolean = false,
    var mergeExpDrops : Boolean = true,
    var hpAboveHeads : Boolean = false,
    var enableTooltipHovers : Boolean = false,
    var enableSkillOrbs : Boolean = false,
    var enableGroundItemNames : Boolean = true,
    var enableBuffOverlay : Boolean = true,
    var combatOverlayBox : Boolean = true,
    var enableSpecOrb : Boolean = true,
    var enableOrbs : Boolean = true,
    var enableRoofs : Boolean = false,
    var stackSideStones : Boolean = false,
    var changeChatArea : Boolean = false,
    var transparentTabArea : Boolean = false,
    var npcAttackOptionPriority: Int = 2,
    var playerAttackOptionPriority: Int = 0,
    var enableShiftClickDrop: Boolean = true,
    var eulaAccepted : Boolean = false,
    var rememberUsername : Boolean = false,
    var hiddenUsername : Boolean = false,
    var savedUsername : String = "",
    var loginBackground : LoginBackground = LoginBackground.ANIMATED_GAME_WORLD
) : RSClientPreferences {

    override fun getRememberedUsername(): String {
        return savedUsername;
    }

    override fun setRememberedUsername(username: String?) {
        TODO("Not yet implemented")
    }

    override fun getSoundEffectVolume(): Int {
        TODO("Not yet implemented")
    }

    override fun setSoundEffectVolume(i: Int) {
        TODO("Not yet implemented")
    }

    override fun getAreaSoundEffectVolume(): Int {
        TODO("Not yet implemented")
    }

    override fun setAreaSoundEffectVolume(i: Int) {
        TODO("Not yet implemented")
    }

    override fun getMusicVolume(): Int {
        TODO("Not yet implemented")
    }

    override fun setMusicVolume(i: Int) {
        TODO("Not yet implemented")
    }

    override fun getHideUsername(): Boolean {
        return false
    }
}

object UserPreferences {


    fun load(client : Client) {
        val file = File(SignLink.findcachedir(),"settings.json")

        if (!file.exists()) {
            file.createNewFile()
            save()
        }

        Client.preferences = Klaxon().parse<PreferencesData>(file.readText())!!

    }


    fun save() {
        val file = File(SignLink.findcachedir(),"settings.json")
        try {
            val output = BufferedWriter(FileWriter(file))
            output.write(Klaxon().toJsonString(Client.instance.preferences))
            output.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}