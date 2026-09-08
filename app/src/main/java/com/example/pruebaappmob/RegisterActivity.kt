package com.example.pruebaappmob

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etNombres = findViewById<EditText>(R.id.etRegNombres)
        val etApellidos = findViewById<EditText>(R.id.etRegApellidos)
        val etEmail = findViewById<EditText>(R.id.etRegEmail)
        val etPass = findViewById<EditText>(R.id.etRegPass)
        val etRepeatPass = findViewById<EditText>(R.id.etRegRepeatPass)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {
            val nombres = etNombres.text.toString().trim()
            val apellidos = etApellidos.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val pass = etPass.text.toString().trim()
            val repeatPass = etRepeatPass.text.toString().trim()

            if (nombres.isEmpty() || apellidos.isEmpty() || email.isEmpty() || pass.isEmpty() || repeatPass.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Campos Obligatorios")
                    .setContentText("Por favor complete todos los datos requeridos.")
                    .show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Email Inválido")
                    .setContentText("Ingrese un correo electrónico válido.")
                    .show()
                return@setOnClickListener
            }

            if (pass != repeatPass) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("No coinciden")
                    .setContentText("Las contraseñas deben ser idénticas.")
                    .show()
                return@setOnClickListener
            }

            val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.#\\-_])[A-Za-z\\d@$!%*?&.#\\-_]{8,}$"
            if (!pass.matches(Regex(passwordRegex))) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Contraseña Débil")
                    .setContentText("Debe contener al menos 8 caracteres, 1 mayúscula, 1 minúscula, 1 número y 1 carácter especial.")
                    .show()
                return@setOnClickListener
            }

            val newUser = User(
                id = System.currentTimeMillis().toString(),
                nombres = nombres,
                apellidos = apellidos,
                email = email,
                password = pass
            )

            val success = UserRepository.addUser(newUser)

            if (success) {
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("¡Registro Exitoso!")
                    .setContentText("El usuario ha sido registrado. Redirigiendo al Login.")
                    .setConfirmClickListener { dialog ->
                        dialog.dismissWithAnimation()
                        finish() // Retorna a la pantalla anterior (Login)
                    }
                    .show()
            } else {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error de Registro")
                    .setContentText("El correo electrónico ya se encuentra registrado.")
                    .show()
            }
        }
    }
}