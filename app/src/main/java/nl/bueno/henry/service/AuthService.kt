package nl.bueno.henry.`interface`

import nl.bueno.henry.session.LoginResponse
import nl.bueno.henry.session.RegisterResponse
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