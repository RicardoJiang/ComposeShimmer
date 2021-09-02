package com.zj.composeshimmer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.zj.composeshimmer.ui.theme.ComposeShimmerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeShimmerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

class ShimmerModel {
    var baseColor: Color by mutableStateOf(Color.LightGray.copy(alpha = 0.3f))

    var highlightColor: Color by mutableStateOf(Color.LightGray.copy(alpha = 0.8f))

    var dropOff: Float by mutableStateOf(0.5f)

    var intensity: Float by mutableStateOf(0.2f)

    var angle: Float by mutableStateOf(20f)
}

@Composable
fun Greeting(name: String) {
    var loading: Boolean by remember {
        mutableStateOf(true)
    }
    val model by remember { mutableStateOf(ShimmerModel()) }
    LazyColumn(modifier = Modifier.padding(20.dp)) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shimmer(
                        loading, config = ShimmerConfig(
                            contentColor = model.baseColor,
                            higLightColor = model.highlightColor,
                            dropOff = model.dropOff,
                            intensity = model.intensity,
                            angle = model.angle
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
            TestConfig(model = model)
        }
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "确定更新动画",
                    color = Color.White,
                    modifier = Modifier
                        .background(Color.Blue)
                        .padding(30.dp, 16.dp, 30.dp, 16.dp)
                        .placeholder(true, highlight = PlaceholderHighlight.shimmer())
                        .clickable {
                            loading = false
                            loading = true
                        })
            }
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
private fun TestConfig(model: ShimmerModel) {
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