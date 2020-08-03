package com.example.chap.application.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chap.R
import com.example.chap.application.internal.SharedPref

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val s = SharedPref(this).getToken()
        if (s != "") {
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }
    }
}