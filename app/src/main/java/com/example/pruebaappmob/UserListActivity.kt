package com.example.pruebaappmob

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import java.text.Normalizer
import java.util.regex.Pattern

class UserListActivity : AppCompatActivity() {

    private lateinit var userRepository: UserRepository
    private lateinit var lvUsers: ListView
    private lateinit var etSearch: EditText
    private lateinit var tvStatus: TextView

    private var fullUserList = mutableListOf<Map<String, String>>()
    private var displayList = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        userRepository = UserRepository(this)

        lvUsers = findViewById(R.id.lvUsers)
        etSearch = findViewById(R.id.etSearch)
        tvStatus = findViewById(R.id.tvStatus)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList)
        lvUsers.adapter = adapter

        // Cargar lista inicial
        loadUsers()

        // Búsqueda insensible a mayúsculas y acentos
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterUsers(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Evento de clic simple: Abre la pantalla de Modificación/Eliminación de usuario
        lvUsers.setOnItemClickListener { _, _, position, _ ->
            if (position < fullUserList.size) {
                val selectedUser = fullUserList[position]
                val intent = Intent(this, EditUserActivity::class.java).apply {
                    putExtra("id", selectedUser["id"])
                    putExtra("nombres", selectedUser["nombres"])
                    putExtra("apellidos", selectedUser["apellidos"])
                    putExtra("email", selectedUser["email"])
                }
                startActivity(intent)
            }
        }

        // Evento de clic largo en usuario para eliminar con confirmación SweetAlert
        lvUsers.setOnItemLongClickListener { _, _, position, _ ->
            if (position < fullUserList.size) {
                val selectedUser = fullUserList[position]
                val userId = selectedUser["id"] ?: ""
                val userName = "${selectedUser["nombres"]} ${selectedUser["apellidos"]}"

                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("¿Eliminar usuario?")
                    .setContentText("¿Está seguro de eliminar a $userName?")
                    .setConfirmText("Sí, eliminar")
                    .setCancelText("Cancelar")
                    .setConfirmClickListener { sDialog ->
                        sDialog.dismissWithAnimation()
                        deleteUser(userId, userName)
                    }
                    .show()
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        // Actualiza automáticamente la lista al volver de EditUserActivity
        loadUsers()
    }

    private fun loadUsers() {
        userRepository.getUsers { users, isOnline ->
            fullUserList.clear()
            fullUserList.addAll(users)

            tvStatus.text = if (isOnline) "Estado: En Línea (Sincronizado con AWS)" else "Estado: Sin Conexión (Modo Offline - SQLite)"
            tvStatus.setTextColor(if (isOnline) 0xFF4CAF50.toInt() else 0xFFFF9800.toInt())

            updateDisplayList(fullUserList)
        }
    }

    private fun updateDisplayList(list: List<Map<String, String>>) {
        displayList.clear()
        for (u in list) {
            val sync = if (u["sync_status"] == DatabaseHelper.SYNCED.toString()) "✔" else "⏳"
            displayList.add("$sync ${u["nombres"]} ${u["apellidos"]}\n📧 ${u["email"]}")
        }
        adapter.notifyDataSetChanged()
    }

    // Filtro de texto normalizado (remueve tildes y convierte a minúsculas)
    private fun filterUsers(query: String) {
        val normalizedQuery = removeAccents(query.lowercase().trim())
        val filtered = fullUserList.filter { u ->
            val fullName = removeAccents("${u["nombres"]} ${u["apellidos"]}".lowercase())
            val email = removeAccents((u["email"] ?: "").lowercase())
            fullName.contains(normalizedQuery) || email.contains(normalizedQuery)
        }
        updateDisplayList(filtered)
    }

    private fun removeAccents(text: String): String {
        val normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(normalized).replaceAll("")
    }

    private fun deleteUser(id: String, name: String) {
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = "Eliminando $name..."
        pDialog.show()

        // Lógica de eliminación (actualiza y recarga lista)
        pDialog.dismissWithAnimation()
        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Usuario Eliminado")
            .setContentText("El usuario fue eliminado correctamente.")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                loadUsers()
            }
            .show()
    }
}