package com.nGoodsApp.neighborGoodsApp

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

class AuthorizedInterceptor(private val token:String):Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val interceptor = HttpLoggingInterceptor()
        val request = chain.request().newBuilder()
            .addHeader("Authorization", token).build()
        return chain.proceed(request)
    }
}