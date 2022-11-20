package com.runescape.loginscreen.flames

import com.runescape.cache.graphics.sprite.SpriteCache
import java.time.Instant
import java.util.*


enum class FlameImages(val id: Int, val active : (() -> Boolean)? = null) {
    //Halloween Icons
    BAT(id = 34, active = { isHalloween() }),
    SKULL(id = 36,active = { isHalloween() }),
    MOON(id = 37,active = { isHalloween() }),
    PUMPKIN(id = 35,active = { isHalloween() }),

    //Christmas Icons
    SNOWFLAKE(id = 38, active = { isChristmas() }),
    HOLLY(id = 39,active = { isChristmas() }),
    SNOWMAN(id = 40,active = { isChristmas() }),
    ANGEL(id = 41,active = { isChristmas() }),

    //Normal Icons
    FIRE_RUNE(id = 42,active = { isNotHoliday() }),
    WATER_RUNE(id = 43,active = { isNotHoliday() }),
    EARTH_RUNE(id = 44,active = { isNotHoliday() }),
    AIR_RUNE(id = 45,active = { isNotHoliday() }),
    BODY_RUNE(id = 46,active = { isNotHoliday() }),
    WRATH_RUNE(id = 47,active = { isNotHoliday() }),
    CHAOS_RUNE(id = 48,active = { isNotHoliday() }),
    COSMIC_RUNE(id = 49,active = { isNotHoliday() }),
    NATURE_RUNE(id = 50,active = { isNotHoliday() }),
    LAW_RUNE(id = 51,active = { isNotHoliday() }),
    DEATH_RUNE(id = 52,active = { isNotHoliday() }),
    SOUL_RUNE(id = 53,active = { isNotHoliday() }),

    ;

    companion object {

        private val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        fun isNotHoliday() = !isChristmas() && !isHalloween()
        
        fun isHalloween() : Boolean {
            val dateStart = GregorianCalendar(currentYear, Calendar.OCTOBER, 21).time
            val dateEnd = GregorianCalendar(currentYear, Calendar.NOVEMBER, 11).time
            return isWithinRange(dateStart,dateEnd)
        }

        fun isChristmas() : Boolean {
            val dateStart = GregorianCalendar(currentYear, Calendar.DECEMBER, 16).time
            val dateEnd = GregorianCalendar(currentYear, Calendar.JANUARY, 5).time
            return isWithinRange(dateStart,dateEnd)
        }

        private fun isWithinRange(startDate: Date, endDate : Date): Boolean {
            val currentDate = Date.from(Instant.now())
            return !(currentDate.before(startDate) || currentDate.after(endDate))
        }

        private var flameImages : List<FlameImages> = emptyList();

        fun getRandomImage() : Int {
            if (flameImages.isEmpty()) {
                flameImages = FlameImages.values().filter { it.active != null }.filter { it.active!!.invoke() }
            }
            return flameImages.random().id
        }

    }

}
