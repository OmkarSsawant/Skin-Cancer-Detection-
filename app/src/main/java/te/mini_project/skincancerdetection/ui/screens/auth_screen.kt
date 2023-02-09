package te.mini_project.skincancerdetection.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import te.mini_project.skincancerdetection.MainActivity
import te.mini_project.skincancerdetection.onlyIntUppersString
import te.mini_project.skincancerdetection.ui.theme.SkinCancerDetectionTheme

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AuthScreen(signIn:(String,(String)->Unit)->Unit,navNext:()->Unit){
    var name by remember{ mutableStateOf("") }
    var phoneNumber by remember{ mutableStateOf("") }
    var otp by remember{ mutableStateOf("") }
    var otpVisible by remember {
        mutableStateOf(false)
    }
    var btnText by remember {
        mutableStateOf("Sign In")
    }

    Scaffold {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
                ){
            Text(text = "Sign In", style = TextStyle(
                fontSize = 52.sp
            ))

            TextField(value = name, onValueChange = {
                name = it
            }, placeholder = { Text("Enter Your Name") }, label = {Text("Name")})
            TextField(value = phoneNumber, onValueChange = {
                    otpVisible = it.length ==10
                    phoneNumber = it
            }, placeholder = { Text("Enter Your Phone Number") }, label = {Text("Phone Number")})
            AnimatedVisibility(visible = otpVisible) {
                TextField(value = otp, onValueChange = {
                                                       otp = it
                }, placeholder = { Text("Enter Received OTP") }, label = {Text("OTP")})

            }
            Button(onClick = {
//                if(btnText == "Sign In"){
//                    signIn("+1 650-555-1212"){
//                        otpVisible = true
//                        otp = it
//                        btnText = "Next"
//                    }
//                }else{
//                    //home screen
//
//                }
                navNext()
            }) {
                    Text(text =btnText)
            }





        }
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SkinCancerDetectionTheme {
    }
}