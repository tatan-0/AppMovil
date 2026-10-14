package com.example.pruebaappmob

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog

class EditUserActivity : AppCompatActivity() {

    private lateinit var userRepository: UserRepository
    private var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        userRepository = UserRepository(this)

        val etNombres = findViewById<EditText>(R.id.etEditNombres)
        val etApellidos = findViewById<EditText>(R.id.etEditApellidos)
        val etEmail = findViewById<EditText>(R.id.etEditEmail)
        val btnUpdate = findViewById<Button>(R.id.btnUpdateUser)
        val btnDelete = findViewById<Button>(R.id.btnDeleteUser)
        val btnCancel = findViewById<Button>(R.id.btnCancelEdit)

        // Cargar datos recibidos de la lista
        userId = intent.getStringExtra("id") ?: ""
        etNombres.setText(intent.getStringExtra("nombres") ?: "")
        etApellidos.setText(intent.getStringExtra("apellidos") ?: "")
        etEmail.setText(intent.getStringExtra("email") ?: "")

        // Acción Modificar
        btnUpdate.setOnClickListener {
            val nombres = etNombres.text.toString().trim()
            val apellidos = etApellidos.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if (nombres.isEmpty() || apellidos.isEmpty() || email.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Campos Incompletos")
                    .setContentText("Todos los campos son obligatorios.")
                    .show()
                return@setOnClickListener
            }

            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Usuario Modificado")
                .setContentText("Los cambios han sido guardados correctamente.")
                .setConfirmClickListener { dialog ->
                    dialog.dismissWithAnimation()
                    finish()
                }
                .show()
        }

        // Acción Eliminar con confirmación previa
        btnDelete.setOnClickListener {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("¿Eliminar Usuario?")
                .setContentText("¿Está seguro de eliminar este registro?")
                .setConfirmText("Sí, eliminar")
                .setCancelText("Cancelar")
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Eliminado")
                        .setContentText("El usuario ha sido eliminado.")
                        .setConfirmClickListener { dialog ->
                            dialog.dismissWithAnimation()
                            finish()
                        }
                        .show()
                }
                .show()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }
}