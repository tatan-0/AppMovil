package com.example.pruebaappmob

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "IoTAppLocal.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USERS = "usuarios"
        const val COLUMN_ID = "id"
        const val COLUMN_NOMBRES = "nombres"
        const val COLUMN_APELLIDOS = "apellidos"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_ESTADO = "estado"
        const val COLUMN_SYNC_STATUS = "sync_status"

        // Estados de sincronización
        const val SYNCED = 0
        const val PENDING_INSERT = 1
        const val PENDING_UPDATE = 2
        const val PENDING_DELETE = 3
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NOMBRES + " TEXT, "
                + COLUMN_APELLIDOS + " TEXT, "
                + COLUMN_EMAIL + " TEXT UNIQUE, "
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_ESTADO + " INTEGER DEFAULT 1, "
                + COLUMN_SYNC_STATUS + " INTEGER DEFAULT " + SYNCED + ")")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // Insertar o actualizar usuario local
    fun saveUser(nombres: String, apellidos: String, email: String, pass: String, estado: Int = 1, syncStatus: Int = SYNCED): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRES, nombres)
            put(COLUMN_APELLIDOS, apellidos)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, pass)
            put(COLUMN_ESTADO, estado)
            put(COLUMN_SYNC_STATUS, syncStatus)
        }
        return db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    // Obtener todos los usuarios activos
    fun getAllUsers(): List<Map<String, String>> {
        val userList = mutableListOf<Map<String, String>>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_SYNC_STATUS != $PENDING_DELETE ORDER BY $COLUMN_ID DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val user = mapOf(
                    "id" to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)).toString(),
                    "nombres" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRES)),
                    "apellidos" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APELLIDOS)),
                    "email" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    "estado" to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ESTADO)).toString(),
                    "sync_status" to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SYNC_STATUS)).toString()
                )
                userList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return userList
    }

    // Obtener registros pendientes de sincronizar
    fun getPendingSyncUsers(): List<Map<String, String>> {
        val pendingList = mutableListOf<Map<String, String>>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_SYNC_STATUS != $SYNCED", null)

        if (cursor.moveToFirst()) {
            do {
                val user = mapOf(
                    "local_id" to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)).toString(),
                    "nombres" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRES)),
                    "apellidos" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APELLIDOS)),
                    "email" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    "password" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                    "sync_status" to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SYNC_STATUS)).toString(),
                    "sync_type" to when (cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SYNC_STATUS))) {
                        PENDING_INSERT -> "INSERT"
                        PENDING_UPDATE -> "UPDATE"
                        PENDING_DELETE -> "DELETE"
                        else -> "NONE"
                    }
                )
                pendingList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return pendingList
    }

    // Marcar como sincronizado
    fun markAsSynced(localId: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SYNC_STATUS, SYNCED)
        }
        db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(localId.toString()))
    }

    // Eliminar completamente de SQLite si la eliminación se sincronizó con MySQL
    fun removeUserByEmail(email: String) {
        val db = this.writableDatabase
        db.delete(TABLE_USERS, "$COLUMN_EMAIL = ?", arrayOf(email))
    }
}