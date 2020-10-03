package nl.bueno.henry.service

import nl.bueno.henry.service.response.LoginResponse
import nl.bueno.henry.service.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.*

interface AuthService {

    @FormUrlEncoded
    @POST("users/login")
    fun login( @Field("UserName") username: String, @Field("Password") password: String): Call<LoginResponse>

    @FormUrlEncoded
    @POST("users/register")
    fun register(@Field("UserName") username: String, @Field("Password") password: String): Call<RegisterResponse>

}