package te.mini_project.skincancerdetection

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log

@SuppressLint("CustomSplashScreen")
class SplashScreen : Activity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.splash_screen)
        Log.i("Splash Screen", "onCreate: In Splash Screen" )
        Handler().postDelayed({
            startActivity(Intent(this,MainActivity::class.java))
        },2_000)

    }
}