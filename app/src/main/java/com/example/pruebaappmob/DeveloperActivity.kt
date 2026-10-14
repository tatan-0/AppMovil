package com.example.pruebaappmob

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DeveloperActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)

        val btnGithub = findViewById<Button>(R.id.btnGithub)
        val btnLinkedin = findViewById<Button>(R.id.btnLinkedin)
        val btnBack = findViewById<Button>(R.id.btnBack)

        // Enlace activo a GitHub
        btnGithub.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com"))
            startActivity(intent)
        }

        // Enlace activo a LinkedIn
        btnLinkedin.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://linkedin.com"))
            startActivity(intent)
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}