package com.zj.shimmer

import androidx.compose.animation.core.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.core.graphics.transform
import kotlin.math.tan


fun Modifier.shimmer(
    visible: Boolean, config: ShimmerConfig = ShimmerConfig()
) = composed(inspectorInfo = debugInspectorInfo {
    name = "shimmer"
    properties["visible"] = visible
    properties["config"] = config
}) {
    var progress: Float by remember { mutableFloatStateOf(0f) }
    if (visible) {
        val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")
        progress = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                tween(
                    durationMillis = config.duration.toInt(),
                    delayMillis = config.delay.toInt(),
                    easing = LinearEasing
                ), repeatMode = RepeatMode.Restart
            ),
            label = "progress",
        ).value
    }

    ShimmerElement(visible = visible,
        progress = progress,
        config = config,
        inspectorInfo = debugInspectorInfo {
            name = "shimmerElement"
            properties["visible"] = visible
            properties["config"] = config
            properties["progress"] = progress
        })
}


private data class ShimmerElement(
    var visible: Boolean,
    var progress: Float,
    var config: ShimmerConfig,
    val inspectorInfo: InspectorInfo.() -> Unit
) : ModifierNodeElement<ShimmerNode>() {
    override fun create(): ShimmerNode {
        return ShimmerNode(
            visible = visible, progress = progress, config = config
        )
    }

    override fun equals(other: Any?): Boolean {
        val otherModifier = other as? ShimmerElement ?: return false
        return visible == otherModifier.visible && progress == otherModifier.progress && config == otherModifier.config
    }

    override fun hashCode(): Int {
        var result = visible.hashCode()
        result = 31 * result + progress.hashCode()
        result = 31 * result + config.hashCode()
        return result
    }

    override fun update(node: ShimmerNode) {
        node.visible = visible
        node.progress = progress
        node.config = config
        node.updateColor()
    }

    override fun InspectorInfo.inspectableProperties() {
        inspectorInfo()
    }
}

private class ShimmerNode(
    var visible: Boolean, var progress: Float, var config: ShimmerConfig
) : DrawModifierNode, LayoutModifierNode, Modifier.Node() {
    private val cleanPaint = Paint()
    private val paint = Paint().apply {
        isAntiAlias = true
        style = PaintingStyle.Fill
        blendMode = BlendMode.SrcIn
    }
    private val angleTan = tan(Math.toRadians(config.angle.toDouble())).toFloat()
    private var translateHeight = 0f
    private var translateWidth = 0f
    private var intensity = config.intensity
    private var dropOff = config.dropOff

    private val colors = listOf(
        config.contentColor, config.higLightColor, config.higLightColor, config.contentColor
    )

    private var colorStops: List<Float> = listOf(
        ((1f - intensity - dropOff) / 2f).coerceIn(0f, 1f),
        ((1f - intensity - 0.001f) / 2f).coerceIn(0f, 1f),
        ((1f + intensity + 0.001f) / 2f).coerceIn(0f, 1f),
        ((1f + intensity + dropOff) / 2f).coerceIn(0f, 1f)
    )

    fun updateColor() {
        if (intensity != config.intensity || dropOff != config.dropOff) {
            intensity = config.intensity
            dropOff = config.dropOff
            colorStops = listOf(
                ((1f - intensity - dropOff) / 2f).coerceIn(0f, 1f),
                ((1f - intensity - 0.001f) / 2f).coerceIn(0f, 1f),
                ((1f + intensity + 0.001f) / 2f).coerceIn(0f, 1f),
                ((1f + intensity + dropOff) / 2f).coerceIn(0f, 1f)
            )
        }
    }

    override fun ContentDrawScope.draw() {
        drawIntoCanvas {
            it.withSaveLayer(Rect(0f, 0f, size.width, size.height), paint = cleanPaint) {
                drawContent()
                if (visible) {
                    val (dx, dy) = when (config.direction) {
                        ShimmerDirection.LeftToRight -> Pair(
                            offset(-translateWidth, translateWidth, progress), 0f
                        )

                        ShimmerDirection.RightToLeft -> Pair(
                            offset(translateWidth, -translateWidth, progress), 0f
                        )

                        ShimmerDirection.TopToBottom -> Pair(
                            0f, offset(-translateHeight, translateHeight, progress)
                        )


                        ShimmerDirection.BottomToTop -> Pair(
                            0f, offset(translateHeight, -translateHeight, progress)
                        )

                    }
                    paint.shader?.transform {
                        reset()
                        postRotate(config.angle, size.width / 2f, size.height / 2f)
                        postTranslate(dx, dy)
                    }
                    it.drawRect(Rect(0f, 0f, size.width, size.height), paint = paint)
                }
            }
        }
    }

    override fun MeasureScope.measure(
        measurable: Measurable, constraints: Constraints
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
        translateHeight = size.height + angleTan * size.width
        val toOffset = when (config.direction) {
            ShimmerDirection.RightToLeft, ShimmerDirection.LeftToRight -> Offset(size.width, 0f)
            else -> Offset(0f, size.height)
        }
        paint.shader = LinearGradientShader(
            Offset(0f, 0f), toOffset, colors, colorStops
        )
    }

    //计算位置渐变
    private fun offset(start: Float, end: Float, progress: Float): Float {
        return start + (end - start) * progress
    }
}