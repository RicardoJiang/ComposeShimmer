package com.zj.composeshimmer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    var loading: Boolean by remember {
        mutableStateOf(true)
    }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .shimmer(true)
    ) {
        repeat(3) {
            PlaceHolderItem()
            Spacer(modifier = Modifier.height(10.dp))
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