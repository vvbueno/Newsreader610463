package nl.bueno.henry.Interface

import nl.bueno.henry.Model.Feed
import retrofit2.Call
import retrofit2.http.*

interface FeedService {
    @GET("articles")
    fun getAllFeeds(): Call<List<Feed>>
}