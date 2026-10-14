package com.example.pruebaappmob

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class UserRepository(private val context: Context) {

    private val dbHelper = DatabaseHelper(context)
    private val queue = Volley.newRequestQueue(context)

    companion object {
        const val BASE_URL = "http://18.234.44.123/"
    }

    // 1. INICIO DE SESIÓN
    fun login(
        email: String,
        pass: String,
        onSuccess: (JSONObject) -> Unit,
        onError: (String) -> Unit
    ) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            val url = "${BASE_URL}login.php"
            val request = object : StringRequest(
                Request.Method.POST, url,
                { response ->
                    try {
                        val json = JSONObject(response)
                        if (json.getString("status") == "success") {
                            val u = json.getJSONObject("user")
                            dbHelper.saveUser(
                                u.getString("nombres"),
                                u.getString("apellidos"),
                                u.getString("email"),
                                pass,
                                1,
                                DatabaseHelper.SYNCED
                            )
                            onSuccess(json)
                        } else {
                            onError(json.getString("message"))
                        }
                    } catch (e: Exception) {
                        onError("Error al procesar respuesta: ${e.message}")
                    }
                },
                { error -> onError("Error de red: ${error.message ?: "Sin conexión al servidor"}") }
            ) {
                override fun getParams(): Map<String, String> {
                    return mapOf("email" to email, "password" to pass)
                }
            }
            queue.add(request)
        } else {
            val users = dbHelper.getAllUsers()
            val user = users.find { it["email"] == email }
            if (user != null) {
                if (user["estado"] == "0") {
                    onError("Cuenta bloqueada por proceso de recuperación.")
                } else {
                    val res = JSONObject().apply {
                        put("status", "success")
                        put("message", "Inicio de sesión local (Modo Offline)")
                    }
                    onSuccess(res)
                }
            } else {
                onError("Modo Offline: Usuario no registrado localmente.")
            }
        }
    }

    // 2. REGISTRO DE USUARIOS
    fun register(
        nombres: String,
        apellidos: String,
        email: String,
        pass: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            val url = "${BASE_URL}register.php"
            val request = object : StringRequest(
                Request.Method.POST, url,
                { response ->
                    try {
                        val json = JSONObject(response)
                        if (json.getString("status") == "success") {
                            dbHelper.saveUser(nombres, apellidos, email, pass, 1, DatabaseHelper.SYNCED)
                            onSuccess(json.getString("message"))
                        } else {
                            onError(json.getString("message"))
                        }
                    } catch (e: Exception) {
                        onError("Error de formato JSON: ${e.message}")
                    }
                },
                { error -> onError("Error de comunicación: ${error.message}") }
            ) {
                override fun getParams(): Map<String, String> {
                    return mapOf(
                        "nombres" to nombres,
                        "apellidos" to apellidos,
                        "email" to email,
                        "password" to pass
                    )
                }
            }
            queue.add(request)
        } else {
            dbHelper.saveUser(nombres, apellidos, email, pass, 1, DatabaseHelper.PENDING_INSERT)
            onSuccess("Usuario guardado localmente (Pendiente de sincronizar).")
        }
    }

    // 3. SOLICITUD DE CÓDIGO DE RECUPERACIÓN
    fun requestRecoveryCode(
        email: String,
        onSuccess: (String, String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            val url = "${BASE_URL}recover.php"
            val request = object : StringRequest(
                Request.Method.POST, url,
                { response ->
                    try {
                        val json = JSONObject(response)
                        if (json.getString("status") == "success") {
                            val code = json.optString("codigo", "12345")
                            onSuccess(json.getString("message"), code)
                        } else {
                            onError(json.getString("message"))
                        }
                    } catch (e: Exception) {
                        onError("Error en la respuesta: ${e.message}")
                    }
                },
                { error -> onError("Error de conexión: ${error.message}") }
            ) {
                override fun getParams(): Map<String, String> = mapOf("email" to email)
            }
            queue.add(request)
        } else {
            onError("La recuperación requiere conexión a Internet.")
        }
    }

    // 4. RESTABLECER CONTRASEÑA
    fun resetPassword(
        email: String,
        newPass: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            val url = "${BASE_URL}reset_password.php"
            val request = object : StringRequest(
                Request.Method.POST, url,
                { response ->
                    try {
                        val json = JSONObject(response)
                        if (json.getString("status") == "success") {
                            onSuccess(json.getString("message"))
                        } else {
                            onError(json.getString("message"))
                        }
                    } catch (e: Exception) {
                        onError("Error en respuesta: ${e.message}")
                    }
                },
                { error -> onError("Error de red: ${error.message}") }
            ) {
                override fun getParams(): Map<String, String> = mapOf("email" to email, "password" to newPass)
            }
            queue.add(request)
        } else {
            onError("Se requiere conexión a Internet para restablecer la clave.")
        }
    }

    // 5. OBTENER USUARIOS
    fun getUsers(
        onResult: (List<Map<String, String>>, Boolean) -> Unit
    ) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            syncPendingData {
                val url = "${BASE_URL}users.php?action=list"
                val request = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        try {
                            val json = JSONObject(response)
                            if (json.getString("status") == "success") {
                                val array = json.getJSONArray("users")
                                val list = mutableListOf<Map<String, String>>()
                                for (i in 0 until array.length()) {
                                    val item = array.getJSONObject(i)
                                    val uMap = mapOf(
                                        "id" to item.getString("id"),
                                        "nombres" to item.getString("nombres"),
                                        "apellidos" to item.getString("apellidos"),
                                        "email" to item.getString("email"),
                                        "estado" to item.getString("estado"),
                                        "sync_status" to DatabaseHelper.SYNCED.toString()
                                    )
                                    list.add(uMap)
                                    dbHelper.saveUser(
                                        item.getString("nombres"),
                                        item.getString("apellidos"),
                                        item.getString("email"),
                                        "Clave123!",
                                        item.optInt("estado", 1),
                                        DatabaseHelper.SYNCED
                                    )
                                }
                                onResult(list, true)
                            } else {
                                onResult(dbHelper.getAllUsers(), false)
                            }
                        } catch (e: Exception) {
                            onResult(dbHelper.getAllUsers(), false)
                        }
                    },
                    { onResult(dbHelper.getAllUsers(), false) }
                )
                queue.add(request)
            }
        } else {
            onResult(dbHelper.getAllUsers(), false)
        }
    }

    // 6. SINCRONIZACIÓN AUTOMÁTICA
    fun syncPendingData(onComplete: () -> Unit) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            onComplete()
            return
        }

        val pending = dbHelper.getPendingSyncUsers()
        if (pending.isEmpty()) {
            onComplete()
            return
        }

        val jsonArray = JSONArray()
        for (user in pending) {
            val obj = JSONObject()
            obj.put("local_id", user["local_id"])
            obj.put("nombres", user["nombres"])
            obj.put("apellidos", user["apellidos"])
            obj.put("email", user["email"])
            obj.put("password", user["password"])
            obj.put("sync_type", user["sync_type"])
            jsonArray.put(obj)
        }

        val url = "${BASE_URL}sync.php"
        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                try {
                    val jsonRes = JSONObject(response)
                    if (jsonRes.getString("status") == "success") {
                        val syncedIds = jsonRes.getJSONArray("synced_ids")
                        for (i in 0 until syncedIds.length()) {
                            val id = syncedIds.getInt(i)
                            dbHelper.markAsSynced(id)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                onComplete()
            },
            { onComplete() }
        ) {
            override fun getBodyContentType(): String = "application/json; charset=utf-8"
            override fun getBody(): ByteArray = jsonArray.toString().toByteArray(Charsets.UTF_8)
        }

        queue.add(request)
    }
}