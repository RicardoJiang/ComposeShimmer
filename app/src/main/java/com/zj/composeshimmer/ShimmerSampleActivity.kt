/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zj.composeshimmer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.zj.composeshimmer.ui.theme.ComposeShimmerTheme
import com.zj.shimmer.shimmer
import kotlinx.coroutines.delay

class ShimmerSampleActivity : ComponentActivity() {
    companion object {
        fun navigate(context: Context) {
            context.startActivity(Intent(context, ShimmerSampleActivity::class.java))
        }
    }

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
                    Sample()
                }
            }
        }
    }
}

@Composable
private fun Sample() {
    Scaffold(
        topBar = {
            Column() {
                Spacer(modifier = Modifier.statusBarsHeight())
                TopAppBar(
                    title = { Text(text = "Shimmer Example") },
                    backgroundColor = MaterialTheme.colors.surface,
                    elevation = 0.dp
                )
                Spacer(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color.LightGray))
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        // Simulate a fake 2-second 'load'. Ideally this 'refreshing' value would
        // come from a ViewModel or similar
        var refreshing by remember { mutableStateOf(false) }
        LaunchedEffect(refreshing) {
            if (refreshing) {
                delay(4000)
                refreshing = false
            }
        }

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = refreshing),
            onRefresh = { refreshing = true },
        ) {
            LazyColumn {
                if (refreshing.not()) {
                    item {
                        ListItem(
                            painter = rememberVectorPainter(Icons.Default.ArrowDownward),
                            text = "Pull down"
                        )
                    }
                }
                items(30) { index ->
                    ListItem(
                        painter = rememberImagePainter(randomSampleImageUrl(index)),
                        text = "Text",
                        modifier = Modifier.shimmer(refreshing),
                        childModifier = Modifier.placeholder(
                            visible = refreshing
                        )
                    )
                }
            }
        }
    }
}
