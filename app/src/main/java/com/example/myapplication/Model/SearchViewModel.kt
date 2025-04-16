package com.example.myapplication.Model

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.myapplication.Doggo
import com.example.myapplication.Screens.Comps.DoggoItem

class SearchViewModel : ViewModel() {

    private val _doggos = mutableStateListOf<Doggo>()
    val doggos: List<Doggo> get() = _doggos

    var searchQuery by mutableStateOf("")
        private set

    val displayedDoggos: List<Doggo>
        get() = if (searchQuery.isBlank()) doggos else doggos.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
    }

    fun addDoggo(doggo: Doggo): Boolean {
        return if (_doggos.any { it.name.equals(doggo.name, true) }) {
            false
        } else {
            _doggos.add(doggo)
            true
        }
    }

    fun deleteDoggo(doggo: Doggo) {
        _doggos.remove(doggo)
    }

    fun toggleFavorite(doggo: Doggo) {
        val index = _doggos.indexOf(doggo)
        if (index != -1) {
            _doggos[index] = _doggos[index].copy(isFav = !doggo.isFav)
        }
    }

    fun getDoggoByName(name: String): Doggo? {
        return _doggos.find { it.name == name }
    }
}




//@Composable
//fun SearchLayout(navController: NavController, doggosState: MutableState<List<Doggo>>) {
//    var name by remember { mutableStateOf("") }
//    var searchQuery by remember { mutableStateOf("") }
//    var isDuplicate by remember { mutableStateOf(false) }
//    var doggos by remember { doggosState }
//
//    val displayed = if (searchQuery.isBlank()) doggos else doggos.filter { it.name.contains(searchQuery, true) }
//
//    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
//
//        //Doggo search
//        OutlinedTextField(
//            value = name,
//            onValueChange = {
//                name = it
////                if (it.isBlank()) searchQuery = ""
//                searchQuery = it
//                isDuplicate = false
//            },
//            placeholder = { Text("Poszukaj lub dodaj pieska 🐕") },
//            isError = isDuplicate,
//            modifier = Modifier
//                .padding(20.dp)
//                .width(300.dp)
//        )
//
//        IconButton(
//            onClick = {
//                navController.navigate("addDog/${name}")
//            },
////            enabled = name.isNotBlank(),
//            modifier = Modifier.padding(top = 20.dp)
//        ) {
//            Icon(
//                Icons.Default.Add,
//                contentDescription = "Add",
////                tint = if (name.isNotBlank()) Color(0xFF9C27B0) else Color.Gray,
//                tint = Color(0xFF9C27B0),
//                modifier = Modifier.size(33.dp)
//            )
//        }
//    }
//
//    if (isDuplicate) {
//        Text("Ten piesek jest na liście", color = Color.Red)
//    }
//
//    Row(modifier = Modifier
//        .fillMaxWidth()
//        .padding(start = 20.dp)) {
//        Text("🐶: ${doggos.size} ")
//        Text("💜: ${doggos.count { it.isFav }}")
//    }
//
//    Spacer(Modifier.height(20.dp))
//
//    LazyColumn {
//        items(displayed.sortedByDescending { it.isFav }) { doggo ->
//            DoggoItem(
//                doggo = doggo,
//                onFavoriteClick = {
//                    doggos = doggos.map { if (it == doggo) it.copy(isFav = !it.isFav) else it }
//                },
//                onDeleteClick = {
//                    doggos = doggos - doggo
//                },
//                onClick = {
//                    navController.navigate("details/${doggo.name}")
//                }
//            )
//        }
//    }
//}