package com.example.chefchomps.logica

import com.example.chefchomps.persistencia.DatabaseConnection
import java.sql.ResultSet
import java.sql.PreparedStatement
import java.sql.SQLException

class DatabaseHelper(private val databaseConnection: DatabaseConnection) {

    fun userExists(email: String, password: String): Boolean {
        val connection = databaseConnection.connect()
        if (connection == null) {
            return false
        }

        val query = "SELECT * FROM usuarios WHERE email = ? AND password = ?"
        var result = false
        try {
            val statement: PreparedStatement = connection.prepareStatement(query)
            statement.setString(1, email)
            statement.setString(2, password)
            val resultSet: ResultSet = statement.executeQuery()

            result = resultSet.next()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            databaseConnection.disconnect(connection)
        }

        return result
    }

    fun registerUser(email: String, password: String, nombre: String, apellidos: String): Boolean {
        val connection = databaseConnection.connect()
        if (connection == null) {
            return false
        }

        val query = "INSERT INTO usuarios (email, password, nombre, apellidos) VALUES (?, ?, ?, ?)"
        var result = false
        try {
            val statement: PreparedStatement = connection.prepareStatement(query)
            statement.setString(1, email)
            statement.setString(2, password)
            statement.setString(3, nombre)
            statement.setString(4, apellidos)

            val rowsAffected = statement.executeUpdate()
            result = rowsAffected > 0
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            databaseConnection.disconnect(connection)
        }

        return result
    }

    fun loginUser(email: String, password: String): Boolean {
        return userExists(email, password)
    }
}