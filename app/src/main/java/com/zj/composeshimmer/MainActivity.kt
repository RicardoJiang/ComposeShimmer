package com.zj.composeshimmer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.placeholder
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

@Composable
fun Greeting(name: String) {
    var loading: Boolean by remember { mutableStateOf(true) }
    Text(text = "Hello $name!", modifier = Modifier
        .padding(16.dp)
        .shimmer(visible = loading)
        .clickable {
            loading = !loading
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeShimmerTheme {
        Greeting("Android")
    }
}