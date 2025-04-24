package com.example.chefchomps.ui

import ChefChompsTema
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import coil.compose.AsyncImage
import com.example.chefchomps.R
import com.example.chefchomps.logica.ApiCLient
import com.example.chefchomps.logica.ApiCLient.Companion.autocompleteRecipes
import com.example.chefchomps.logica.ApiCLient.Companion.getRandomRecipe
import com.example.chefchomps.model.Recipe
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
                uiState:ViewModelPaginaPrincipal= ViewModelPaginaPrincipal()
    ){
        uiState.updatelist(runBlocking { getRandomRecipe() } )
        var text by remember { mutableStateOf("") }
        val focusManager = LocalFocusManager.current
        val textFieldFocusRequester = remember { FocusRequester() }
        val state = rememberScrollState()
        Scaffold(
            topBar = {
                Column{
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
                Row(modifier = Modifier.fillMaxWidth()
                    .verticalScroll(state)){
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Buscar") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            uiState.clear()
                            uiState.updatelist(runBlocking {autocompleteRecipes(text)})
                            focusManager.clearFocus()
                            text=""
                        }),
                    modifier = Modifier.focusRequester(textFieldFocusRequester)
                )
                IconButton(onClick = {
                    uiState.clear()
                    uiState.updatelist(runBlocking {autocompleteRecipes(text)})
                    focusManager.clearFocus()
                    text=""
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.lupa),
                        contentDescription = "back icon",
                    )
                }}
                }
            },
            modifier = modifier.padding(10.dp)

        ){
            innerPadding->
            LazyColumn(modifier=modifier
                .padding(innerPadding)
                .fillMaxWidth()) {
                items(
                    items=uiState.getlist(),
                    key={
                        item->item.title
                    }
                ){
                    aux->RowReceta(aux)
                }
            }



        }

    }

}