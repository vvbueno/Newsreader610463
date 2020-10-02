package nl.bueno.henry.Common

import nl.bueno.henry.Interface.ArticleService
import nl.bueno.henry.Interface.AuthService
import nl.bueno.henry.Interface.FeedService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Common {

    private const val BASE_URL = "https://inhollandbackend.azurewebsites.net/api/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    val articleService: ArticleService by lazy {
        retrofit.create(ArticleService::class.java)
    }

    val feedService: FeedService by lazy {
        retrofit.create(FeedService::class.java)
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

}