package com.bitbytestudio.classicaltimer.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalLayoutApi::class)
@Composable
fun AnimatedClassicalTimer(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val controller = remember { ClassicalTimerController() }
    val timerState by controller.state.collectAsState()

    val tickSound = remember { TickSoundManager(context) }

    // Play tick sound on each second tick
    val prevRemaining = remember { mutableIntStateOf(-1) }
    LaunchedEffect(timerState.remaining) {
        if (timerState.isRunning && timerState.remaining != prevRemaining.intValue) {
            prevRemaining.intValue = timerState.remaining
            tickSound.playTick()
        }
    }

    DisposableEffect(Unit) {
        onDispose { tickSound.release() }
    }

    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .then(modifier)
            ) {
                Text(
                    text = "Remaining: ${timerState.remaining}s",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(Modifier.height(16.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items((1..60).toList()) { sec ->
                        Box(
                            Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (timerState.total == sec && timerState.isRunning)
                                        Color(0xFF1565C0) else Color.DarkGray
                                )
                                .clickable { controller.start(sec, scope) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(sec.toString(), color = Color.White)
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                AnimatedBallBox(
                    title = "Remaining",
                    ballCount = timerState.topBalls,
                    total = timerState.total,
                    isTop = true,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedVisibility
                )

                Spacer(Modifier.height(32.dp))

                AnimatedBallBox(
                    title = "Elapsed",
                    ballCount = timerState.bottomBalls,
                    total = timerState.total,
                    isTop = false,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedVisibility
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { controller.pause() },
                        enabled = timerState.isRunning) {
                        Text("Pause")
                    }
                    Button(onClick = { controller.resume(scope) },
                        enabled = timerState.isPaused) {
                        Text("Resume")
                    }
                    Button(onClick = { controller.stop() }) {
                        Text("Stop")
                    }
                }
            }
        }
    }
}