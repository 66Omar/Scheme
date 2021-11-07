package com.scheme.utilities

import android.graphics.Color
import java.util.Random

object SchemeUtils {
    private const val lectureIncrement = 1000000
    private const val eventIncrement = lectureIncrement * 3

    fun generateColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(150), rnd.nextInt(150), rnd.nextInt(150))
    }

    fun formatPath(text: String?): String {
        return text.toString().lowercase().filter { !it.isWhitespace() }
    }

    fun getNotificationIds(id: Long, type: Int): IntArray {
        val intId = id.toInt()
        when (type) {
            0 -> return intArrayOf(intId + lectureIncrement * 0,
                intId + lectureIncrement*1,
                intId + lectureIncrement*2)
            1 -> return intArrayOf(intId + eventIncrement * 0,
                intId + eventIncrement*1,
                intId + eventIncrement*2)
        }
        return intArrayOf()
    }
}