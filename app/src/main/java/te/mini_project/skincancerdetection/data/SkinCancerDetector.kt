package te.mini_project.skincancerdetection.data


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.hardware.SensorManager
import android.media.Image
import android.view.OrientationEventListener
import androidx.camera.core.ImageProxy
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import te.mini_project.skincancerdetection.ml.Model
import te.mini_project.skincancerdetection.toBitmap
import java.io.ByteArrayOutputStream

class SkinCancerDetector(context:Context) : OrientationEventListener(context,SensorManager.SENSOR_DELAY_NORMAL) {
    private val skinModel by lazy {
        Model.newInstance(context)
    }
    private var orientation:Int = 0

    @SuppressLint("UnsafeOptInUsageError")
    fun detect(imgP: ImageProxy): Map<String, Float>? {
        val mBITMAP = imgP.toBitmap() ?: return null
        return detectBitmap(mBITMAP)
    }

    private fun detectBitmap(bitmap: Bitmap):Map<String,Float>?{
        val modelInput: TensorBuffer = prepareModelInput(bitmap) ?: return null
        val outputs = skinModel.process(modelInput)
        return mapToRecognitions(outputs)
    }
    private fun mapToRecognitions(outputs: Model.Outputs): Map<String,Float> {
        val mOutputs = mutableMapOf<String,Float>()
        outputs.outputFeature0AsTensorBuffer.floatArray.forEachIndexed{i,confidence ->
            mOutputs[SkinCancerModelLabeler.getSkinClass(i)] = confidence
        }
        return mOutputs
    }

    private fun prepareModelInput(mBITMAP:Bitmap): TensorBuffer? {
        val resized = Bitmap.createScaledBitmap(mBITMAP,224,224,true)
        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(resized)
        return tensorImage.tensorBuffer
    }



    fun dispose(){
        skinModel.close()
    }

    companion object{
        private const val TAG = "SkinCancerDetector"
    }

    override fun onOrientationChanged(orientation: Int) {
        this.orientation  = orientation
    }
}

fun Image.toBitmap(): Bitmap {
    val yBuffer = planes[0].buffer // Y
    val vuBuffer = planes[2].buffer // VU

    val ySize = yBuffer.remaining()
    val vuSize = vuBuffer.remaining()

    val nv21 = ByteArray(ySize + vuSize)

    yBuffer.get(nv21, 0, ySize)
    vuBuffer.get(nv21, ySize, vuSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}