package com.nGoodsApp.neighborGoodsApp

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIRetrofitAuthorizationClient {

    fun getClient(): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

}