package com.ait.shoppinglistapp

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val showAnim = AnimationUtils.loadAnimation(this,
            R.anim.icon_anim)
        ivCart.startAnimation(showAnim)

        scheduleSplashScreen()
    }

    private fun scheduleSplashScreen() {
        val splashScreenDuration = getSplashScreenDuration()
        Handler().postDelayed(
            {
                // After the splash screen duration, route to the right activities
                //ScrollingActivity.start(this)

                val intent = Intent(this,
                    ScrollingActivity::class.java)
                startActivity(intent)
            },
            splashScreenDuration
        )
    }



    private fun getSplashScreenDuration() = 4000L

}