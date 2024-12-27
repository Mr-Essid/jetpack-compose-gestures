package com.example.helloworld

import android.content.ClipData
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.collection.IntList
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.helloworld.ui.theme.HelloWorldTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HelloWorldTheme {
                SwipeDemonstration()
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
    val TAG = "DragAndDropDemonstration"
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

@Composable
fun SwipeDemonstration(modifier: Modifier = Modifier) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val coroutineScope = rememberCoroutineScope()
    Scaffold  { it ->
        Column(modifier = Modifier
            .padding(horizontal = 4.dp)
            .padding(it)
        ) {
            Text(
                "Swipe demonstration", modifier = Modifier.padding(
                    vertical = 8.dp
                )
            )
            val ourMutableListState = List(40) {it}.toMutableStateList()

            LazyColumn {
                items(ourMutableListState,
                    key = {UUID.randomUUID().toString()}
                ) {
                    var internalState by remember {
                        mutableFloatStateOf(0f)
                    }
                    val isSwapped by remember { derivedStateOf {
                        (screenWidth.toFloat() / internalState) < 2f
                        }
                    }
                    var job: Job? = null
                    Box(
                       modifier =  Modifier

                    ) {
                        Box(modifier = Modifier.matchParentSize(), contentAlignment = Alignment.CenterStart) {
                            TextButton(onClick = {
                                if(job != null) {
                                    Log.d(TAG, "SwipeDemonstration: job canceled")
                                    job?.cancel()
                                }else {
                                    Log.d(TAG, "SwipeDemonstration: job doesn't exits!")
                                }
                                internalState = 0f
                            }) {
                              Text("UNDO")
                            }
                        }
                        ListItem(
                            modifier = Modifier
                                .padding(vertical = 2.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .offset(internalState.roundToInt().dp)
                                .pointerInput(Unit) {
                                    detectHorizontalDragGestures(
                                        onDragEnd = {
                                            if (isSwapped) {
                                                internalState += 999f
                                                job = coroutineScope.launch {
                                                    Log.d(TAG, "SwipeDemonstration: we are running!")
                                                    delay(4000)
                                                    ourMutableListState.remove(it)
                                                }
                                            } else {
                                                internalState = 0f
                                            }
                                        }
                                    ) { change, dragAmount ->
                                        change.consume()
                                        if (internalState + dragAmount > 0)
                                            internalState += dragAmount.coerceIn(-6f, 6f)
                                    }
                                }
                            ,
                            colors = ListItemDefaults.colors(containerColor = Color.Gray),
                            headlineContent = {
                                Text("Item $it")
                            }
                        )
                    }
                }
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
private fun LIstDemonstration() {
   val list_ = List(50) {it} .toMutableStateList()
    LazyColumn {
       items(list_)  {
           ListItem(headlineContent = {
               Text("Item $it")
           },
               modifier = Modifier.clickable { list_.remove(it) }
           )
       }
    }
}