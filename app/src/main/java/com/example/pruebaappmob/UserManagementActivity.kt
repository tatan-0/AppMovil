package com.example.pruebaappmob

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class UserManagementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)

        val btnIngresar = findViewById<Button>(R.id.btnIngresarUsuario)
        val btnListar = findViewById<Button>(R.id.btnListarUsuarios)

        btnIngresar.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnListar.setOnClickListener {
            startActivity(Intent(this, UserListActivity::class.java))
        }
    }
}