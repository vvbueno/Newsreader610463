package nl.bueno.henry.`interface`

import nl.bueno.henry.model.Feed
import retrofit2.Call
import retrofit2.http.*

interface FeedService {
    @GET("articles")
    fun getAllFeeds(): Call<List<Feed>>
}