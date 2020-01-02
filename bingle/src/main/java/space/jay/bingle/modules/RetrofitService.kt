package space.jay.bingle.modules

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

interface RetrofitService {

    companion object {
        var BASE_URL = "https://we-original.firebaseapp.com"
        var isURLChanged = false

        val instance: RetrofitService by lazy {
            val client = OkHttpClient.Builder().addInterceptor { chain ->
                val request = if (isURLChanged) {
                    isURLChanged = false
                    chain.request().newBuilder().url(BASE_URL).build()
                } else {
                    chain.request()
                }
                chain.proceed(request)
            }.build()
            val gson = GsonBuilder().setLenient().create()
            val retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            retrofit.create(RetrofitService::class.java)
        }
    }

    @GET("/")
    fun getVersion(): Call<String>

    @GET("/menu")
    fun getMenu(): Call<String>
}