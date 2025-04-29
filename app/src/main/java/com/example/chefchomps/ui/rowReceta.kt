package com.example.chefchomps.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.chefchomps.R
import com.example.chefchomps.model.Recipe

@Composable
fun RowReceta(rec: Recipe,modifier: Modifier=Modifier){

    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, Color(0xCCCCCCCC), shape = RoundedCornerShape(5.dp))
    ){
        Column(
            modifier = modifier
                .fillMaxWidth()
        ){
            Text(text=rec.title,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.size(10.dp))
            AsyncImage(model=rec.image,
                placeholder = painterResource(R.drawable.nofoodplaceholder),
                contentDescription = "Imagen de la receta",
                onLoading = { println("Cargando imagen...") },
                onSuccess = { println("Imagen cargada con éxito") },
                onError = { println("Error al cargar la imagen: ${it.result.throwable}") },
                modifier = modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxSize()
            )

        }
    }
}