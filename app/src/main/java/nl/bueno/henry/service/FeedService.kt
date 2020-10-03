package nl.bueno.henry.service

import nl.bueno.henry.service.response.FeedsResponse
import retrofit2.Call
import retrofit2.http.*

interface FeedService {
    @GET("feeds")
    fun getAllFeeds(): Call<FeedsResponse>
}