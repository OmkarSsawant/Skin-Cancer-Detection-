package te.mini_project.skincancerdetection.ui.screens

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.selects.select
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import org.json.JSONObject
import te.mini_project.skincancerdetection.R
import te.mini_project.skincancerdetection.data.Details
import te.mini_project.skincancerdetection.data.Response
import te.mini_project.skincancerdetection.data.Result
import te.mini_project.skincancerdetection.spaced
import te.mini_project.skincancerdetection.ui.theme.SkinCancerDetectionTheme
import java.io.File
import java.lang.reflect.Type
import kotlin.reflect.full.memberProperties

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReportDetailsScreen(res:Result){

    val captureController = rememberCaptureController()
    val gson = Gson()
    val type  = object: TypeToken<Response>() {}
   val result =  gson.fromJson(LocalContext.current.assets.open("cancer_details.json").bufferedReader().readText(),type)
    Log.i("TAG", "ReportDetailsScreen: $result \n ${res.diseaseName.split(',')[0]}")
    val details:Details  = when(res.diseaseName.split(',')[0]){
        "bcc" -> result.bcc
        "df" -> result.df
        "bkl" -> result.bkl
        "akiec" -> result.akiec
        "mel" -> result.mel
        "nv" -> result.nv
        "vasc" -> result.vasc
        else -> { Details() }
    }

    Scaffold {
        val ctx = LocalContext.current.applicationContext

        it.calculateBottomPadding()
        Column {
            Row(horizontalArrangement = Arrangement.SpaceAround) {
                Text(" Report ",style= TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold))
                IconButton(onClick = {
                    captureController.capture()
                }) {
                    Icon(painter = painterResource(id = R.drawable.baseline_share_24), contentDescription = "Share")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Capturable(
                controller = captureController,
                onCaptured = { bitmap, error ->
                    // This is captured bitmap of a content inside Capturable Composable.
                    if (bitmap != null) {
                        // Bitmap is captured successfully. Do something with it!
                        val bm = bitmap.asAndroidBitmap()
                        val cv = ContentValues()
                        cv.put(Media.DISPLAY_NAME,System.currentTimeMillis())
                        cv.put(Media.MIME_TYPE,"image/jpeg")
                        val uri = ctx.contentResolver.insert(Media.EXTERNAL_CONTENT_URI,cv)!!
                        val os = ctx.contentResolver.openOutputStream(uri)
                        bm.compress(Bitmap.CompressFormat.JPEG,100,os)
                        os?.close()
                        val  share: Intent = Intent(Intent.ACTION_SEND)
                        share.type = "image/*"
                        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        share.putExtra(Intent.EXTRA_STREAM,uri)
                        ctx.startActivity(share)
                    }

                    if (error != null) {
                        // Error occurred. Handle it!
                        Log.e("TAG", "ReportDetailsScreen: ", error)
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 8.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo")
                            Text("Detected Result",style= TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Medium))
                            Card(Modifier.padding(12.dp),elevation = 12.dp){
                                ListItem(text =  {
                                    Button(
                                        onClick = {  }) {
                                        Text("Details")
                                    }
                                }, secondaryText = { LinearProgressIndicator( modifier = Modifier.padding(12.dp),
                                    progress = res.accuracy
                                )}, trailing = { Text("${res.accuracy/1f * 100f}  %")}, overlineText = {
                                    Text(res.diseaseName, style = MaterialTheme.typography.h5)
                                })
                            }
                            Text("Advice",style= TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold))
                            for(prop in Details::class.memberProperties){
                                Text(modifier = Modifier.padding(12.dp),text =buildAnnotatedString {
                                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                                    append(prop.name.spaced())
                                    append(" :  ")
                                    pop()
                                    pushStyle(SpanStyle(fontWeight = FontWeight.Thin))
                                    append(prop.get(details).toString())
                                    pop()
                                })
                            }
                        }
                    }
                }
            }
        }
            }


}


@Preview
@Composable
fun ReportDetailsScreenPreview(){
    SkinCancerDetectionTheme {
        ReportDetailsScreen(res = Result("Sample Disease", .9f))
    }
}