package te.mini_project.skincancerdetection

import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview.SurfaceProvider
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import te.mini_project.skincancerdetection.ui.screens.AuthScreen
import te.mini_project.skincancerdetection.ui.screens.HomeScreen
import te.mini_project.skincancerdetection.ui.screens.ScanScreen
import te.mini_project.skincancerdetection.ui.theme.SkinCancerDetectionTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            SkinCancerDetectionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavHost(navController, startDestination = "scan"){
                        composable("signIn"){
                            AuthScreen(signIn = {pn,smsCallback->
                                signIn(pn){smsCode->
                                    smsCallback(smsCode)
                                }
                            }, navNext = {
                                navController.navigate("home")
                            })
                        }
                        composable("home"){
                            HomeScreen{
                                navController.navigate("scan")
                            }
                        }
                        composable("scan"){
                            ScanScreen(setUpCam = { setupCam(it) })
                        }
                    }
                }
            }
        }

        FirebaseAuth.getInstance()
            .firebaseAuthSettings
            .setAppVerificationDisabledForTesting(true)
    }

    private  val TAG = "MainActivity"
    private fun signIn(phoneNumber:String, enableNext:(String)->Unit) {
        Log.i(TAG, "signIn: Signing In ...")
        val phoneOpts = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(pac: PhoneAuthCredential) {
                    Log.i(TAG, "onVerificationCompleted: ${pac.smsCode}")
                    if(pac.smsCode!=null){
                        FirebaseAuth.getInstance().signInWithCredential(pac)
                            .addOnSuccessListener {
                                enableNext(pac.smsCode!!)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@MainActivity,"Log In  Failed" , Toast.LENGTH_SHORT).show()

                            }
                    }

                }

                override fun onCodeSent(code: String, token: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(code, token)
                }

                override fun onVerificationFailed(p0: FirebaseException) {

                    Toast.makeText(this@MainActivity,"Log In  Failed" , Toast.LENGTH_SHORT).show()
                }

            })
            .setTimeout(30,TimeUnit.SECONDS)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(phoneOpts)
    }

    private fun setupCam(sv:SurfaceProvider) {
        ProcessCameraProvider.getInstance(this)
            .let { cf ->
                cf.addListener({
                    val cameraProvider = cf.get()
                    val preview = androidx.camera.core.Preview.Builder()
                        .build()
                        .also { it.setSurfaceProvider(sv) }
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA,preview)
                }catch (e:Exception){
                    e.printStackTrace()
                }
                }, ContextCompat.getMainExecutor(this))
            }
    }
}
