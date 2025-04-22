package com.example.chefchomps.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.VerticalAlignmentLine
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
fun PaginaDetalle(modifier: Modifier,recipe: Recipe
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
            Text(recipe.title, textAlign = TextAlign.Center, modifier=modifier.fillMaxWidth(),fontSize=5.em, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.size(10.dp))
            AsyncImage(model=recipe.image,
                placeholder = painterResource(R.drawable.nofoodplaceholder),
                contentDescription = "Imagen de la receta",
                onLoading = { println("Cargando imagen...") },
                onSuccess = { println("Imagen cargada con éxito") },
                onError = { println("Error al cargar la imagen: ${it.result.throwable}") },
                modifier = modifier.align(Alignment.CenterHorizontally).fillMaxWidth())
            Text("INGREDIENTS", textAlign = TextAlign.Center, modifier=modifier.fillMaxWidth(), fontSize =3.em )
            val state = rememberLazyStaggeredGridState()

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                state = state,
                content = {
                    items(recipe.extendedIngredients) {
                        aux -> Text("-"+aux.name)
                    }
                }
            )
            //LazyColumn(modifier=modifier.fillMaxWidth().fillMaxWidth()) {
            //    items(recipe.extendedIngredients) { aux -> Text("-"+aux.name) }
           // }
            Text("INSTRUCTIONS", textAlign = TextAlign.Center, modifier=modifier.fillMaxWidth(), fontSize=3.em )
            Text(recipe.instructions,Modifier.verticalScroll(rememberScrollState()))
        }
    }
}