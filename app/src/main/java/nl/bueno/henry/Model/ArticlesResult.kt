package nl.bueno.henry.Model

data class ArticlesResult(
    val Results: List<Article>,
    val NextId: Int?,
)