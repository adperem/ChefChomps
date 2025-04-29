package com.example.chefchomps.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.chefchomps.R
import com.example.chefchomps.model.Recipe
import com.example.chefchomps.logica.ApiCLient
import com.example.chefchomps.logica.ApiCLient.Companion.getRandomRecipe

class VerReceta : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var receta by remember { mutableStateOf<Recipe?>(null) }

            LaunchedEffect(Unit) {
                val result = getRandomRecipe()
                receta = result.getOrNull(0)

/*                val result = getRandomRecipe()
                if (result.isSuccess) {
                    val recetas = result.getOrNull()
                    if (!recetas.isNullOrEmpty()) {
                        receta = recetas.first()
                    }
                } else {
                    Log.e("VerReceta", "Error obteniendo receta: ${result.exceptionOrNull()}")
                }*/
            }

            if (receta != null) {
                PaginaDetalle(Modifier, receta!!)
            } else {
                Text("Cargando receta...",textAlign = TextAlign.Center,modifier=Modifier.fillMaxSize().padding(vertical = 100.dp))
            }
        }
    }

}