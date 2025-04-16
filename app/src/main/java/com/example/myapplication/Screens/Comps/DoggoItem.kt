package com.example.myapplication.Screens.Comps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.myapplication.Doggo

@Composable
fun DoggoItem(doggo: Doggo, onFavoriteClick: () -> Unit, onDeleteClick: () -> Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(start = 12.dp)
                .size(48.dp)
                .background(
                    brush = Brush.linearGradient(listOf(Color(0xFFa18cd1), Color(0xFFfbc2eb))),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (doggo.imageUrl != null) {
                AsyncImage(
                    model = doggo.imageUrl,
                    contentDescription = "Doggo image",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text("🐕", fontSize = 20.sp)
            }
        }
        Column(modifier = Modifier
            .weight(1f)
            .padding(start = 8.dp)) {
            Text(text = doggo.name, fontWeight = FontWeight.Bold)
            Text(text = doggo.breed, style = MaterialTheme.typography.bodyMedium)
        }
        IconButton(onClick = onFavoriteClick) {
            Icon(Icons.Default.Favorite, tint = if (doggo.isFav) Color(0xFF9C27B0) else Color.Gray, contentDescription = "Favorite")
        }
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Default.Delete, tint = Color.Red, contentDescription = "Delete")
        }
    }
}