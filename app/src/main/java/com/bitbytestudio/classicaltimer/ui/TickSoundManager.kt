package com.bitbytestudio.classicaltimer.ui

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.bitbytestudio.classicaltimer.R

class TickSoundManager(context: Context) {

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val tickSoundId = soundPool.load(context, R.raw.raw_tick_sound, 1)

    fun playTick() {
        soundPool.play(tickSoundId, 1f, 1f, 0, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}