package nl.bueno.henry.service

import nl.bueno.henry.service.response.ArticlesResponse
import retrofit2.Call
import retrofit2.http.*

interface ArticleService {

    @GET("articles")
    fun getLatestArticles(@Query("count") maximumArticles: Int, @Header("x-authtoken") xauthtoken: String?): Call<ArticlesResponse>

    @GET("articles/{id}")
    fun getNextArticles(@Path("id") articleId: Int?, @Query("count") maximumArticles: Int?,  @Header("x-authtoken") xauthtoken : String?): Call<ArticlesResponse>

    @GET("articles/liked")
    fun getLikedArticles(@Header("x-authtoken") xauthtoken : String?): Call<ArticlesResponse>

    @GET("articles/{id}")
    fun getArticle(@Path("id") articleId: Int,  @Header("x-authtoken") xauthtoken : String?): Call<ArticlesResponse>

    @PUT("articles/{id}/like")
    fun likeArticle(@Path("id") articleId: Int, @Header("x-authtoken") xauthtoken : String?): Call<Void>

    @DELETE("articles/{id}/like")
    fun unlikeArticle(@Path("id") articleId: Int, @Header("x-authtoken") xauthtoken : String?): Call<Void>
}