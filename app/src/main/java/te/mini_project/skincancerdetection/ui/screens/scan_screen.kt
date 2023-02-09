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
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ScanScreen(setUpCam:(SurfaceProvider)->Unit){

    Scaffold {
        Box {
            AndroidView(modifier = Modifier.fillMaxSize(), factory = {
                val cameraPreviewView = PreviewView(it)
                setUpCam(cameraPreviewView.surfaceProvider)
                cameraPreviewView
            })
        Card(
            Modifier
//                .offset(x = 50.dp, y = 800.dp)
                .fillMaxWidth(.9f)
                .align(Alignment.BottomCenter)
                .padding(52.dp)
                .background(Color.DarkGray)
            ,
            shape = RoundedCornerShape(12.dp),
            backgroundColor = Color.DarkGray
        ) {
            Row {
                Column {
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    ) {
                    }
                    Text(text = "Focus")
                }
                Column {
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    ) {
                    }
                    Text(text = "Clarity")

                }
                Column {
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .background(Color.Green)
                    ) {
                    }
                    Text(text = "Detection")

                }
            }
        }



        }
    }
}