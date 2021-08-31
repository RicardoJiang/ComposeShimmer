package com.zj.composeshimmer

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints

fun Modifier.shimmer(
    visible: Boolean
): Modifier = composed {
    drawWithContent {
        drawContent()
        if (visible) {
            drawRect(color = Color.Blue)
        }
    }
}

internal class ShimmerModifier(visible: Boolean) : DrawModifier, LayoutModifier {
    override fun ContentDrawScope.draw() {
        TODO("Not yet implemented")
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        TODO("Not yet implemented")
    }

}