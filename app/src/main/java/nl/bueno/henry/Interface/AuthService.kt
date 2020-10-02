package nl.bueno.henry.Interface

import nl.bueno.henry.Model.Feed
import retrofit2.Call
import retrofit2.http.*

interface AuthService {

    @FormUrlEncoded
    @POST("users/login")
    fun login(@Field("UserName") username: Int, @Field("Password") password: Int): Call<Void>

    @FormUrlEncoded
    @POST("users/register")
    fun register(@Field("UserName") username: Int, @Field("Password") password: Int): Call<Void>

}