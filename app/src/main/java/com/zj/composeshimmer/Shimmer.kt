package com.zj.composeshimmer

import androidx.compose.animation.core.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.core.graphics.transform
import kotlin.math.tan

fun Modifier.shimmer(
    visible: Boolean,
    colors: List<Color> = listOf(
        Color.LightGray.copy(alpha = 0.3f),
        Color.LightGray.copy(alpha = 0.9f),
        Color.LightGray.copy(alpha = 0.3f)
    ),
    colorStops: List<Float> = listOf(0.1f, 0.5f, 0.9f)
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            tween(
                durationMillis = 1000,
                delayMillis = 300,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )
    ShimmerModifier(visible = visible, progress = progress, colors, colorStops)
}

internal class ShimmerModifier(
    private val visible: Boolean,
    private val progress: Float,
    private val colors: List<Color>,
    private val colorStops: List<Float>,
) : DrawModifier, LayoutModifier {
    private val cleanPaint = Paint()
    private val paint = Paint().apply {
        isAntiAlias = true
        style = PaintingStyle.Fill
        blendMode = BlendMode.SrcIn
    }
    private val angleTan = tan(Math.toRadians(20.toDouble())).toFloat()
    private var translateHeight = 0f
    private var translateWidth = 0f

    override fun ContentDrawScope.draw() {
        drawIntoCanvas {
            it.withSaveLayer(Rect(0f, 0f, size.width, size.height), paint = cleanPaint) {
                drawContent()
                if (visible) {
                    val dx = -(translateWidth) + (translateWidth * 2 * progress)
                    paint.shader?.transform {
                        reset()
                        postRotate(20f, size.width / 2f, size.height / 2f)
                        postTranslate(dx, 0f)
                    }
                    it.drawRect(Rect(0f, 0f, size.width, size.height), paint = paint)
                }
            }
        }
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        val size = Size(width = placeable.width.toFloat(), height = placeable.height.toFloat())
        updateSize(size)
        return layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }

    private fun updateSize(size: Size) {
        translateWidth = size.width + angleTan * size.height
        paint.shader = LinearGradientShader(
            Offset(0f, 0f),
            Offset(size.width, 0f),
            colors,
            colorStops
        )
    }
}