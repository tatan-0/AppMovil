package com.example.pruebaappmob

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog

class RecoverPasswordActivity : AppCompatActivity() {

    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)

        val etEmail = findViewById<EditText>(R.id.etRecoverEmail)
        val btnRecover = findViewById<Button>(R.id.btnRecover)
        val tvTimer = findViewById<TextView>(R.id.tvTimer)

        val etNewPass = findViewById<EditText>(R.id.etNewPass)
        val etRepeatPass = findViewById<EditText>(R.id.etRepeatPass)
        val btnCreatePass = findViewById<Button>(R.id.btnCreatePass)

        btnRecover.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Campo Obligatorio")
                    .setContentText("Ingrese un correo electrónico.")
                    .show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Email Inválido")
                    .setContentText("El formato del correo ingresado no es válido.")
                    .show()
                return@setOnClickListener
            }
            
            countDownTimer?.cancel()
            countDownTimer = object : CountDownTimer(59000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val seconds = millisUntilFinished / 1000
                    tvTimer.text = "$seconds Segundos"
                }

                override fun onFinish() {
                    tvTimer.text = "0 Segundos (Expirado)"
                }
            }.start()

            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Código Enviado")
                .setContentText("Se ha enviado el código a su correo. Puede proceder a ingresar la nueva clave.")
                .show()
        }

        btnCreatePass.setOnClickListener {
            val pass1 = etNewPass.text.toString().trim()
            val pass2 = etRepeatPass.text.toString().trim()

            if (pass1.isEmpty() || pass2.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Campos Obligatorios")
                    .setContentText("Complete ambos campos de contraseña.")
                    .show()
                return@setOnClickListener
            }

            if (pass1 != pass2) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("No coinciden")
                    .setContentText("Las contraseñas ingresadas no son iguales.")
                    .show()
                return@setOnClickListener
            }

            val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.#\\-_])[A-Za-z\\d@$!%*?&.#\\-_]{8,}$"
            if (!pass1.matches(Regex(passwordRegex))) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Contraseña Débil")
                    .setContentText("Mínimo 8 caracteres, 1 mayúscula, 1 minúscula, 1 número y 1 carácter especial.")
                    .show()
                return@setOnClickListener
            }

            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("¡Contraseña Actualizada!")
                .setContentText("Su clave ha sido modificada con éxito.")
                .setConfirmClickListener { dialog ->
                    dialog.dismissWithAnimation()
                    finish()
                }
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}