package te.mini_project.skincancerdetection

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview.SurfaceProvider
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import te.mini_project.skincancerdetection.data.Result
import te.mini_project.skincancerdetection.data.SkinCancerDetector
import te.mini_project.skincancerdetection.room.SkinCancerDatabase
import te.mini_project.skincancerdetection.room.models.MoleScan
import te.mini_project.skincancerdetection.ui.screens.*
import te.mini_project.skincancerdetection.ui.theme.SkinCancerDetectionTheme
import java.security.Permissions
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
class MainActivity : ComponentActivity() {

    private lateinit var skinCancerDetector : SkinCancerDetector
    private lateinit  var executors :ExecutorService
    private lateinit var composeCoroutineScope: CoroutineScope
    private lateinit var db: SkinCancerDatabase
    var reports: Map<String,Float>?=null
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        skinCancerDetector = SkinCancerDetector(this)
        executors = Executors.newFixedThreadPool(3)

        if(arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO
            ).all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED })
        requestPermissions(arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
        ),100);
        else{
            Toast.makeText(this,"Please grant permission to proceed",Toast.LENGTH_SHORT).show()
            finish()
        }

        db = SkinCancerDatabase.getInstance(applicationContext)
        setContent {
            composeCoroutineScope = rememberCoroutineScope()
            val navController = rememberNavController()
            SkinCancerDetectionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavHost(navController, startDestination = "splash"){
                        composable("splash"){
                            te.mini_project.skincancerdetection.ui.screens.SplashScreen {
                                navController.navigate("home")
                            }
                        }
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
                            HomeScreen(navAnalytics = {
                                navController.navigate("analytics")
                            }, navScan = {
                                navController.navigate("scan")
                            })
                        }
                        composable("scan"){
                            ScanScreen(setUpCam = { sv,mbss-> setupCam(sv,mbss) }){
                                val gson = GsonBuilder()
                                    .create()
                                val jsonString = gson.toJson(reports)
                                navController.navigate("show_results/$jsonString")
                                navigationgToResults = false
                            }
                        }
                        composable("show_results/{results}"){
                            val gson = GsonBuilder()
                                .create()
                            val aresults = it.arguments?.getString("results") ?: return@composable Box{}
                            val tv = object: TypeToken<Map<String, Float>>() {}
                                val results = gson.fromJson(aresults,tv)
                                    .map { e -> Result(e.key,e.value) }
                                    .toList()
                            Log.i(TAG, "onCreate: $results")
                                ResultScreen(results = results)
                        }
                        composable("analytics"){
                            AnalyticsScreen()
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

    @OptIn(ExperimentalMaterialApi::class)
    var mbss:ModalBottomSheetState?=null

    @OptIn(ExperimentalMaterialApi::class)
    private fun setupCam(sv:SurfaceProvider,mbss:ModalBottomSheetState)  : Unit{
        this.mbss = mbss
        ProcessCameraProvider.getInstance(this)
            .let { cf ->
                cf.addListener({
                    val cameraProvider = cf.get()
                    val preview = androidx.camera.core.Preview.Builder()
                        .build()
                        .also { it.setSurfaceProvider(sv) }
                    val analyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(executors,SkinCancerAnalyzer())
                        }
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA,preview,analyzer)
                }catch (e:Exception){
                    e.printStackTrace()
                }
                }, ContextCompat.getMainExecutor(this))
            }
    }



    var lastTime : Long = 0L
    var navigationgToResults = false



    inner class SkinCancerAnalyzer : ImageAnalysis.Analyzer{
        @OptIn(ExperimentalMaterialApi::class)
        override fun analyze(image: ImageProxy) {
            if(navigationgToResults){
                return
            }
            val now = System.currentTimeMillis()
            if(now - lastTime > 2000L){
                 reports =  skinCancerDetector.detect(image)
                if (reports != null) {
                    Log.i(TAG, "analyze: $reports")
                    //Some Event Trigger that will Open Results Screen
                if(reports!!.values.any { it > 0.9f })
                //Bottom Sheet and show `show results` button
                {
                    composeCoroutineScope.launch(Dispatchers.IO){
                        if(mbss?.isVisible == false)
                        {
                            //Save Generated Result
                            db.skinCancerDao().insertRecord(MoleScan().apply {
                                scanDate = Date()
                                scanResults = mapToResults(reports!!)
                            })

                            //Navigate to Results Screen
                            withContext(Dispatchers.Main){
                                navigationgToResults = true
                                mbss?.show()

                            }
                        }
                    }

                }
                }
                image.close()
                lastTime = System.currentTimeMillis()
            }else {
                image.close()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::executors.isInitialized)
            executors.shutdownNow()

        if(::skinCancerDetector.isInitialized)
            skinCancerDetector.dispose()
    }
}
