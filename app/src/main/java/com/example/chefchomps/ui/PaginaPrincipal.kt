package com.example.chefchomps.ui

import ChefChompsTema
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import coil.compose.AsyncImage
import com.example.chefchomps.R
import com.example.chefchomps.logica.ApiCLient
import com.example.chefchomps.logica.ApiCLient.Companion.getRandomRecipe
import kotlinx.coroutines.runBlocking

/**
 * Class que define la página principal de la app ChefChomps
 *
 */
class PaginaPrincipal :ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChefChompsTema(darkTheme = false){
                Surface (modifier = Modifier.fillMaxSize())
                {
                    PaginaPrincipal();
                }
            }}
    }
    /**
     * Pagina de inicio para la aplicacion
     * @param modifier modificador que define comportamiento
     * @param uiState contiene todos los datos relacionados con la página principal
     * */
    @SuppressLint("NotConstructor")
    @Preview
    @Composable
    fun PaginaPrincipal(modifier:Modifier=Modifier,
                uiState:ViewModelPaginaPrincipal=ViewModelPaginaPrincipal()
    ){
        uiState.updatelist(runBlocking { ApiCLient.getRandomRecipe() } )
        Scaffold(
            topBar = {
                Row(verticalAlignment=Alignment.CenterVertically){
                    Image(painter = painterResource(R.drawable.menuicon), contentDescription = "",
                        modifier=modifier
                        .size(30.dp)
                    )
                    Image(painter = painterResource(R.drawable.chomper), contentDescription = "",modifier=modifier
                        .size(100.dp))
                    Text(text="CHEF CHOMPS", maxLines = 1, textAlign = TextAlign.Center,modifier=modifier .fillMaxWidth(),
                        fontSize = 7.em, fontWeight = FontWeight.Bold)
                }
            },
            modifier = modifier.padding(10.dp)

        ){ innerPadding->
            LazyColumn(modifier=modifier
                .padding(innerPadding)
                .fillMaxWidth()) {
                items(uiState.getlist()){
                    aux->Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .border(2.dp, Color(0xCCCCCCCC), shape = RoundedCornerShape(5.dp))
                    ){
                        Column(

                            modifier = modifier
                                .fillMaxWidth()
                        ){
                            Text(text=aux.title,
                                textAlign = TextAlign.Center,
                                modifier = modifier
                                    .fillMaxWidth()
                                )
                            Spacer(modifier = Modifier.size(10.dp))
                            AsyncImage(model=aux.image,
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
            }



        }

    }

}