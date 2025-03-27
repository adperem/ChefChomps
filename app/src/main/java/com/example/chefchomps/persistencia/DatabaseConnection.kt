package com.example.chefchomps.persistencia

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DatabaseConnection {
    private val url = "jdbc:mysql://localhost:3306/app_recetas"
    private val user = "root"
    private val password = "root"

    private var connection: Connection? = null

    fun connect(): Connection? {
        return try {
            DriverManager.getConnection(url, user, password)
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    fun disconnect(connection: Connection?) {
        try {
            connection?.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}