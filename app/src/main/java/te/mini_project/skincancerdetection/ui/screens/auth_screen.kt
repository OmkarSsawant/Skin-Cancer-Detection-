package te.mini_project.skincancerdetection.ui.screens

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
import te.mini_project.skincancerdetection.Greeting
import te.mini_project.skincancerdetection.MainActivity
import te.mini_project.skincancerdetection.onlyIntUppersString
import te.mini_project.skincancerdetection.ui.theme.SkinCancerDetectionTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AuthScreen(mainActivity: MainActivity){
    val name by remember{ mutableStateOf("") }
    val phoneNumber by remember{ mutableStateOf(-1) }
    val otp by remember{ mutableStateOf(-1) }
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

            }, placeholder = { Text("Enter Your Name") }, label = {Text("Name")})
            TextField(value = phoneNumber.onlyIntUppersString(), onValueChange = {
                otpVisible = it.length ==10
            }, placeholder = { Text("Enter Your Phone Number") }, label = {Text("Phone Number")})
            AnimatedVisibility(visible = otpVisible) {
                TextField(value = otp.onlyIntUppersString(), onValueChange = {
                }, placeholder = { Text("Enter Received OTP") }, label = {Text("Name")})

            }
            Button(onClick = {
                if(btnText == "Sign In"){
                    mainActivity.signIn("+91 1234 567 891")
                }else{
                    //home screen

                }
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
        AuthScreen()
    }
}