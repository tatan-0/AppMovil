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
    private lateinit var userRepository: UserRepository
    private var generatedCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)

        userRepository = UserRepository(this)

        val etEmail = findViewById<EditText>(R.id.etRecoverEmail)
        val btnRecover = findViewById<Button>(R.id.btnRecover)
        val tvTimer = findViewById<TextView>(R.id.tvTimer)

        val etCode1 = findViewById<EditText>(R.id.etCode1)
        val etCode2 = findViewById<EditText>(R.id.etCode2)
        val etCode3 = findViewById<EditText>(R.id.etCode3)
        val etCode4 = findViewById<EditText>(R.id.etCode4)
        val etCode5 = findViewById<EditText>(R.id.etCode5)

        val etNewPass = findViewById<EditText>(R.id.etNewPass)
        val etRepeatPass = findViewById<EditText>(R.id.etRepeatPass)
        val btnCreatePass = findViewById<Button>(R.id.btnCreatePass)

        btnRecover.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Correo Inválido")
                    .setContentText("Ingrese un correo válido para recuperar su cuenta.")
                    .show()
                return@setOnClickListener
            }

            val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.titleText = "Generando código..."
            pDialog.show()

            userRepository.requestRecoveryCode(email,
                onSuccess = { msg, code ->
                    pDialog.dismissWithAnimation()
                    generatedCode = code

                    if (code.length == 5) {
                        etCode1.setText(code[0].toString())
                        etCode2.setText(code[1].toString())
                        etCode3.setText(code[2].toString())
                        etCode4.setText(code[3].toString())
                        etCode5.setText(code[4].toString())
                    }

                    countDownTimer?.cancel()
                    countDownTimer = object : CountDownTimer(59000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            tvTimer.text = "${millisUntilFinished / 1000} Segundos"
                        }

                        override fun onFinish() {
                            tvTimer.text = "0 Segundos (Expirado)"
                            generatedCode = ""
                        }
                    }.start()

                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Código Generado")
                        .setContentText("$msg\n\nCódigo de prueba: $code")
                        .show()
                },
                onError = { error ->
                    pDialog.dismissWithAnimation()
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText(error)
                        .show()
                }
            )
        }

        btnCreatePass.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass1 = etNewPass.text.toString().trim()
            val pass2 = etRepeatPass.text.toString().trim()
            val inputCode = "${etCode1.text}${etCode2.text}${etCode3.text}${etCode4.text}${etCode5.text}"

            if (email.isEmpty() || pass1.isEmpty() || pass2.isEmpty() || inputCode.length < 5) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Campos Incompletos")
                    .setContentText("Complete el correo, código y contraseñas.")
                    .show()
                return@setOnClickListener
            }

            if (pass1 != pass2) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Sin coincidencia")
                    .setContentText("Las contraseñas no coinciden.")
                    .show()
                return@setOnClickListener
            }

            val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.titleText = "Actualizando clave..."
            pDialog.show()

            userRepository.resetPassword(email, pass1,
                onSuccess = { msg ->
                    pDialog.dismissWithAnimation()
                    countDownTimer?.cancel()
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("¡Éxito!")
                        .setContentText(msg)
                        .setConfirmClickListener { dialog ->
                            dialog.dismissWithAnimation()
                            finish()
                        }
                        .show()
                },
                onError = { error ->
                    pDialog.dismissWithAnimation()
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText(error)
                        .show()
                }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}