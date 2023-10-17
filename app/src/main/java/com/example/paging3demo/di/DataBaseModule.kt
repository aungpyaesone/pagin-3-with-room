package com.example.paging3demo.di

import android.content.Context
import androidx.room.Room
import com.example.paging3demo.data.local.UnsplashDatabase
import com.example.paging3demo.data.remote.UnsplashApi
import com.example.paging3demo.util.Constants.BASE_URL
import com.example.paging3demo.util.Constants.UNSPLASH_DATABASE
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@OptIn(ExperimentalSerializationApi::class)
@Module
@InstallIn (SingletonComponent::class)
object DataBaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): UnsplashDatabase{
        return Room.databaseBuilder(
            context,
            UnsplashDatabase::class.java,
            UNSPLASH_DATABASE
        ).build()
    }
}