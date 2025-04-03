package com.example.chefchomps

import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * Clase personalizada de aplicación que inicializa Firebase al iniciar la aplicación.
 */
class MyApplication : Application() {
    /**
     * Metodo de ciclo de vida que se ejecuta cuando se crea la aplicación.
     * Inicializa Firebase en la aplicación.
     */
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
