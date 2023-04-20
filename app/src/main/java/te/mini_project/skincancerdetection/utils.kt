package te.mini_project.skincancerdetection

import android.content.Context
import android.graphics.*
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import androidx.camera.core.ImageProxy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import te.mini_project.skincancerdetection.data.Result
import te.mini_project.skincancerdetection.data.SkinCancerModelLabeler
import te.mini_project.skincancerdetection.room.models.MoleScan
import java.io.ByteArrayOutputStream
import java.sql.Time
import java.util.*


fun Context.isNetworkEnabled(){

}

fun Int.onlyIntUppersString() = if(this > 0 ) this.toString() else ""
@androidx.camera.core.ExperimentalGetImage
fun  imageProxyToBitmap(imageProxy: ImageProxy, cropRect: Rect?): Bitmap? {
    val image = imageProxy.image ?: return null
    try {
        // Get the YUV image planes
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        val nv21 = ByteArray(ySize + uSize + vSize)
        // Convert the image data from YUV to NV21 format
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)
        // Create the bitmap from the NV21 data
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        if (cropRect == null) {
            yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, out)
        } else {
            yuvImage.compressToJpeg(cropRect, 100, out)
        }
        val bytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        image.close()
        imageProxy.close()
    }
    return null
}


//private fun getColor(x: Float, y: Float): Long {
//    return if (x < 0 || y < 0 || x > getWidth().toFloat() || y > getHeight().toFloat()) {
//        0 //Invalid, return 0
//    } else {
//        //Convert touched x, y on View to on Bitmap
//        val xBm = (x * (bmWidth / getWidth().toDouble())) as Int
//        val yBm = (y * (bmHeight / getHeight().toDouble())) as Int
//        mask.getPixel(xBm, yBm)
//    }
//}
val SAFE_SKIN_DISEASES = arrayOf(
    SkinCancerModelLabeler.getSkinClass(0),
    SkinCancerModelLabeler.getSkinClass(2),
    SkinCancerModelLabeler.getSkinClass(3),
    SkinCancerModelLabeler.getSkinClass(5),
    SkinCancerModelLabeler.getSkinClass(6),
)
typealias  ShowResultBSCallback = (Color, String) -> Unit

fun String.spaced():String{
    val str = if(!contains(' ') && contains(Regex("[A-Z]"))) {
        val capLet = find { Regex("[A-Z]").matches(it.toString()) }
        split(Regex("[A-Z]")).joinToString(" $capLet")
    }
    else this

    return str.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.ENGLISH) else it.toString() }
}

 public val mockResults  = listOf<Result>(
    Result("dvfvdfsvd",0.9f),
    Result("vdsvdvdv",0.85f),
    Result("dvdvdsv",0.7f),
    Result("dvdvdvdsvdv",0.4f),
    Result("dvdsvdvv",0.2f),
)

public  val mockScans = buildList<MoleScan> {
   for (i in (0..10)){
     add(
      MoleScan().apply {
         id = 1
         scanDate = Date(System.currentTimeMillis())
         scanResults = mockResults
      })
   }
}

fun mapToResults(map: Map<String,Float>):List<Result>{
    val results = mutableListOf<Result>()
    for((k,v) in map){
        results.add(Result(k,v))
    }
    return  results
}