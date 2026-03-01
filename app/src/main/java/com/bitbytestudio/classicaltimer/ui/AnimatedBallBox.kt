package com.bitbytestudio.classicaltimer.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalLayoutApi::class)
@Composable
fun AnimatedBallBox(
    title: String,
    ballCount: Int,
    total: Int,
    isTop: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val color = if (isTop) Color.Red else Color(0xFF00C853)

    val progress = if (total == 0) 0f else ballCount.toFloat() / total.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .height(160.dp)
                .drawBehind {
                    val strokeWidth = 6.dp.toPx()
                    val cornerRadius = 20f
                    val w = size.width
                    val h = size.height

                    // Total perimeter length
                    val perimeter = 2f * (w + h)
                    val progressLength = perimeter * animatedProgress

                    // Background outline (faint)
                    drawRoundRect(
                        color = color.copy(alpha = 0.15f),
                        size = size,
                        cornerRadius = CornerRadius(cornerRadius),
                        style = Stroke(width = strokeWidth)
                    )

                    // Animated progress outline
                    val paint = Paint().apply {
                        this.color = color
                        this.style = PaintingStyle.Stroke
                        strokeCap = StrokeCap.Round
                        this.strokeWidth = strokeWidth
                    }

                    val path = Path().apply {
                        // Start from top-left, go clockwise: top → right → bottom → left
                        moveTo(cornerRadius, 0f)
                        lineTo(w - cornerRadius, 0f)                          // top
                        quadraticTo(w, 0f, w, cornerRadius)                   // top-right corner
                        lineTo(w, h - cornerRadius)                            // right
                        quadraticTo(w, h, w - cornerRadius, h)                // bottom-right corner
                        lineTo(cornerRadius, h)                                // bottom
                        quadraticTo(0f, h, 0f, h - cornerRadius)              // bottom-left corner
                        lineTo(0f, cornerRadius)                               // left
                        quadraticTo(0f, 0f, cornerRadius, 0f)                 // top-left corner
                    }

                    val pathMeasure = PathMeasure()
                    pathMeasure.setPath(path, false)

                    val clippedPath = Path()
                    pathMeasure.getSegment(0f, progressLength, clippedPath, true)

                    drawIntoCanvas { canvas ->
                        canvas.drawPath(clippedPath, paint)
                    }
                }
                .padding(12.dp)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(ballCount) { index ->
                    val key = "ball_${isTop}_$index"

                    with(sharedTransitionScope) {
                        Box(
                            Modifier
                                .size(22.dp)
                                .sharedElement(
                                    rememberSharedContentState(key),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    boundsTransform = { _, _ ->
                                        spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMediumLow
                                        )
                                    }
                                )
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }
            }
        }
    }
}