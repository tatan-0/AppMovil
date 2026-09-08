package com.example.pruebaappmob

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvDateTime = findViewById<TextView>(R.id.tvDateTime)
        val btnCrudUser = findViewById<Button>(R.id.btnCrudUser)
        val btnSensors = findViewById<Button>(R.id.btnSensors)
        val btnDeveloper = findViewById<Button>(R.id.btnDeveloper)

        val formatter = SimpleDateFormat("dd-MM-yyyy, HH:mm:ss", Locale.getDefault())
        tvDateTime.text = "Fecha/Hora: " + formatter.format(Date())

        btnCrudUser.setOnClickListener {
            startActivity(Intent(this, UserManagementActivity::class.java))
        }

        btnSensors.setOnClickListener {
            startActivity(Intent(this, SensorsActivity::class.java))
        }

        btnDeveloper.setOnClickListener {
            startActivity(Intent(this, DeveloperActivity::class.java))
        }
    }
}