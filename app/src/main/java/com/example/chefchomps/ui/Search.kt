package com.example.chefchomps.ui

import ChefChompsTema
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.chefchomps.R
import com.example.chefchomps.logica.ApiCLient.Companion.findRecipesByIngredients
import com.example.chefchomps.model.Recipe
import kotlinx.coroutines.runBlocking

class Search :ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChefChompsTema(darkTheme = false){
                Surface (modifier = Modifier.fillMaxSize())
                {
                    Search();
                }
            }
        }
    }
    @SuppressLint("NotConstructor")
    @Composable
    fun Search(){
        var text by remember { mutableStateOf("") }
        val lista=mutableListOf<String>()
        val focusManager = LocalFocusManager.current
        val textFieldFocusRequester = remember { FocusRequester() }

        Column(modifier = Modifier.fillMaxWidth()) {
            Row{
                IconButton(onClick = {
                    lista.add(text)
                    text = ""
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "back icon",
                    )
                }
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Buscar") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            lista.add(text)
                            text = ""
                        }),
                    modifier = Modifier.focusRequester(textFieldFocusRequester)
                )
                IconButton(onClick = {
                    focusManager.clearFocus()
                    lista.add(text)
                    text = ""
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_add_24),
                        contentDescription = "back icon",
                    )
                }
                IconButton(onClick = {
                    focusManager.clearFocus()
                    //Poner aqui codigo para que vaya a la pagina principal
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.lupa),
                        contentDescription = "back icon",
                    )
                }
            }

            LazyColumn {
                items(lista) { listItem ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = listItem)
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = {
                                lista.remove(listItem)
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.minus),
                                contentDescription = "back icon",
                            )
                        }
                    }
                }

            }
        }

    }
    @Preview
    @Composable
    fun ExpandedSearchViewPreview() {
        Surface(
        ) {
            Search()
        }

    }
}