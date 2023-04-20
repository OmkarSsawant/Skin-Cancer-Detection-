package te.mini_project.skincancerdetection.ui.screens

import android.annotation.SuppressLint
import androidx.camera.core.Preview.SurfaceProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch
import te.mini_project.skincancerdetection.ShowResultBSCallback
import te.mini_project.skincancerdetection.ui.composables.ResultModalBottomSheet

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LiveScanScreen(setUpCam:(SurfaceProvider, ShowResultBSCallback)->Unit, navToResults:()->Unit){
    val mbss = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var sheetTitle by remember{ mutableStateOf("") }
    var sheetTitleColor by remember{ mutableStateOf(Color.White) }
    val coroutineScope = rememberCoroutineScope()
    ResultModalBottomSheet(btnState = mbss, title = sheetTitle, btnText = "Show Results",
        navToResults = navToResults
    , color = sheetTitleColor) {

        Scaffold {
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
        }

    }
}
