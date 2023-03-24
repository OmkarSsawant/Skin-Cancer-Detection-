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

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ScanScreen(setUpCam:(SurfaceProvider,ModalBottomSheetState)->Unit,navToResults:()->Unit){
    val mbss = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    ResultModalBottomSheet(btnState = mbss, barColor = Color.Red, text = "Sorry! \n U r f", btnText = "Show Results",
        navToResults = navToResults
    )
    {
        Scaffold {
            Box {
                AndroidView(modifier = Modifier.fillMaxSize(), factory = {
                    val cameraPreviewView = PreviewView(it)
                    setUpCam(cameraPreviewView.surfaceProvider,mbss)
                    cameraPreviewView
                })
                Card(
                    Modifier
//                .offset(x = 50.dp, y = 800.dp)
                        .fillMaxWidth(.9f)
                        .align(Alignment.BottomCenter)
                        .defaultMinSize(minHeight = 100.dp)
                        .padding(52.dp)
                        .background(Color.DarkGray)
                    ,
                    shape = RoundedCornerShape(24.dp),
                    backgroundColor = Color.DarkGray
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            verticalArrangement = Arrangement.SpaceEvenly
                            , horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                Modifier
                                    .clip(CircleShape)
                                    .size(12.dp)
                                    .background(Color.White)
                                    .padding(20.dp)
                            ) {
                            }
                            Text(text = "Focus")
                        }
                        Column(
                            verticalArrangement = Arrangement.SpaceEvenly
                            , horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Box(
                                Modifier
                                    .clip(CircleShape)
                                    .size(12.dp)
                                    .background(Color.White)
                                    .padding(20.dp)
                            ) {
                            }
                            Text(text = "Clarity")

                        }
                        Column(
                            verticalArrangement = Arrangement.SpaceEvenly
                            , horizontalAlignment = Alignment.CenterHorizontally

                        ) {
                            Box(
                                Modifier
                                    .clip(CircleShape)
                                    .size(12.dp)
                                    .background(
                                        Color.White
                                    )
                                    .padding(20.dp)
                            ) {
                            }
                            Text(text = "Detection")

                        }
                    }
                }


            }
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ResultModalBottomSheet(navToResults:()->Unit,btnState: ModalBottomSheetState,barColor: Color,text:String,btnText:String,content:@Composable ()->Unit){


   ModalBottomSheetLayout(sheetState = btnState,sheetContent = {
       Box(
           Modifier
               .fillMaxWidth(.8f)
               .height(24.dp)
               .background(barColor)
               .clip(RoundedCornerShape(12.dp))
               .align(Alignment.CenterHorizontally)
       ){}
       Spacer(modifier = Modifier.height(20.dp))
       Text(modifier = Modifier.align(Alignment.CenterHorizontally),text=text)
       Spacer(modifier = Modifier.height(20.dp))
       Button(modifier = Modifier.align(Alignment.CenterHorizontally),onClick = navToResults){
            Text(btnText)
       }
   }) {
    content()
   }
}