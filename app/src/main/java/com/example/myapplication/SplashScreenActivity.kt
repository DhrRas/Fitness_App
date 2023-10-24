package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.myapplication.activity.LoginActivity
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.global.DB
import com.example.myapplication.manager.SessionManager

class SplashScreenActivity : AppCompatActivity() {
    private var mDelayHandler: Handler? = null
    private val splash_delay: Long = 3000
    var db: DB? = null
    var session: SessionManager? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DB(this)
        session = SessionManager(this)

        insertAdminData()

        mDelayHandler = Handler()
        mDelayHandler?.postDelayed(mRunnable, splash_delay)
    }

    private val mRunnable: Runnable = Runnable {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun insertAdminData() {
        try {
            val sqlCheck = "SELECT * FROM ADMIN"
            db?.fireQuery(sqlCheck)?.use {
                if (it.count > 0) {
                    Log.d("SplashActivity", "Data Available.")
                } else {
                    val sqlQuery =
                        "INSERT OR REPLACE INTO ADMIN(ID, USER_NAME, PASSWORD, MOBILE)VALUES('1','Admin','123456','8888888888')"
                    db?.executeQuery(sqlQuery)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDelayHandler?.removeCallbacks(mRunnable)
    }
}