package com.example.pruebaappmob

object UserRepository {
    val usersList = mutableListOf<User>(
        User("1", "Sebastián", "Olave", "sebastian.olave@alumnos.cl", "Clave123!"),
        User("2", "Matías", "Cortés", "matias.cortes@alumnos.cl", "Clave456!")
    )

    fun addUser(user: User): Boolean {
        if (usersList.any { it.email.equals(user.email, ignoreCase = true) }) {
            return false
        }
        usersList.add(user)
        return true
    }

    fun findUserByEmail(email: String): User? {
        return usersList.find { it.email.equals(email, ignoreCase = true) }
    }
}