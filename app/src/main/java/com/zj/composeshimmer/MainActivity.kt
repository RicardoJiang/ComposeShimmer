package com.zj.composeshimmer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.zj.composeshimmer.ui.theme.ComposeShimmerTheme
import com.zj.shimmer.ShimmerDirection
import com.zj.shimmer.shimmer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ComposeShimmerTheme {
                ProvideWindowInsets {
                    rememberSystemUiController().setStatusBarColor(
                        Color.Transparent,
                        darkIcons = true
                    )
                    Surface(color = MaterialTheme.colors.background) {
                        HomeScreen()
                    }
                }
            }
        }
    }
}

class ShimmerModel {
    var contentColor: Color by mutableStateOf(Color.LightGray.copy(alpha = 0.3f))

    var highlightColor: Color by mutableStateOf(Color.LightGray.copy(alpha = 0.8f))

    var dropOff: Float by mutableStateOf(0.5f)

    var intensity: Float by mutableStateOf(0.2f)

    var angle: Float by mutableStateOf(20f)
}

@Composable
fun HomeScreen() {
    var loading: Boolean by remember {
        mutableStateOf(true)
    }
    val model by remember { mutableStateOf(ShimmerModel()) }
    LazyColumn(modifier = Modifier.padding(20.dp)) {
        item {
            Spacer(
                modifier = Modifier.statusBarsHeight()
            )
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shimmer(
                        loading, config = com.zj.shimmer.ShimmerConfig(
                            contentColor = model.contentColor,
                            higLightColor = model.highlightColor,
                            dropOff = model.dropOff,
                            intensity = model.intensity,
                            angle = model.angle,
                            direction = ShimmerDirection.LeftToRight
                        )
                    )
                    .clickable {
                        loading = !loading
                    }
            ) {
                repeat(3) {
                    PlaceHolderItem()
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
        item {
            ConfigSlide(model = model)
        }
        item {
            ConfigBtn(model = model)
        }
    }

}

@Composable
fun PlaceHolderItem() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .background(color = Color.LightGray)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            repeat(3) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .background(color = Color.LightGray)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            Text(
                text = "", modifier = Modifier
                    .width(200.dp)
                    .height(20.dp)
                    .background(Color.White)
            )
        }
    }
}

@Composable
private fun ConfigBtn(model: ShimmerModel) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "修改高亮颜色",
            color = Color.White,
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(16.dp)
                .width(200.dp)
                .wrapContentHeight()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Blue)
                .padding(0.dp, 10.dp)
                .clickable {
                    if (model.highlightColor != Color(0x4DFF0000)) {
                        model.highlightColor = Color(0x4DFF0000)
                    } else {
                        model.highlightColor = Color.LightGray.copy(alpha = 0.8f)
                    }
                })
        Text(
            text = "查看更多示例",
            color = Color.White,
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(16.dp)
                .width(200.dp)
                .wrapContentHeight()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Blue)
                .padding(0.dp, 10.dp)
                .clickable {
                    ShimmerSampleActivity.navigate(context)
                })
    }
}

@Composable
private fun ConfigSlide(model: ShimmerModel) {
    LabelSlider(
        label = "drop off",
        value = model.dropOff,
        onValueChange = model::dropOff::set,
        range = 0f..1f
    )
    LabelSlider(
        label = "intensity",
        value = model.intensity,
        onValueChange = model::intensity::set,
        range = 0f..1f
    )
    LabelSlider(
        label = "angle",
        value = model.angle,
        onValueChange = model::angle::set,
        range = 0f..90f
    )
}

@Composable
private fun LabelSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>
) {
    val formatValue = String.format("%.2f", value)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$label($formatValue)", modifier = Modifier.width(110.dp))
        Slider(value = value, onValueChange = onValueChange, valueRange = range)
    }
}