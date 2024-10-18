package com.verkada.android.catpictures

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.verkada.android.catpictures.data.Picture
import com.verkada.android.catpictures.network.PictureService
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CatPicturesApp(pictureService: PictureService) {
    //    two state variables declared, currentScreen which is a String that tracks whether either
    //    Home or Favorites screen is selected and favoritedPictures which is a set that holds the
    //    pictures that have been favorited
    var currentScreen by remember {
        mutableStateOf("Home")
    }
    val favoritedPictures = remember {
        mutableStateOf<Set<Picture>>(emptySet())
    }

    //establishing nav bar UI and control logic
    Scaffold(
        bottomBar = {
            BottomNavigation (
                backgroundColor = Color(0xFFD7EFFE)
            ){
                BottomNavigationItem(
                    icon = {
                        Icon(Icons.Default.Home, contentDescription = null)
                           },
                    label = {
                        Text("Home")
                            },
                    selected = currentScreen == "Home",
                    onClick = {
                        currentScreen = "Home"
                    }
                )
                BottomNavigationItem(
                    icon = {
                        Icon(Icons.Default.Favorite, contentDescription = null)
                           },
                    label = {
                        Text("Favorites")
                            },
                    selected = currentScreen== "Favorites",
                    onClick = {
                        currentScreen = "Favorites"
                    }
                )
            }
        }
    ) {
        when (currentScreen) {
            //calling same composable, but designating whether Favorites through boolean argument
            "Home" -> FlexiblePictureScreen(pictureService, favoritedPictures, isFavoritesScreen = false)
            "Favorites" -> FlexiblePictureScreen(pictureService, favoritedPictures, isFavoritesScreen = true)
        }
    }
}

//adaptive, flexible composable enabling Home and Favorites Screens UI and control logic
@Composable
fun FlexiblePictureScreen(pictureService: PictureService,
                  favoritedPictures:MutableState<Set<Picture>>,
                  isFavoritesScreen: Boolean) {
    val scope = rememberCoroutineScope()
    val pictures = remember { mutableStateOf<List<Picture>>(emptyList()) }
    var currentPicture by remember { mutableStateOf<Picture?>(null) }

    LaunchedEffect(Unit) {
        if (isFavoritesScreen) {
            pictures.value = favoritedPictures.value.toList()
        } else {
            scope.launch {
                val retrievedPictures = pictureService.pictures(page = 1, perPage = 100)
                pictures.value = retrievedPictures
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        currentPicture?.let { picture ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(325.dp)
                        .background(Color(0xFFEFF7CF))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberImagePainter(picture.url),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(200.dp)
                            .background(Color.LightGray)
                    )
                }
                Icon(
                    imageVector = if (favoritedPictures.value.contains(picture)) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = "Favorite",
                    modifier = Modifier
                        .size(75.dp)
                        .padding(12.dp)
                        .clickable {
                            if (favoritedPictures.value.contains(picture)) {
                                favoritedPictures.value -= picture
                                if (isFavoritesScreen) {
                                    pictures.value = favoritedPictures.value.toList()
                                }
                                if (isFavoritesScreen and favoritedPictures.value.isNotEmpty()) {
                                    currentPicture = favoritedPictures.value.first()
                                } else{
                                    if (isFavoritesScreen) {
                                        currentPicture = null
                                    }
                                }
                            } else {
                                favoritedPictures.value += picture
                            }
                        },
                    tint = Color.Black
                )
            }
        }
    PictureGrid(pictures.value) { picture ->
        currentPicture = if (currentPicture == picture) {
            null
        } else {
            picture
        }
    }
    }
}


//composable establishes UI and clicking logic of basic grid layout of images
@Composable
fun PictureGrid(pictures: List<Picture>, onPictureClick: (Picture) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(2.dp)
    ) {
        items(pictures) { picture ->
            Image(
                painter = rememberImagePainter(picture.url),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(1.dp)
                    .size(140.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp))
                    .clickable { onPictureClick(picture) }
            )
        }
    }
}
