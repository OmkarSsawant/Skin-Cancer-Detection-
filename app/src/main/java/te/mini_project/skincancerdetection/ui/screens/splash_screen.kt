package te.mini_project.skincancerdetection.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import te.mini_project.skincancerdetection.R

@Composable
fun SplashScreen(navHome:()->Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
      Image(painter = painterResource(id = R.drawable.logo),contentDescription = "Splash Screen Logo")
    }
    LaunchedEffect(key1 = ""){
        delay(2000)
        navHome()
    }
}