package nl.bueno.henry.service.response
import nl.bueno.henry.model.Article

data class ArticlesResponse(
    val Results: List<Article>,
    val NextId: Int?,
)