package te.mini_project.skincancerdetection

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import te.mini_project.skincancerdetection.ui.theme.SkinCancerDetectionTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkinCancerDetectionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }

        FirebaseAuth.getInstance()
            .firebaseAuthSettings
            .setAppVerificationDisabledForTesting(true)
    }

    fun signIn(phoneNumber:String) {
        val phoneOpts = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(pac: PhoneAuthCredential) {
                        FirebaseAuth.getInstance().signInWithCredential(pac)
                            .addOnSuccessListener {
                                    nextScreen()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@MainActivity,"Log In  Failed" , Toast.LENGTH_SHORT).show()

                            }
                }

                override fun onVerificationFailed(p0: FirebaseException) {

                    Toast.makeText(this@MainActivity,"Log In  Failed" , Toast.LENGTH_SHORT).show()
                }

            })
            .setTimeout(30,TimeUnit.SECONDS)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(phoneOpts)
    }

    private fun nextScreen() {

    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SkinCancerDetectionTheme {
        Greeting("Android")
    }
}