package com.zj.composeshimmer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.material.placeholder
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

    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .shimmer(true), verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .placeholder(true)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column(modifier = Modifier.padding(8.dp)) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .placeholder(true)
            )
            Spacer(
                modifier = Modifier
                    .padding(0.dp,8.dp)
                    .fillMaxWidth()
                    .height(30.dp)
                    .placeholder(true)
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(30.dp)
                    .placeholder(true)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeShimmerTheme {
        Greeting("Android")
    }
}