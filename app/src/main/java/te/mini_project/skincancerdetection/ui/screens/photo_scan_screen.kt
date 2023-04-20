package te.mini_project.skincancerdetection.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import te.mini_project.skincancerdetection.MainActivity.Companion.TAG
import te.mini_project.skincancerdetection.SAFE_SKIN_DISEASES
import te.mini_project.skincancerdetection.ShowResultBSCallback
import te.mini_project.skincancerdetection.data.SkinCancerDetector
import te.mini_project.skincancerdetection.mapToResults
import te.mini_project.skincancerdetection.room.models.MoleScan
import te.mini_project.skincancerdetection.ui.composables.ResultModalBottomSheet
import te.mini_project.skincancerdetection.ui.theme.Black200
import te.mini_project.skincancerdetection.ui.theme.Black500
import te.mini_project.skincancerdetection.vm.SkinCancerDetectorVM
import java.util.*
import java.util.concurrent.Executor

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "RestrictedApi")
@Composable
fun ScanScreen(setUpCam:(Preview.SurfaceProvider, ShowResultBSCallback)->Unit,getCapturer:()-> ImageCapture?,executor: Executor,detector: SkinCancerDetector,vm : SkinCancerDetectorVM,setReports:( Map<String, Float>?)->Unit, navToResults:()->Unit){
    val mbss = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var sheetTitle by remember{ mutableStateOf("") }
    var sheetTitleColor by remember{ mutableStateOf(Color.White) }
    val coroutineScope = rememberCoroutineScope()
    var scanImage by remember {
        mutableStateOf<ImageBitmap?>(null)
    }
    val scaffoldHost = rememberScaffoldState()
    ResultModalBottomSheet(btnState = mbss, title = sheetTitle, btnText = "Show Results",
        navToResults = navToResults
    , color = sheetTitleColor,image=scanImage) {

        Scaffold(

            floatingActionButtonPosition = FabPosition.Center,
            scaffoldState = scaffoldHost,
            floatingActionButton = {
               val ctx =  LocalContext.current
                ExtendedFloatingActionButton(
                    text = { Text("Scan") },
                    onClick = {
                        //capture -> scan -> show
                        getCapturer()?.takePicture(executor,object :
                            ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                super.onCaptureSuccess(image)
                                Log.i(TAG, "onCaptureSuccess: YES")
                                val res = detector.detectCropped(image)
                                val (reports,sImage)  =res ?: return
                                scanImage = sImage?.asImageBitmap()
                                Log.i(TAG, "onCaptureSuccess: $scanImage")

                                reports?.let { map ->
                                    setReports(map)
                                    val sus = map.maxBy { it.value }
                                    if (sus.value > 0.8f) {
                                        //Check For Cancerous
                                        if (sus.key in SAFE_SKIN_DISEASES) {
                                            sheetTitle = "You are Safe!"
                                            sheetTitleColor = Black500
                                            coroutineScope.launch {
                                                mbss.show()
                                            }
                                        } else {
                                            sheetTitle = "You need to consult Doctor!"
                                            sheetTitleColor = Color.Red
                                            coroutineScope.launch(Dispatchers.IO) {
                                                //Save Generated Result
                                                val ms = MoleScan().apply {
                                                    scanDate = Date()
                                                    scanResults = mapToResults(reports)
                                                }
                                                if(vm.networkConnected())
                                                    vm.saveMoleRecord(ms)
                                                else{
                                                    scaffoldHost.snackbarHostState.showSnackbar("Kindly enable network and rescan to save")
                                                }
                                                coroutineScope.launch {
                                                    mbss.show()
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            override fun onError(exception: ImageCaptureException) {
                                super.onError(exception)
                                Log.e("pss", "onError: $exception", )
                                coroutineScope.launch (Dispatchers.Main){
                                    Toast.makeText(ctx,exception.localizedMessage,Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    },
                icon = {
                    Icon(Icons.Filled.Search,contentDescription = null)
                })
            }
        ) {
            Box{
                AndroidView(modifier = Modifier.fillMaxSize(), factory = {
                    val cameraPreviewView = PreviewView(it)
                    setUpCam(cameraPreviewView.surfaceProvider) { c, s ->
                        sheetTitle = s
                        sheetTitleColor = c
                        coroutineScope.launch {
                            if(!mbss.isVisible)
                                mbss.show()
                        }
                    }
                    cameraPreviewView
                })
                Box(Modifier.fillMaxSize(),contentAlignment = Alignment.Center){
                        AnimatedScanBox(size = DpSize(200f.dp, 200f.dp), lineSize = DpSize(200f.dp,10.dp))
                }

            }

        }

    }
}


@Composable
fun AnimatedScanBox(size: DpSize,lineSize:DpSize){
    val infiniteTransition = rememberInfiniteTransition()
    val offsetAnimation by infiniteTransition.animateFloat(
        initialValue = 14f,
        targetValue = size.height.value - (lineSize.height.value + 14f),
        animationSpec = infiniteRepeatable(
            animation = tween(1000,200),
            repeatMode = RepeatMode.Reverse
        )
    )

    val alphaAnimation by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000,200),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        Modifier
            .size(size)
            .border(2.dp, Black200, RoundedCornerShape(12.dp))

    ){
     Box(
         Modifier
             .size(lineSize)
             .offset(y = offsetAnimation.dp)
             .background(Black500)
             .alpha(alphaAnimation)
             .clip(RoundedCornerShape(CornerSize(12.dp)))
     ) {}//line
    }
}
