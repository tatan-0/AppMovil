package com.example.pruebaappmob

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class UserManagementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)

        val btnGoToRegister = findViewById<Button>(R.id.btnGoToRegister)
        val btnGoToList = findViewById<Button>(R.id.btnGoToList)
        val btnBackMenu = findViewById<Button>(R.id.btnBackMenu)

        // Abre la pantalla de Formulario de Registro
        btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Abre la pantalla de Listado de Usuarios
        btnGoToList.setOnClickListener {
            startActivity(Intent(this, UserListActivity::class.java))
        }

        // Regresa al Menú Principal
        btnBackMenu.setOnClickListener {
            finish()
        }
    }
}