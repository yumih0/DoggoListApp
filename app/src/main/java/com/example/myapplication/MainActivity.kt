package com.example.myapplication

import AddDogScreen
import DoggoViewModel
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.myapplication.Model.SearchViewModel
import com.example.myapplication.Screens.Comps.DoggoItem
import com.example.myapplication.Screens.DoggoDetailsScreen
import com.example.myapplication.Screens.SettingsScreen
import com.example.myapplication.Screens.UserProfileScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.http.Url

data class Doggo(
    val name: String,
    val breed: String,
    val imageUrl: String? = null,
    var isFav: Boolean = false)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val searchViewModel: SearchViewModel = viewModel()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        MainScreen(navController, searchViewModel)
                    }
                    composable("details/{name}") { backStackEntry ->
                        val name = backStackEntry.arguments?.getString("name") ?: ""
                        val doggo = searchViewModel.getDoggoByName(name)
                        doggo?.let {
                            DoggoDetailsScreen(
                                doggo = it,
                                onDelete = {
                                    searchViewModel.deleteDoggo(it)
                                    navController.popBackStack()
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                    composable("settings") {
                        SettingsScreen(onBack = { navController.popBackStack() })
                    }
                    composable("user-profile") {
                        UserProfileScreen(onBack = { navController.popBackStack() })
                    }
                    composable("addDog/{name}") { backStackEntry ->
                        val name = backStackEntry.arguments?.getString("name") ?: ""
                        AddDogScreen(
                            defaultName = name,
                            onBack = { navController.popBackStack() },
                            onAdd = { doggo ->
                                val added = searchViewModel.addDoggo(doggo)
                                if (added) {
                                    navController.popBackStack()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, viewModel: SearchViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Doggos", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.Black,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("user-profile") }) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "ProfileButton",
                            tint = Color.Black,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFFEF7FF))
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            SearchLayout(navController, viewModel)
        }
    }
}


@Composable
fun SearchLayout(navController: NavController, viewModel: SearchViewModel) {
    var name by remember { mutableStateOf("") }
    var isDuplicate by remember { mutableStateOf(false) }

    val doggos = viewModel.displayedDoggos

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                viewModel.onSearchQueryChange(it)
                isDuplicate = false
            },
            placeholder = { Text("Poszukaj lub dodaj pieska 🐕") },
            isError = isDuplicate,
            modifier = Modifier
                .padding(20.dp)
                .width(300.dp)
        )

        IconButton(
            onClick = {
                if (viewModel.doggos.any { it.name.equals(name, ignoreCase = true) }) {
                    isDuplicate = true
                } else {
                    isDuplicate = false
                    navController.navigate("addDog/${name}")
                }
            },
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add",
                tint = Color(0xFF9C27B0),
                modifier = Modifier.size(33.dp)
            )
        }
    }

    if (isDuplicate) {
        Text("Ten piesek jest już na liście", color = Color.Red, modifier = Modifier.padding(start = 20.dp))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp)
    ) {
        Text("🐶: ${viewModel.doggos.size} ")
        Text("💜: ${viewModel.doggos.count { it.isFav }}")
    }

    Spacer(Modifier.height(20.dp))

    LazyColumn {
        items(doggos.sortedByDescending { it.isFav }) { doggo ->
            DoggoItem(
                doggo = doggo,
                onFavoriteClick = { viewModel.toggleFavorite(doggo) },
                onDeleteClick = { viewModel.deleteDoggo(doggo) },
                onClick = {
                    navController.navigate("details/${doggo.name}")
                }
            )
        }
    }
}
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            MyApplicationTheme {
//                val navController = rememberNavController()
//                val doggosState = remember { mutableStateOf(listOf<Doggo>()) }
//
//                NavHost(navController = navController, startDestination = "home") {
//                    composable("home") {
//                        MainScreen(navController, doggosState)
//                    }
//                    composable("details/{name}") { backStackEntry ->
//                        val name = backStackEntry.arguments?.getString("name") ?: ""
//                        val doggo = doggosState.value.find { it.name == name }
//                        doggo?.let {
//                            DoggoDetailsScreen(
//                                doggo = it,
//                                onDelete = {
//                                    doggosState.value = doggosState.value.filterNot { d -> d.name == it.name }
//                                    navController.popBackStack()
//                                },
//                                onBack = { navController.popBackStack() }
//                            )
//                        }
//                    }
//                    composable("settings") {
//                        SettingsScreen(onBack = {navController.popBackStack()})
//                    }
//
//                    composable("user-profile"){
//                        UserProfileScreen(onBack = {navController.popBackStack()})
//                    }
//                    composable("addDog/{name}") { backStackEntry ->
//                        val name = backStackEntry.arguments?.getString("name") ?: ""
//                        AddDogScreen(
//                            defaultName = name,
//                            onBack = { navController.popBackStack() },
//                            onAdd = { doggo ->
//                                doggosState.value = doggosState.value + doggo
//                                navController.popBackStack()
//                            }
//                        )
//                    }
//                }
//            }
//        }
//    }
//}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MainScreen(navController: NavController, doggosState: MutableState<List<Doggo>>) {
//    Scaffold(
//        modifier = Modifier.fillMaxSize(),
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = { Text(text = "Doggos", color = Color.Black) },
//
//                //settings button
//                navigationIcon = {
//                    IconButton(onClick = {navController.navigate("settings")}) {
//                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.Black, modifier = Modifier.size(42.dp))
//                    }
//                },
//
//                //user profile button
//                actions = {
//                    IconButton(onClick = {navController.navigate("user-profile")}) {
//                        Icon(Icons.Default.AccountCircle, contentDescription = "ProfileButton", tint = Color.Black, modifier = Modifier.size(42.dp))
//                    }
//                },
//                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFFEF7FF))
//            )
//        }
//    ) { innerPadding ->
//        Column(modifier = Modifier.padding(innerPadding)) {
//            SearchLayout(navController, doggosState)
//        }
//    }
//}

//@Composable
//fun SearchLayout(navController: NavController, viewModel: DoggoViewModel) {
//    var name by remember { mutableStateOf("") }
//    var isDuplicate by remember { mutableStateOf(false) }
//
//    val doggos = viewModel.displayedDoggos
//
//    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
//        OutlinedTextField(
//            value = name,
//            onValueChange = {
//                name = it
//                viewModel.onSearchQueryChange(it)
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
//            modifier = Modifier.padding(top = 20.dp)
//        ) {
//            Icon(
//                Icons.Default.Add,
//                contentDescription = "Add",
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
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(start = 20.dp)
//    ) {
//        Text("🐶: ${viewModel.doggos.size} ")
//        Text("💜: ${viewModel.doggos.count { it.isFav }}")
//    }
//
//    Spacer(Modifier.height(20.dp))
//
//    LazyColumn {
//        items(doggos.sortedByDescending { it.isFav }) { doggo ->
//            DoggoItem(
//                doggo = doggo,
//                onFavoriteClick = { viewModel.toggleFavorite(doggo) },
//                onDeleteClick = { viewModel.deleteDoggo(doggo) },
//                onClick = {
//                    navController.navigate("details/${doggo.name}")
//                }
//            )
//        }
//    }
//}




//!!!!! SEARCHLAYOUT PRZED VIEWMODELEM!!!!!
//@Composable
//fun SearchLayout(navController: NavController, doggosState: MutableState<List<Doggo>>) {
//    var name by remember { mutableStateOf("") }
//    var searchQuery by remember { mutableStateOf("") }
//    var isDuplicate by remember { mutableStateOf(false) }
//    var doggos by remember { doggosState }
//
//    val displayed =
//        if (searchQuery.isBlank()) doggos else doggos.filter { it.name.contains(searchQuery, true) }
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
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(start = 20.dp)
//    ) {
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


//@Composable
//fun DoggoItem(doggo: Doggo, onFavoriteClick: () -> Unit, onDeleteClick: () -> Unit, onClick: () -> Unit) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { onClick() }
//            .padding(8.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Box(
//            modifier = Modifier
//                .padding(start = 12.dp)
//                .size(48.dp)
//                .background(
//                    brush = Brush.linearGradient(listOf(Color(0xFFa18cd1), Color(0xFFfbc2eb))),
//                    shape = RoundedCornerShape(8.dp)
//                ),
//            contentAlignment = Alignment.Center
//        ) {
//            if (doggo.imageUrl != null) {
//                AsyncImage(
//                    model = doggo.imageUrl,
//                    contentDescription = "Doggo image",
//                    modifier = Modifier
//                        .size(48.dp)
//                        .clip(RoundedCornerShape(8.dp)),
//                    contentScale = ContentScale.Crop
//                )
//            } else {
//                Text("🐕", fontSize = 20.sp)
//            }
//        }
//        Column(modifier = Modifier
//            .weight(1f)
//            .padding(start = 8.dp)) {
//            Text(text = doggo.name, fontWeight = FontWeight.Bold)
//            Text(text = doggo.breed, style = MaterialTheme.typography.bodyMedium)
//        }
//        IconButton(onClick = onFavoriteClick) {
//            Icon(Icons.Default.Favorite, tint = if (doggo.isFav) Color(0xFF9C27B0) else Color.Gray, contentDescription = "Favorite")
//        }
//        IconButton(onClick = onDeleteClick) {
//            Icon(Icons.Default.Delete, tint = Color.Red, contentDescription = "Delete")
//        }
//    }
//}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DoggoDetailsScreen(doggo: Doggo, onDelete: () -> Unit, onBack: () -> Unit) {
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = { Text("Detale") },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                actions = {
//                    IconButton(onClick = onDelete) {
//                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
//                    }
//                },
//                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFFEF7FF))
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .padding(top = 200.dp)
//                .fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Top
//        ) {
//            Box(
//                modifier = Modifier
//                    .size(180.dp)
//                    .clip(RoundedCornerShape(12.dp))
//                    .background(
//                        brush = Brush.linearGradient(listOf(Color(0xFFa18cd1), Color(0xFFfbc2eb))),
//                    ),
//                contentAlignment = Alignment.Center
//            ) {
//                if (doggo.imageUrl != null) {
//                    AsyncImage(
//                        model = doggo.imageUrl,
//                        contentDescription = "Doggo image",
//                        modifier = Modifier.fillMaxSize(),
//                        contentScale = ContentScale.Crop
//                    )
//                } else {
//                    Text("🐕", fontSize = 24.sp)
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(text = doggo.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
//            Text(text = doggo.breed, fontSize = 14.sp, color = Color.Gray)
//        }
//    }
//}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SettingsScreen(onBack: () -> Unit) {
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = { Text("Ustawienia") },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFFEF7FF))
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxSize()
//                .padding(16.dp),
//            verticalArrangement = Arrangement.Top,
//            horizontalAlignment = Alignment.Start
//        ) {
//            //Settings contetn
//        }
//    }
//}

////UserProfile
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun UserProfileScreen(onBack: () -> Unit) {
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = { Text("Profil") },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFFEF7FF))
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxSize()
//                .padding(start = 16.dp, end = 16.dp, top = 50.dp),
//            verticalArrangement = Arrangement.Top,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Box(modifier = Modifier
//                .size(100.dp)
//                .background(Color.Gray, shape = CircleShape))
//            Spacer(modifier = Modifier.height(10.dp))
//            Text(text = "Imię Nazwisko", fontWeight = FontWeight.Bold)
//        }
//    }
//}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AddDogScreen(
//    defaultName: String,
//    onBack: () -> Unit,
//    onAdd: (Doggo) -> Unit
//) {
//    var name by remember { mutableStateOf(defaultName) }
//    var breed by remember { mutableStateOf("") }
//
//    // ViewModel and STATES
//    val viewModel: DoggoViewModel = viewModel()
//    val imageUrl by viewModel.imageUrl.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//    val error by viewModel.error.collectAsState()
//
//    LaunchedEffect(Unit) {
//        viewModel.fetchRandomDogImage()
//    }
//
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = { Text("Dodaj Psa") },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFFEF7FF))
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxSize()
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Top
//        ) {
//            // Placeholder for doggo :(
//            Box(
//                modifier = Modifier
//                    .size(180.dp)
//                    .background(Color(0xFFE0E0E0)),
//                contentAlignment = Alignment.Center
//            ) {
//                when {
//                    isLoading -> CircularProgressIndicator()
//                    imageUrl != null -> AsyncImage(
//                        model = imageUrl,
//                        contentDescription = "Dog Image",
//                        modifier = Modifier.fillMaxSize(),
//                        contentScale = ContentScale.Crop
//                    )
//                    else -> Text("🐕", fontSize = 40.sp)
//                }
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // Doggo name
//            OutlinedTextField(
//                value = name,
//                onValueChange = { name = it },
//                label = { Text("Imię") },
//                modifier = Modifier.fillMaxWidth(),
//                singleLine = true
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // doggo breed
//            OutlinedTextField(
//                value = breed,
//                onValueChange = { breed = it },
//                label = { Text("Rasa") },
//                modifier = Modifier.fillMaxWidth(),
//                singleLine = true
//            )
//
//            // eroor
//            if (error != null) {
//                Text(error ?: "", color = Color.Red, modifier = Modifier.padding(top = 8.dp))
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // AddButton
//            Button(
//                onClick = {
//                    if (name.isNotBlank() && breed.isNotBlank()) {
//                        onAdd(Doggo(name = name, breed = breed, imageUrl = imageUrl))
//                    }
//                },
//                modifier = Modifier.fillMaxWidth(),
//                enabled = name.isNotBlank() && breed.isNotBlank(),
//                colors = ButtonDefaults.buttonColors(
//                    contentColor = Color.White,
//                    containerColor = Color(0xFF9C27B0)
//                )
//            ) {
//                Text("Dodaj pieska")
//            }
//        }
//    }
//}

//strzałka do odświeżania
//blokada entera
//view model wrzucić




