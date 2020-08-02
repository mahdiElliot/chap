package com.example.chap.application.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chap.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

    }
}