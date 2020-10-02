package nl.bueno.henry.Interface

import nl.bueno.henry.Model.ArticlesResult
import retrofit2.Call
import retrofit2.http.*

interface ArticleService {

    @GET("articles")
    fun getLatestArticles(@Query("count") maximumArticles: Int): Call<ArticlesResult>

    @GET("articles/{id}")
    fun getNextArticles(@Path("id") articleId: Int?, @Query("count") maximumArticles: Int?): Call<ArticlesResult>

    @GET("articles/liked")
    fun getLikedArticles(@Query("NextId") NextId: Int?): Call<ArticlesResult>

    @GET("articles/{id}")
    fun getArticle(@Path("id") articleId: Int): Call<ArticlesResult>

    @PUT("articles/{id}/like")
    fun likeArticle(@Path("id") articleId: Int): Call<Void>

    @DELETE("articles/{id}/like")
    fun unlikeArticle(@Path("id") articleId: Int): Call<Void>
}