package nl.bueno.henry.Model

data class Article(
    val Id: Int,
    val Feed: Int?,
    val Title: String?,
    val Summary: String?,
    val PublishDate: String?,
    val Image: String?,
    val Url: String?,
    val Related: List<String>?,
    val Categories: List<Category>?,
    var IsLiked: Boolean?
)