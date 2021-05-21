package com.example.neighborGoodsApp

sealed class State<out T> {
    class Success<T>(val data:T): State<T>()
    class Failure<T>(val message:String) :State<T>()
    class Loading<T>():State<T>()
}