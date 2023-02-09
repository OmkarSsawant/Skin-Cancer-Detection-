package te.mini_project.skincancerdetection.ui.screens

import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.graphics.drawable.shapes.Shape
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.buildSpannedString
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import te.mini_project.skincancerdetection.R
import te.mini_project.skincancerdetection.ui.theme.Shapes
import te.mini_project.skincancerdetection.ui.theme.SkinCancerDetectionTheme

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun HomeScreen(){
    val scaffoldHost = rememberScaffoldState()
    var clickedColor by remember {
        mutableStateOf(Color.White)
    }
    val bgTestColor by animateColorAsState(targetValue = clickedColor)

    Scaffold(scaffoldState =  scaffoldHost/*, backgroundColor = bgTestColor*/,topBar = {
        Row(

           horizontalArrangement =  Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ){
            val dynamicColor = AndroidColor.argb(255,(255 * Math.random()).toInt(),(255 * Math.random()).toInt(),(255 * Math.random()).toInt())
                Text("Home",style= TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold))
                Row{
                    IconButton(onClick = {
                        GlobalScope.launch {
                            scaffoldHost.snackbarHostState.showSnackbar("Sending Update to doctors")
                        }
                    }) {
                        Icon(painter = painterResource(id = android.R.drawable.stat_notify_sync), contentDescription = "send mail icon")
                    }
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .background(Color(dynamicColor))
                    ) {
                            Text(modifier = Modifier.padding(12.dp),text="OS", style = TextStyle(Color.White))
                    }
                }
        }
    }
    ,
    bottomBar = {
        BottomAppBar(
            cutoutShape = CircleShape,
            modifier = Modifier.fillMaxWidth(),

        ) {

            IconButton(modifier = Modifier.weight(1f),onClick = {  }) {
                Icon(painterResource(id = R.drawable.baseline_dashboard_24), contentDescription = "home")
            }
            IconButton(modifier = Modifier.weight(1f),onClick = {  }) {
                Icon(painterResource(id = R.drawable.baseline_analytics_24), contentDescription = "analytics")
            }


        }

    }, floatingActionButton = {
        FloatingActionButton(onClick = {  }) {
            Icon(painter = painterResource(id = android.R.drawable.ic_input_add), contentDescription = "")
        }
        }, floatingActionButtonPosition = FabPosition.Center, isFloatingActionButtonDocked = true){



        val mask =   ImageBitmap.imageResource(id = R.drawable.body_mask)
        val maskBitmap = mask.asAndroidBitmap()
        var screenImgSize: IntSize = IntSize.Zero
        Box(
            Modifier
                .pointerInput(Unit) {
                    detectTapGestures { p ->
                        val bitmapScaledX = (mask.width / screenImgSize.width) * p.x
                        val bitmapScaledY = (mask.height / screenImgSize.height) * p.y
                        val clickedPixel =
                            maskBitmap.getPixel(bitmapScaledX.toInt(), bitmapScaledY.toInt())
                        clickedColor = Color(
                            android.graphics.Color.red(clickedPixel),
                            android.graphics.Color.green(clickedPixel),
                            android.graphics.Color.blue(clickedPixel)
                        )
                        Log.i("Test", "HomeScreen: $clickedColor")
                    }
                }
                .onSizeChanged {
                    screenImgSize = it
                }
                .padding(vertical = 52.dp)
        ) {
            Image(modifier = Modifier.fillMaxSize(),painter = painterResource(id = R.drawable.body_mask), contentDescription = "click_mask")
            Image(modifier = Modifier.fillMaxSize(),painter = painterResource(id = R.drawable.body_main), contentDescription = "main_body")
            Row(Modifier.fillMaxWidth()
                .offset(y= (-52).dp)
                ,
                horizontalArrangement = Arrangement.SpaceAround
            ){
                Column {
                    Text("Status", style = MaterialTheme.typography.h5)
                    Text("Good", style = MaterialTheme.typography.h6.copy(Color.Green))
                }
                Spacer(modifier = Modifier.width(44.dp))

                Column {
                    Text("Checkups", style = MaterialTheme.typography.h5)
                   val checkups =  buildAnnotatedString {
                        withStyle(SpanStyle(fontSize = 42.sp)){
                            append("3")
                            withStyle(SpanStyle(fontSize = 12.sp)){
                                append("w")
                            }
                        }
                    append('\t')
                        withStyle(SpanStyle(fontSize = 32.sp)){
                            append("25")
                            withStyle(SpanStyle(fontSize = 12.sp)){
                                append("t")
                            }
                        }

                    }
                    Text(checkups)
                }
            }
        }
        Spacer(modifier = Modifier.height(77.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultHomeScreenPreview() {
    SkinCancerDetectionTheme {
        HomeScreen()
    }
}