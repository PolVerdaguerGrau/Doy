package com.napptilians.networkdatasource.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.napptilians.commons.Constants.STRING_TO_BE_PROVIDED
import com.napptilians.commons.Constants.RETROFIT_TIMEOUT
import com.napptilians.networkdatasource.api.data.MovieService
import com.napptilians.networkdatasource.interceptors.UrlParamInterceptor
import com.napptilians.networkdatasource.providers.NetworkProvider
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofitClient(
        okHttpBuilder: OkHttpClient.Builder,
        retrofitBuilder: Retrofit.Builder
    ): Retrofit =
        retrofitBuilder.client(okHttpBuilder.build()).build()

    @Provides
    @Singleton
    fun provideRetrofitBuilder(
        @BaseUrl url: String,
        gsonConverterFactory:
        GsonConverterFactory
    ): Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(gsonConverterFactory)

    @Provides
    @Singleton
    fun provideHttpBuilder(
        networkProvider: NetworkProvider
    ): OkHttpClient.Builder =
        OkHttpClient.Builder().apply {
            addInterceptor(UrlParamInterceptor(networkProvider))
            addInterceptor(HttpLoggingInterceptor().apply {
                // TODO: Add isDebug variable to switch between Level.BODY and Level.NONE
                level = HttpLoggingInterceptor.Level.BODY
            })
            readTimeout(RETROFIT_TIMEOUT, TimeUnit.SECONDS)
            connectTimeout(RETROFIT_TIMEOUT, TimeUnit.SECONDS)
        }

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideGsonConverter(gson: Gson): GsonConverterFactory = GsonConverterFactory.create(gson)

    @Provides
    @Singleton
    fun provideNetworkProvider(
        @Named(STRING_TO_BE_PROVIDED) apiKey: String
    ): NetworkProvider = object : NetworkProvider {
        override val valueToBeProvided: String
            get() = apiKey
    }
}

@Module
object NetworkServicesModule {

    @Provides
    @Singleton
    fun provideMovieService(retrofit: Retrofit) = retrofit.create(MovieService::class.java)
}