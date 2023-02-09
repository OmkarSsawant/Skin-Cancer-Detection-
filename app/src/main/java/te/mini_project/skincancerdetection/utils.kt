package te.mini_project.skincancerdetection

import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth


fun Int.onlyIntUppersString() = if(this > 0 ) this.toString() else ""

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