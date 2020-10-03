package nl.bueno.henry.service.response
import nl.bueno.henry.model.Feed

data class FeedsResponse(
    val Results: List<Feed>,
)