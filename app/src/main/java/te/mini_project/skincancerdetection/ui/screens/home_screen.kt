package te.mini_project.skincancerdetection.ui.screens

import UserProfileDialog
import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import te.mini_project.skincancerdetection.R
import te.mini_project.skincancerdetection.ui.composables.MolesHistory
import te.mini_project.skincancerdetection.room.SkinCancerDatabase
import te.mini_project.skincancerdetection.room.models.MoleScan

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun HomeScreen(navScan:()->Unit,navAnalytics:()->Unit,getNavController: ()->NavController){
    val scaffoldHost = rememberScaffoldState()
    var user:FirebaseUser? by remember {
        mutableStateOf(null)
    }
    var showProfile  by remember {
        mutableStateOf(false)
    }
    var moleScans by remember {
        mutableStateOf(listOf<MoleScan>())
    }
    FirebaseAuth.getInstance().addAuthStateListener {
        user = it.currentUser
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
        TopAppBar(
           contentColor = Color.White,
            actions = {
                val dynamicColor = AndroidColor.argb(255,(255 * Math.random()).toInt(),(255 * Math.random()).toInt(),(255 * Math.random()).toInt())

                IconButton(onClick = {
                        GlobalScope.launch {
                            scaffoldHost.snackbarHostState.showSnackbar("Refreshing")
                            withContext(Dispatchers.IO){
                                val db = SkinCancerDatabase.getInstance(context)
                                db.skinCancerDao().observeMolesRecord().collect{
                                    moleScans = it
                                }
                            }
                        }

                    }) {
                        Icon(painter = painterResource(id = android.R.drawable.stat_notify_sync), contentDescription = "send mail icon")
                    }
                    if(user==null){
                        Box(
                            Modifier
                                .clickable {
                                    showProfile = true
                                }
                                .clip(CircleShape)
                                .background(color = Color(dynamicColor))
                        ) {
                            Text(modifier = Modifier.padding(12.dp),text="GT", style = TextStyle(Color.White))
                        }
                    }
                    else{
                        Image(

                            painter = rememberImagePainter(user!!.photoUrl),
                            contentDescription = user!!.displayName!!.first().toString(),
                            modifier = Modifier
                                .size(45.dp)
                                .clip(CircleShape)
                                .clickable {
                                    showProfile = true
                                }
                        )
                    }

            },
            title = {
                Row{
                    Icon(modifier = Modifier.size(36.dp),painter = painterResource(id = R.drawable.baseline_home_24), contentDescription = "")
                    Text(" Home",style= TextStyle(fontSize = 32.sp))

                }

            }
        )
    }
    ,
    bottomBar = {
        BottomAppBar(
            cutoutShape = CircleShape,
            modifier = Modifier.fillMaxWidth(),

        ) {

            IconButton(modifier = Modifier.weight(1f),onClick = {


            }) {
                Icon(painterResource(id = R.drawable.baseline_home_24), tint = Color.White, contentDescription = "home")
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



//        Box(
//           Modifier
//                .padding(vertical = 52.dp)
//        ) {
//
//             Row(
//                 Modifier
//                     .fillMaxWidth()
//                     .offset(y = (-52).dp)
//                ,
//                horizontalArrangement = Arrangement.SpaceAround
//            ){
//                Column {
//                    Text("Status", style = MaterialTheme.typography.h5)
//                    Text("Good", style = MaterialTheme.typography.h6.copy(Color.DarkGray))
//                }
//                Spacer(modifier = Modifier.width(44.dp))
//
//                Column {
//                    Text("Checkups", style = MaterialTheme.typography.h5)
//                   val checkups =  buildAnnotatedString {
//                        withStyle(SpanStyle(fontSize = 42.sp)){
//                            append("3")
//                            withStyle(SpanStyle(fontSize = 12.sp)){
//                                append("x week")
//                            }
//                        }
//                    append('\t')
//                        withStyle(SpanStyle(fontSize = 32.sp)){
//                            append("25")
//                            withStyle(SpanStyle(fontSize = 12.sp)){
//                                append("x total")
//                            }
//                        }
//
//                    }
//                    Text(checkups)
//                }
//            }
            MolesHistory(moleScans = moleScans, getNavController = getNavController, mod = Modifier)
            if(showProfile && user!=null){
                UserProfileDialog(user = user!!){
                    showProfile = false
                }
            }
        }
        Spacer(modifier = Modifier.height(77.dp))
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
