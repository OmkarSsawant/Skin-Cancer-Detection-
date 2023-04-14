package te.mini_project.skincancerdetection.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.Camera
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraX
import androidx.camera.core.Preview.SurfaceProvider
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import te.mini_project.skincancerdetection.ShowResultBSCallback
import te.mini_project.skincancerdetection.ui.composables.ResultModalBottomSheet

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ScanScreen(setUpCam:(SurfaceProvider,ShowResultBSCallback)->Unit,navToResults:()->Unit){
    val mbss = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var sheetTitle by remember{ mutableStateOf("") }
    var sheetTitleColor by remember{ mutableStateOf(Color.White) }
    val coroutineScope = rememberCoroutineScope()
    ResultModalBottomSheet(btnState = mbss, title = sheetTitle, btnText = "Show Results",
        navToResults = navToResults
    ) {

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
