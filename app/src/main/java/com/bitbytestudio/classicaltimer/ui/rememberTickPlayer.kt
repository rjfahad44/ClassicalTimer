package com.bitbytestudio.classicaltimer.ui

import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.bitbytestudio.classicaltimer.R

@Composable
fun rememberTickPlayer(): MediaPlayer? {
    val context = LocalContext.current
    return remember {
        MediaPlayer.create(context, R.raw.raw_tick_sound).apply {
            isLooping = false
        }
    }
}