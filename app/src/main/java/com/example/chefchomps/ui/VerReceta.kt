package com.example.chefchomps.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.chefchomps.R
import com.example.chefchomps.model.Recipe

class VerReceta : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Recipe r = ApiCLient.getRandomRecipe()
        setContent {
            PaginaDetalle(r)
        }
    }
    /**
     * Pagina de visualización de una receta
     * */
    @Composable
    fun RecetaVer(/*rec: Recipe,*/ modifier: Modifier = Modifier.fillMaxWidth()){
        Column(modifier=modifier
            .fillMaxWidth()) {
            Row{
                Image(painter = painterResource(R.drawable.chomper), contentDescription = "",modifier=modifier)
                Text(text="CHEF CHOMPS",modifier=modifier)
            }


        }
    }

}