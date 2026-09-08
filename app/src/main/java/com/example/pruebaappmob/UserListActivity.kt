package com.example.pruebaappmob

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import java.text.Normalizer

class UserListActivity : AppCompatActivity() {

    private lateinit var lvUsers: ListView
    private lateinit var etSearch: EditText
    private lateinit var adapter: ArrayAdapter<String>
    private var displayedUsers = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        lvUsers = findViewById(R.id.lvUsers)
        etSearch = findViewById(R.id.etSearchUser)

        refreshList()


        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterUsers(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        lvUsers.setOnItemClickListener { _, _, position, _ ->
            val selectedUser = displayedUsers[position]
            showEditDeleteDialog(selectedUser)
        }
    }

    private fun stripAccents(text: String): String {
        val normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
        return normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }

    private fun filterUsers(query: String) {
        val cleanQuery = stripAccents(query).lowercase()
        displayedUsers = UserRepository.usersList.filter {
            val fullName = stripAccents("${it.nombres} ${it.apellidos}").lowercase()
            fullName.contains(cleanQuery)
        }.toMutableList()

        updateListView()
    }

    private fun refreshList() {
        displayedUsers = UserRepository.usersList.toMutableList()
        updateListView()
    }

    private fun updateListView() {
        val userNames = displayedUsers.mapIndexed { index, user ->
            "${index + 1}. ${user.nombres} ${user.apellidos}"
        }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userNames)
        lvUsers.adapter = adapter
    }

    private fun showEditDeleteDialog(user: User) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_user, null)
        val etNombres = dialogView.findViewById<EditText>(R.id.etEditNombres)
        val etApellidos = dialogView.findViewById<EditText>(R.id.etEditApellidos)
        val etEmail = dialogView.findViewById<EditText>(R.id.etEditEmail)
        val btnModificar = dialogView.findViewById<Button>(R.id.btnDialogModificar)
        val btnEliminar = dialogView.findViewById<Button>(R.id.btnDialogEliminar)

        etNombres.setText(user.nombres)
        etApellidos.setText(user.apellidos)
        etEmail.setText(user.email)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnModificar.setOnClickListener {
            val nombres = etNombres.text.toString().trim()
            val apellidos = etApellidos.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if (nombres.isEmpty() || apellidos.isEmpty() || email.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Ningún campo puede estar vacío.")
                    .show()
                return@setOnClickListener
            }

            val lettersOnlyRegex = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")
            if (!nombres.matches(lettersOnlyRegex) || !apellidos.matches(lettersOnlyRegex)) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Formato Inválido")
                    .setContentText("Nombres y Apellidos deben contener solo letras y espacios.")
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

            user.nombres = nombres
            user.apellidos = apellidos
            user.email = email
            alertDialog.dismiss()
            refreshList()

            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("¡Éxito!")
                .setContentText("Usuario modificado correctamente.")
                .show()
        }

        btnEliminar.setOnClickListener {
            alertDialog.dismiss()
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("¿Eliminar Usuario?")
                .setContentText("¿Está seguro de eliminar a ${user.nombres}?")
                .setConfirmText("Sí, eliminar")
                .setConfirmClickListener { dialog ->
                    UserRepository.usersList.remove(user)
                    refreshList()
                    dialog.setTitleText("¡Eliminado!")
                        .setContentText("El usuario fue eliminado con éxito.")
                        .setConfirmClickListener(null)
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                }
                .show()
        }

        alertDialog.show()
    }
}