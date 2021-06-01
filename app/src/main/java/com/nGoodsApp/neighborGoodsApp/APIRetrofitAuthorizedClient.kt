package com.nGoodsApp.neighborGoodsApp

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIRetrofitAuthorizedClient {
    fun getClient(token:String):Retrofit {
        val client = OkHttpClient.Builder().addInterceptor(AuthorizedInterceptor(token)).build()
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}