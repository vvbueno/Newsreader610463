package nl.bueno.henry.common

import nl.bueno.henry.service.ArticleService
import nl.bueno.henry.service.AuthService
import nl.bueno.henry.service.FeedService
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