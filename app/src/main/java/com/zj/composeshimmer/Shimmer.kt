package com.zj.composeshimmer

import androidx.compose.animation.core.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.DrawModifier
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

//fun Modifier.shimmer(
//    visible: Boolean
//): Modifier = composed {
//    drawWithContent {
//        drawContent()
//        if (visible) {
//            drawRect(color = Color.Blue)
//        }
//    }
//}

//fun Modifier.shimmer(visible: Boolean): Modifier = this.then(
//
//    ShimmerModifier(visible = visible)
//)

fun Modifier.shimmer(visible: Boolean): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            tween(
                durationMillis = 1300,
                delayMillis = 300,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )
    ShimmerModifier(visible = visible, progress = progress)
}

internal class ShimmerModifier(
    private val visible: Boolean,
    private val baseAlpha: Float = 0.3f,
    private val highlightAlpha: Float = 0.9f,
    private val intensity: Float = 0f,
    private val dropOff: Float = 0.5f,
    private val progress: Float
) : DrawModifier, LayoutModifier {
    private val shaderColors = listOf(
        Color.Unspecified.copy(alpha = baseAlpha),
        Color.Unspecified.copy(alpha = highlightAlpha),
        Color.Unspecified.copy(alpha = highlightAlpha),
        Color.Unspecified.copy(alpha = baseAlpha)
    )
    private val colorStops: List<Float> = listOf(
        ((1f - intensity - dropOff) / 2f).coerceIn(0f, 1f),
        ((1f - intensity - 0.001f) / 2f).coerceIn(0f, 1f),
        ((1f + intensity + 0.001f) / 2f).coerceIn(0f, 1f),
        ((1f + intensity + dropOff) / 2f).coerceIn(0f, 1f)
    )
    private val cleanPaint = Paint()
    private val paint = Paint().apply {
        isAntiAlias = true
        style = PaintingStyle.Fill
        blendMode = BlendMode.DstIn
    }

    override fun ContentDrawScope.draw() {
        drawIntoCanvas {
            it.withSaveLayer(Rect(0f, 0f, size.width, size.height), paint = cleanPaint) {
                drawContent()
                if (visible) {
                    val dx = -(size.width) + (size.width * 2 * progress)
                    paint.shader?.transform {
                        reset()
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
        paint.shader = LinearGradientShader(
            Offset(0f, 0f),
            Offset(size.width, 0f),
            shaderColors,
            colorStops
        )
    }
}