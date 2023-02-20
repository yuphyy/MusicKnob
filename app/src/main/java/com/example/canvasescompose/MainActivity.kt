package com.example.canvasescompose

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.canvasescompose.presentation.SharedViewModel
import com.example.canvasescompose.ui.theme.CanvasesComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: SharedViewModel = hiltViewModel()
            CanvasesComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF101010))
                    ){
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .border(1.dp, Color.Green, RoundedCornerShape(10.dp))
                                .padding(30.dp)
                        ) {
                            val volume = viewModel.volume.collectAsState()

                            val barCount = 20
                            MusicKnob(modifier = Modifier.size(100.dp)) {
                                viewModel.updateVolume(it)
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                            VolumeBar(modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp),
                                activeBars = (barCount * volume.value).roundToInt(),
                                barCount = barCount
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VolumeBar(
    modifier: Modifier,
    activeBars: Int = 0,
    barCount: Int = 10
) {

    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        val barWidth = remember {
            constraints.maxWidth / (2f * barCount)
        }
        Canvas(modifier = modifier) {
            for( i in 0 until barCount){
                drawRoundRect(
                    color = if( i in 0..activeBars) Color.Green else Color.DarkGray,
                    topLeft = Offset(i * barWidth * 2f + barWidth/2f, 0f),
                    size = Size(barWidth, constraints.maxHeight.toFloat()),
                    cornerRadius = CornerRadius(2f)
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MusicKnob(
    viewModel: SharedViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    limitingAngle: Float = 25f,
    onValueChange: (Float) -> Unit
) {
    //rotation angle

    var rotation = viewModel.rotation.collectAsState()

    //touches
    var touchX by remember {
        mutableStateOf(0f)
    }
    var touchY by remember {
        mutableStateOf(0f)
    }
    var centerX by remember {
        mutableStateOf(0f)
    }
    var centerY by remember {
        mutableStateOf(0f)
    }

    Image(
        painter = painterResource(id = R.drawable.dial_knob),
        contentDescription = null,
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned {
                val windowBounds = it.boundsInWindow()
                centerX = windowBounds.size.width / 2
                centerY = windowBounds.size.height / 2
            }
            .pointerInteropFilter { event ->
                touchY = event.y
                touchX = event.x
                val angle = -atan2(centerX - touchX, centerY - touchY) * (180f / PI).toFloat()

                when (event.action) {
                    MotionEvent.ACTION_DOWN,
                    MotionEvent.ACTION_MOVE -> {
                        if (angle !in -limitingAngle..limitingAngle) {
                            val fixedAngle = if (angle in -180f..-limitingAngle) {
                                360f + angle
                            } else {
                                angle
                            }
                            viewModel.updateRotation(fixedAngle)
                            val percent = (fixedAngle - limitingAngle) / (360f - 2 * limitingAngle)
                            onValueChange(percent)
                            true
                        } else false
                    }
                    else -> false
                }
            }
            .rotate(rotation.value)
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MusicKnobCanvas(
    modifier: Modifier = Modifier,
    limitingAngle: Float = 25f,
    onValueChange: (Float) -> Unit
) {
    //rotation angle
    var rotation by remember {
        mutableStateOf(limitingAngle)
    }

    //touches
    var touchX by remember {
        mutableStateOf(0f)
    }
    var touchY by remember {
        mutableStateOf(0f)
    }
    var centerX by remember {
        mutableStateOf(0f)
    }
    var centerY by remember {
        mutableStateOf(0f)
    }

    Canvas(
        modifier = modifier
            .pointerInteropFilter { event ->
                touchY = event.y
                touchX = event.x
                val angle = -atan2(centerX - touchX, centerY - touchY) * (180f / PI).toFloat()

                when (event.action) {
                    MotionEvent.ACTION_DOWN,
                    MotionEvent.ACTION_MOVE -> {
                        if (angle !in -limitingAngle..limitingAngle) {
                            val fixedAngle = if (angle in -180f..-limitingAngle) {
                                360f + angle
                            } else {
                                angle
                            }
                            rotation = fixedAngle
                            val percent = (fixedAngle - limitingAngle) / (360f - 2 * limitingAngle)
                            onValueChange(percent)
                            true
                        } else false
                    }
                    else -> false
                }
            }
            .rotate(rotation)
    ) {
        drawCircle(color = Color.LightGray)
        drawCircle(
            color = Color(R.color.gray),
            style = Stroke(width = 15f)
        )
        drawLine(
            Color.DarkGray,
            start = Offset(this.size.width / 2, this.size.height - 15),
            end = Offset(this.size.width / 2, this.size.height - 20),
            strokeWidth = 10f,
            cap = StrokeCap.Round
        )
    }
}
