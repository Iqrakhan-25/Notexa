package com.example.notexa

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val splashDelay: Long = 3000 // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Animate dots
        val dots = listOf<View>(
            findViewById(R.id.dot1),
            findViewById(R.id.dot2),
            findViewById(R.id.dot3),
            findViewById(R.id.dot4)
        )

        dots.forEachIndexed { index, dot ->
            val anim = AlphaAnimation(0.2f, 1f).apply {
                duration = 500
                startOffset = (index * 300).toLong() // stagger effect
                repeatMode = Animation.REVERSE
                repeatCount = Animation.INFINITE
            }
            dot.startAnimation(anim)
        }

        // Navigate to MainActivity after delay
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, splashDelay)
    }
}
