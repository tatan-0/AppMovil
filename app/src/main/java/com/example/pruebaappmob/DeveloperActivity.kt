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

        // Botones Desarrollador 1 (Sebastián Olave)
        val btnGithub1 = findViewById<Button>(R.id.btnGithub1)
        val btnLinkedin1 = findViewById<Button>(R.id.btnLinkedin1)

        // Botones Desarrollador 2 (Matías Cortés)
        val btnGithub2 = findViewById<Button>(R.id.btnGithub2)
        val btnLinkedin2 = findViewById<Button>(R.id.btnLinkedin2)

        // Botón Volver al Menú
        val btnBack = findViewById<Button>(R.id.btnBack)

        // Enlaces Desarrollador 1
        btnGithub1.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com"))
            startActivity(intent)
        }

        btnLinkedin1.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://linkedin.com"))
            startActivity(intent)
        }

        // Enlaces Desarrollador 2
        btnGithub2.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com"))
            startActivity(intent)
        }

        btnLinkedin2.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://linkedin.com"))
            startActivity(intent)
        }

        // Volver al menú principal
        btnBack.setOnClickListener {
            finish()
        }
    }
}