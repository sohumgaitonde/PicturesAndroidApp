package com.verkada.android.catpictures

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import com.verkada.android.catpictures.network.PictureService
import com.verkada.android.catpictures.theme.CatPicturesTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainComposeActivity : ComponentActivity() {
    private lateinit var pictureService: PictureService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrofit = Retrofit.Builder().baseUrl(PictureService.ROOT_URL).addConverterFactory(GsonConverterFactory.create()).build()
        val pictureService = retrofit.create(PictureService::class.java)

        setContent {
            CatPicturesTheme {
                CatPicturesApp(pictureService)
            }
        }
    }
}
