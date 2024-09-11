package com.uri.bolanope.services

import android.util.Log
import io.github.cdimascio.dotenv.dotenv
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }
    private val BASE_URL = dotenv["BASE_URL"]

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object ApiClient {
    val apiService: ApiService by lazy {
        RetrofitClient.retrofit.create(ApiService::class.java)
    }
}

inline fun <reified T> apiCall(
    call: Call<T>,
    crossinline callback: (T?) -> Unit
) {
    call.enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful) {
                val result = response.body()
                callback(result)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.d("Erro na requisição", "Código: ${response.code()}, Erro: $errorBody")
                callback(null)
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            Log.d("Falha na requisição", "Erro: ${t.message}")
            callback(null)
        }
    })
}