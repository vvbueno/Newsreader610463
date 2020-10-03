package nl.bueno.henry.Interface

import nl.bueno.henry.Session.AuthToken
import retrofit2.Call
import retrofit2.http.*

interface AuthService {

    @FormUrlEncoded
    @POST("users/login")
    fun login( @Field("UserName") username: String, @Field("Password") password: String): Call<AuthToken>

    @FormUrlEncoded
    @POST("users/register")
    fun register(@Field("UserName") username: String, @Field("Password") password: String): Call<Void>

}