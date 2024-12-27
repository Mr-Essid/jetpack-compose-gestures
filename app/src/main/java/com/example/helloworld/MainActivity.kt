package com.example.helloworld

import android.content.ClipData
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.helloworld.ui.theme.HelloWorldTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HelloWorldTheme {
                DragAndDropDemonstration()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HelloWorldTheme {
        Greeting("Android")
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DragAndDropDemonstration(modifier: Modifier = Modifier) {
    val TAG = "MainActivity"
    var firstOneCandidate by remember { mutableStateOf(false) }
    var secondOneCandidate by remember { mutableStateOf(false) }
    val firstCandidateColor by animateColorAsState(if(firstOneCandidate) Color.Gray else Color.White,
        label = "color of first field"
    )
    val secondCandidateColor by animateColorAsState(if(secondOneCandidate) Color.Gray else Color.White,
        label = "color of first field"
    )
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        )
        {
            Row(Modifier.fillMaxWidth()) {
                Box(modifier = Modifier
                    .padding(2.dp)
                    .height(70.dp)
                    .weight(1f)
                    .dragAndDropTarget(
                        shouldStartDragAndDrop = { true },
                        object : DragAndDropTarget {
                            override fun onEntered(event: DragAndDropEvent) {
                                super.onEntered(event)
                                firstOneCandidate = true
                                Log.d(TAG, "onEntered: entered to the first")
                            }

                            override fun onExited(event: DragAndDropEvent) {
                                super.onExited(event)
                                firstOneCandidate = false
                                Log.d(TAG, "Exited: exited to the first")
                            }

                            override fun onEnded(event: DragAndDropEvent) {
                                super.onEnded(event)
                                firstOneCandidate = false
                            }

                            override fun onDrop(event: DragAndDropEvent): Boolean {
                                Log.d(
                                    TAG,
                                    "onDrop: first ${
                                        event.toAndroidDragEvent().clipData.getItemAt(0).text
                                    }"
                                )
                                return true
                            }
                        }
                    )
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedCard(
                        modifier = Modifier.matchParentSize(),
                        colors = CardDefaults.outlinedCardColors(containerColor = firstCandidateColor)
                    )
                    {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Drop Here")
                        }
                    }
                }
                Box(modifier = Modifier
                    .padding(2.dp)
                    .height(70.dp)
                    .weight(1f)
                    .dragAndDropTarget(
                        shouldStartDragAndDrop = { true },
                        object : DragAndDropTarget {
                            override fun onEntered(event: DragAndDropEvent) {
                                super.onEntered(event)
                                Log.d(TAG, "onEntered: second one entered")
                                secondOneCandidate = true
                            }

                            override fun onExited(event: DragAndDropEvent) {
                                super.onExited(event)
                                secondOneCandidate = false
                            }

                            override fun onEnded(event: DragAndDropEvent) {
                                super.onEnded(event)
                                secondOneCandidate = false
                            }

                            override fun onDrop(event: DragAndDropEvent): Boolean {
                                Log.d(
                                    TAG,
                                    "onDrop: from second  ${
                                        event.toAndroidDragEvent().clipData.getItemAt(0).text
                                    }"
                                )
                                return true
                            }
                        }
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedCard(
                        modifier = Modifier.matchParentSize(),
                        colors = CardDefaults.outlinedCardColors(containerColor = secondCandidateColor)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Drop Here")
                        }
                    }
                }
            }
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(
                    20
                ) {
                    ListItem(headlineContent = {
                        Text("Item $it")
                    },
                        modifier = Modifier
                            .width(200.dp)
                            .padding(
                                horizontal = 4.dp,
                                vertical = 2.dp
                            )
                            .clip(RoundedCornerShape(8.dp))
                            .dragAndDropSource {
                                detectTapGestures(
                                    onLongPress = {
                                        startTransfer(
                                            DragAndDropTransferData(
                                                ClipData.newPlainText(
                                                    "label clip",
                                                    "this is it"
                                                )
                                            )
                                        )
                                    }
                                )
                            }
                        ,
                        colors = ListItemDefaults.colors(containerColor = Color.Gray.copy(alpha = 0.4f),
                        )

                    )
                }
            }
        }

    }
}