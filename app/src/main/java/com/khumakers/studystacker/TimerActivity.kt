package com.khumakers.studystacker

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.khumakers.studystacker.databinding.ActivityTimerBinding
import java.util.Timer
import kotlin.concurrent.timer

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class TimerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimerBinding
    private val hideHandler = Handler(Looper.myLooper()!!)
    @SuppressLint("InlinedApi")

    var subjectName : String = ""
    var timeClock : Int = 0
    var innerCounter : Int = 0
    private var timerTask : Timer? = null
    var timestart : Long = 0

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setFullScreen()
        MainActivity.mainActivity?.finish();

        subjectName = intent.getStringExtra("subjectName") as String
        timeClock = intent.getIntExtra("timeClock", 0) as Int
        innerCounter = intent.getIntExtra("innerCounter", 0) as Int

        binding.exitButton.setOnClickListener {
            timerTask?.cancel();
            timeClock += (SystemClock.elapsedRealtime() - timestart).toInt()/1000;

            val intent = Intent(this@TimerActivity, MainActivity::class.java)
            intent.putExtra("timeClock", timeClock)
            intent.putExtra("innerCounter", innerCounter)
            startActivity(intent);

            finish();
        }

        binding.subjectName.text = subjectName
        binding.timeClock.text = timeClock.toTimestamp()

        startTimer()
        timestart = SystemClock.elapsedRealtime()
    }

    private fun startTimer() {
        timerTask = timer(period = 20) {
            runOnUiThread{
                binding.timeClock.text = ((SystemClock.elapsedRealtime() - timestart).toInt()/1000 + timeClock).toTimestamp()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun setFullScreen() {
        // Android 11(R) 대응
        // 상단 Action Bar를 사라지게한다
        supportActionBar?.hide()

        // 이 값이 True이면 내부적으로 SYSTEM UI LAYOUT FLAG 값들을 살펴보고
        // 해당 설정값들을 토대로 화면을 구성하게 된다.
        // 따라서 False로 설정을 해주어야 이제 사라질(Deprecated) Flag 값들을
            // 무시하고 Window Insets를 통해 화면을 구성하게 된다
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            if (controller != null) {
                // 상태바와 네비게이션을 사라지게 한다
                controller.hide(
                    WindowInsets.Type.statusBars() or
                            WindowInsets.Type.navigationBars()
            )
            // 특정 행동(화면 끝을 스와이프하는 등)을 했을 때에만 시스템바가 나타나도록 설정
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}