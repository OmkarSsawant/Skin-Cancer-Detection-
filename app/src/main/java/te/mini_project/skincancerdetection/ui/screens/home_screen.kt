package te.mini_project.skincancerdetection.ui.screens

import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import te.mini_project.skincancerdetection.R
import te.mini_project.skincancerdetection.ui.composables.MolesHistory
import te.mini_project.skincancerdetection.room.SkinCancerDatabase
import te.mini_project.skincancerdetection.room.models.MoleScan

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun HomeScreen(navScan:()->Unit,navAnalytics:()->Unit){
    val scaffoldHost = rememberScaffoldState()

    var moleScans by remember {
        mutableStateOf(listOf<MoleScan>())
    }
    val context = LocalContext.current.applicationContext
    LaunchedEffect(key1 = "" ){
            withContext(Dispatchers.IO){
                val db = SkinCancerDatabase.getInstance(context)
                db.skinCancerDao().observeMolesRecord().collect{
                    moleScans = it
                }
            }
    }

    Scaffold(scaffoldState =  scaffoldHost/*, backgroundColor = bgTestColor*/,topBar = {
        Row(

           horizontalArrangement =  Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ){
            val dynamicColor = AndroidColor.argb(255,(255 * Math.random()).toInt(),(255 * Math.random()).toInt(),(255 * Math.random()).toInt())
                Text("\uD83D\uDEE1️ Home",style= TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold))
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
                            .background(color = Color(dynamicColor))
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
            IconButton(modifier = Modifier.weight(1f),onClick = navAnalytics) {
                Icon(painterResource(id = R.drawable.baseline_analytics_24), contentDescription = "analytics")
            }


        }

    }, floatingActionButton = {

        FloatingActionButton(onClick = navScan) {
            Icon(painter = painterResource(id = android.R.drawable.ic_input_add), contentDescription = "")
        }
        }, floatingActionButtonPosition = FabPosition.Center, isFloatingActionButtonDocked = true){



        Box(
           Modifier
                .padding(vertical = 52.dp)
        ) {

             Row(
                 Modifier
                     .fillMaxWidth()
                     .offset(y = (-52).dp)
                ,
                horizontalArrangement = Arrangement.SpaceAround
            ){
                Column {
                    Text("Status", style = MaterialTheme.typography.h5)
                    Text("Good", style = MaterialTheme.typography.h6.copy(Color.DarkGray))
                }
                Spacer(modifier = Modifier.width(44.dp))

                Column {
                    Text("Checkups", style = MaterialTheme.typography.h5)
                   val checkups =  buildAnnotatedString {
                        withStyle(SpanStyle(fontSize = 42.sp)){
                            append("3")
                            withStyle(SpanStyle(fontSize = 12.sp)){
                                append("x week")
                            }
                        }
                    append('\t')
                        withStyle(SpanStyle(fontSize = 32.sp)){
                            append("25")
                            withStyle(SpanStyle(fontSize = 12.sp)){
                                append("x total")
                            }
                        }

                    }
                    Text(checkups)
                }
            }
            MolesHistory(Modifier.offset(x=0.dp,y=32.dp),moleScans)
        }
        Spacer(modifier = Modifier.height(77.dp))
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ImagedReport(){
    var moles  by remember {
        return@remember mutableStateOf(mutableListOf<Offset>())
    }

    val mask =   ImageBitmap.imageResource(id = R.drawable.body_mask)
    val maskBitmap = mask.asAndroidBitmap()
    var screenImgSize: IntSize = IntSize.Zero
    for ( m in moles){
        Chip(modifier = Modifier
            .offset(m.x.dp, m.y.dp)
            .zIndex(4f),onClick = { /*TODO*/ }) {
            Text(" + ")
        }
    }
    Box(Modifier
        .pointerInput(Unit) {
            detectTapGestures { p ->
                val bitmapScaledX =
                    ((mask.width / screenImgSize.width) * p.x) + 52.dp.toPx()
                val bitmapScaledY =
                    ((mask.height / screenImgSize.height) * p.y) + 52.dp.toPx()
                val clickedPixel =
                    maskBitmap.getPixel(bitmapScaledX.toInt(), bitmapScaledY.toInt())
                val r = android.graphics.Color.red(clickedPixel)
                val g = android.graphics.Color.green(clickedPixel)
                val b = android.graphics.Color.blue(clickedPixel)
//                clickedColor = Color(
//                    r, g, b
//                )
                if (r > g && r > b) {
                    //It is red shade // just arrange
                    moles.add(Offset(bitmapScaledX, bitmapScaledY))
                    Log.i("TAG", "HomeScreen: Red Shade")
                }

            }
        }
        .onSizeChanged {
            screenImgSize = it
        }){

        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.body_main),
            contentDescription = "main_body"
        )
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.body_mask),
            contentDescription = "click_mask"
        )

    }

}
