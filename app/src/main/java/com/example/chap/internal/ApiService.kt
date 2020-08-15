package com.example.chap.internal

import android.util.Log
import com.example.chap.model.*
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.squareup.moshi.Json
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.lang.Exception

class ApiService(private val sharedPref: SharedPref) {

    companion object {
        const val TAG = "ApiService"
        val AUTHTYPE_LOGIN = "login"
        val AUTHTYPE_REGISTER = "register"
        private var apiService: ApiService? = null

        fun getInstance(sharedPref: SharedPref): ApiService {
            return if (apiService != null)
                apiService!!
            else {
                apiService = ApiService(sharedPref)
                apiService!!
            }
        }
    }

    private val requestService: RequestService

    init {
        requestService = RequestService()
    }

    suspend fun login(
        username: String,
        pass: String,
        onError: OnError
    ): Boolean {

        try {
            val res = requestService.login(username, pass)

            if (res.isSuccessful) {
                val token = res.body()?.get("data")?.asJsonObject?.get("session_tokens")?.asString

                return if (token != null) {
                    sharedPref.saveToken(token)
                    true
                } else {
                    MainScope().launch {
                        onError.onError(
                            res.body()?.get("data")?.asJsonObject?.get(
                                "message"
                            )?.asString
                        )
                    }
                    false
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            MainScope().launch { onError.onError("خطا در سرور") }
            return false
        }

        return false
    }

    suspend fun register(
        user: User,
        onError: OnError
    ): Boolean {
        try {
            val index = user.first_last_name.indexOf(" ")
            var name = user.first_last_name
            var familyName = " "
            if (index > 0) {
                name = user.first_last_name.substring(0, index)
                familyName =
                    user.first_last_name.substring(index + 1, user.first_last_name.length).trim()
            }

            val res = requestService.register(
                user.username,
                name,
                familyName,
                user.email!!,
                user.number,
                user.password
            )
            if (res.isSuccessful) {
                return if (user.username != "") {
                    sharedPref.saveToken(user.username)
                    true
                } else {
                    MainScope().launch {
                        onError.onError(
                            if (res.body()?.get("data")?.asJsonObject?.get(
                                    "code"
                                )?.asString == "406"
                            )
                                "ایمیل تکراری است"
                            else
                                "شماره تلفن تکراری است"
                        )
                    }
                    false
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            MainScope().launch { onError.onError("خطا در سرور") }
            return false
        }

        return false
    }

    suspend fun getComments(onError: OnError): ArrayList<Comment>? {
        try {
            val res = requestService.getPosts()
            val arr = ArrayList<Comment>()
            if (res.isSuccessful) {
                res.body()?.forEach {
                    val id = it.get("ID").asString
                    val name = it.get("post_author").asString
                    val content = it.get("post_content").asString
                    val index = it.get("post_date_gmt").asString.indexOf(" ")
                    val date = it.get("post_date").asString.substring(0, index)
                    val comment = Comment(id, name, content, date)
                    arr.add(comment)
                }
                return arr
            } else {
                MainScope().launch {
                    onError.onError("خطا")
                }
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MainScope().launch { onError.onError(e.message) }
            return null
        }
    }

    suspend fun addComment(title: String, content: String, onError: OnError) {
        try {
            val res = requestService.addPost(sharedPref.getToken(), title, content)
            if (!res.isSuccessful)
                MainScope().launch { onError.onError("خطا") }
            return
        } catch (e: Exception) {
            e.printStackTrace()
            MainScope().launch {
                onError.onError(e.message)
            }
        }
    }

    suspend fun getProfile(onError: OnError): User? {
        return try {
            Log.i("shit", sharedPref.getToken())
            val res = requestService.getProfile(sharedPref.getToken())
            checkForAuthentication(res as Response<Any>)
            if (res.isSuccessful) {
                val email = res.body()?.get("data")?.asJsonObject?.get("user_email")?.asString
                val mobile = res.body()?.get("data")?.asJsonObject?.get("mobile_user")?.asString
                val username = res.body()?.get("data")?.asJsonObject?.get("user_login")?.asString
                User(username!!, " ", email, mobile!!, " ")
            } else {
                MainScope().launch {
                    onError.onError("خطا")
                }
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MainScope().launch { onError.onError(e.message) }
            null
        }
    }

    suspend fun editProfile(user: User, onError: OnError): Boolean {
        return try {
            val j = JsonObject()
            j.addProperty("email", user.email)
            j.addProperty("number", user.number)
            val res = requestService.editProfile(sharedPref.getToken(), j)
            checkForAuthentication(res as Response<Any>)
            if (res.isSuccessful)
                true
            else {
                MainScope().launch {
                    onError.onError("خطا")
                }
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MainScope().launch { onError.onError(e.message) }
            false
        }
    }

    suspend fun editPass(password: String, onError: OnError): Boolean {
        return try {
            val j = JsonObject()
            j.addProperty("password", password)
            val res = requestService.editProfile(sharedPref.getToken(), j)
            checkForAuthentication(res as Response<Any>)
            if (res.isSuccessful)
                true
            else {
                MainScope().launch {
                    onError.onError("خطا")
                }
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MainScope().launch { onError.onError(e.message) }
            false
        }
    }

    suspend fun logout(onError: OnError): Boolean {
        return try {
            val res = requestService.logout(sharedPref.getToken())
            checkForAuthentication(res as Response<Any>)
            if (res.isSuccessful)
                true
            else {
                MainScope().launch {
                    onError.onError("خطا")
                }
                false
            }

        } catch (e: Exception) {
            e.printStackTrace()
            MainScope().launch { onError.onError(e.message) }
            false
        }
    }

    suspend fun getTimes(onError: OnError): ArrayList<DateTime>? {
        try {
            val res = requestService.getDates()
            val arr = ArrayList<DateTime>()
            if (res.isSuccessful) {
                res.body()?.forEach {
                    val date = it.get("get_date").asString
                    val hour = it.get("hour").asString
                    arr.add(DateTime(hour, date))
                }
                return arr
            } else {
                MainScope().launch {
                    onError.onError("خطا")
                }
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MainScope().launch { onError.onError(e.message) }
            return null
        }
    }

    suspend fun getGifts(onError: OnError): ArrayList<Gift>? {
        try {
            val res = requestService.getGifts()
            val arr = ArrayList<Gift>()
            if (res.isSuccessful) {
                res.body()?.forEach {
                    val name = it.get("name").asString
                    val url = it.get("url").asString
                    arr.add(Gift(name, url))
                }
                return arr
            } else {
                MainScope().launch {
                    onError.onError("خطا")
                }
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MainScope().launch { onError.onError(e.message) }
            return null
        }
    }

    suspend fun getPromos(onError: OnError): ArrayList<Gift>? {
        try {
            val res = requestService.getPromos()
            val arr = ArrayList<Gift>()
            if (res.isSuccessful) {
                res.body()?.forEach {
                    val name = it.get("name").asString
                    val url = it.get("url").asString
                    arr.add(Gift(name, url))
                }
                return arr
            } else {
                MainScope().launch {
                    onError.onError("خطا")
                }
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MainScope().launch { onError.onError(e.message) }
            return null
        }
    }

    suspend fun addAddress(address: Address, onError: OnError): Boolean {
        return try {
            val j = JsonObject()
            j.addProperty("lat", address.lat)
            j.addProperty("lng", address.lng)
            j.addProperty("address", address.address)
            j.addProperty("phone", address.phone)
            j.addProperty("delete", 0)
            val res = requestService.addAddress(sharedPref.getToken(), j)
            checkForAuthentication(res as Response<Any>)
            if (res.isSuccessful)
                true
            else {
                MainScope().launch {
                    onError.onError("خطا")
                }
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MainScope().launch { onError.onError(e.message) }
            false
        }
    }

    suspend fun getAddresses(onError: OnError): ArrayList<Address>? {
        try {
            val res = requestService.getAddresses(sharedPref.getToken())
            checkForAuthentication(res as Response<Any>)
            if (res.isSuccessful) {
                val arr = ArrayList<Address>()
                res.body()?.forEach {
                    val lat = it.get("lat").asString
                    val lng = it.get("lng").asString
                    val address = it.get("address").asString
                    val phone = it.get("phone").asString
                    arr.add(Address(lat, lng, address, phone))
                }
                return arr
            } else {
                MainScope().launch {
                    onError.onError("خطا")
                }
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MainScope().launch { onError.onError(e.message) }
            return null
        }

    }

    suspend fun editAddress(address: Address, onError: OnError): Boolean {
        return try {
            val j = JsonObject()
            j.addProperty("lat", address.lat)
            j.addProperty("lng", address.lng)
            j.addProperty("address", address.address)
            j.addProperty("phone", address.phone)
            j.addProperty("delete", 0)
            val res = requestService.addAddress(sharedPref.getToken(), j)
            checkForAuthentication(res as Response<Any>)
            if (res.isSuccessful)
                true
            else {
                MainScope().launch {
                    onError.onError("خطا")
                }
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MainScope().launch { onError.onError(e.message) }
            false
        }
    }

    suspend fun deleteAddress(address: Address, onError: OnError): Boolean {
        return try {
            val j = JsonObject()
            j.addProperty("lat", address.lat)
            j.addProperty("lng", address.lng)
            j.addProperty("address", address.address)
            j.addProperty("phone", address.phone)
            j.addProperty("delete", 1)
            val res = requestService.addAddress(sharedPref.getToken(), j)
            checkForAuthentication(res as Response<Any>)
            if (res.isSuccessful)
                true
            else {
                MainScope().launch {
                    onError.onError("خطا")
                }
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MainScope().launch { onError.onError(e.message) }
            false
        }
    }


    private fun checkForAuthentication(res: Response<Any>) {
        if (res.code() == 403 || res.code() == 401)
            sharedPref.saveToken("")
    }

    interface RequestService {
        companion object {
            private const val URL = "https://chapferdositst.ir/wp-json/"

            private var requestService: RequestService? = null
            operator fun invoke(): RequestService {
                return if (requestService != null)
                    requestService!!
                else {
                    val interceptor = HttpLoggingInterceptor()
                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                    val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
                    requestService = Retrofit.Builder()
                        .baseUrl(URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(RequestService::class.java)
                    requestService!!
                }
            }
        }


        @FormUrlEncoded
        @POST("signin/login")
        suspend fun login(
            @Field("username") username: String, @Field("password") password: String
        ): Response<JsonObject>

        @FormUrlEncoded
        @POST("signup/register")
        suspend fun register(
            @Field("username") username: String, @Field("name") name: String,
            @Field("familyName") familyName: String, @Field("email") email: String,
            @Field("mobile") mobile: String, @Field("password") password: String
        ): Response<JsonObject>

        @GET("all/posts")
        suspend fun getPosts(): Response<ArrayList<JsonObject>>

        @FormUrlEncoded
        @POST("all/posts")
        suspend fun addPost(
            @Field("token") token: String,
            @Field("title") title: String,
            @Field("content") content: String
        ): Response<JsonObject>

        @GET("user/profile")
        suspend fun getProfile(@Header("Authorization") token: String): Response<JsonObject>

        @POST("user/profile")
        suspend fun editProfile(
            @Header("Authorization") token: String,
            @Body body: JsonObject
        ): Response<JsonObject>

        @GET("signout/logout")
        suspend fun logout(@Header("Authorization") token: String): Response<Boolean>

        @GET("all/dates")
        suspend fun getDates(): Response<ArrayList<JsonObject>>

        @GET("all/gifts")
        suspend fun getGifts(): Response<ArrayList<JsonObject>>

        @GET("all/promos")
        suspend fun getPromos(): Response<ArrayList<JsonObject>>

        @POST("all/addresses")
        suspend fun addAddress(
            @Header("Authorization") token: String,
            @Body body: JsonObject
        ): Response<JsonObject>

        @GET("all/addresses")
        suspend fun getAddresses(@Header("Authorization") token: String): Response<ArrayList<JsonObject>>

    }
}