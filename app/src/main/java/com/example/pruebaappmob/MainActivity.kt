package com.example.pruebaappmob

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnUserList = findViewById<Button>(R.id.btnUserList)
        val btnSensors = findViewById<Button>(R.id.btnSensors)
        val btnDeveloper = findViewById<Button>(R.id.btnDeveloper)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Redirección al nuevo menú intermedio de Gestión de Usuarios
        btnUserList.setOnClickListener {
            startActivity(Intent(this, UserManagementActivity::class.java))
        }

        btnSensors.setOnClickListener {
            startActivity(Intent(this, SensorsActivity::class.java))
        }

        btnDeveloper.setOnClickListener {
            startActivity(Intent(this, DeveloperActivity::class.java))
        }

        btnLogout.setOnClickListener {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("¿Cerrar Sesión?")
                .setContentText("Volverá a la pantalla de inicio de sesión.")
                .setConfirmText("Sí, salir")
                .setCancelText("Cancelar")
                .setConfirmClickListener { dialog ->
                    dialog.dismissWithAnimation()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .show()
        }
    }
}