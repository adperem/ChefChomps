package com.example.chefchomps.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import coil.compose.AsyncImage
import com.example.chefchomps.R
import com.example.chefchomps.logica.ApiCLient
import com.example.chefchomps.model.Recipe
import kotlinx.coroutines.runBlocking


@Composable
fun PaginaDetalle(modifier: Modifier = Modifier,
                  recipe: Recipe
){

    Scaffold(
        topBar = {
            Row(verticalAlignment= Alignment.CenterVertically){
                Image(painter = painterResource(R.drawable.chomper), contentDescription = "",modifier=modifier
                    .size(150.dp))
                Text(text="CHEF CHOMPS", maxLines = 1, textAlign = TextAlign.Center,modifier=modifier .fillMaxWidth(), fontSize = 7.em, fontWeight = FontWeight.Bold)
            }
        },
        modifier = modifier.padding(10.dp)

    ){
        innerPadding->Column(modifier=modifier.padding(innerPadding)){
            Text(recipe.title)
            Spacer(modifier = Modifier.size(10.dp))
            AsyncImage(model=recipe.image,
                placeholder = painterResource(R.drawable.nofoodplaceholder),
                contentDescription = "Imagen de la receta",
                onLoading = { println("Cargando imagen...") },
                onSuccess = { println("Imagen cargada con éxito") },
                onError = { println("Error al cargar la imagen: ${it.result.throwable}") },
                modifier = modifier.align(Alignment.CenterHorizontally)
                    .fillMaxSize()
            )
        }
    }
}