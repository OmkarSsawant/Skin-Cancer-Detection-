package te.mini_project.skincancerdetection

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import java.sql.Time
import java.util.*


fun Int.onlyIntUppersString() = if(this > 0 ) this.toString() else ""

fun ImageProxy.toBitmap(): Bitmap {
    val buffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    close()
    return bitmap
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