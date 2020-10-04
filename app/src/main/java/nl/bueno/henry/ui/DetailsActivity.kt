package nl.bueno.henry.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import coil.load
import nl.bueno.henry.R
import nl.bueno.henry.common.Common
import nl.bueno.henry.model.Article
import nl.bueno.henry.model.Category
import nl.bueno.henry.service.ArticleService
import nl.bueno.henry.session.SessionManager
import nl.bueno.henry.utils.ToastHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat


class DetailsActivity : AppCompatActivity() {

    // service to connect to the api
    private val articleService: ArticleService = Common.articleService

    // to store article being detailed
    private lateinit var article : Article

    // ui elements
    private lateinit var articleTitle : TextView
    private lateinit var articleImage : ImageView
    private lateinit var likeIcon : ImageButton
    private lateinit var articleSummary : TextView
    private lateinit var dateLabel : TextView
    private lateinit var tagsLabel : TextView
    private lateinit var articleUrl : TextView
    private lateinit var relatedLabel : TextView
    private lateinit var articleRelatedLinks : TextView

    // like icon color state
    private val likedColor: Int = android.graphics.Color.argb(255, 255, 0, 0)
    private val unLikedColor: Int = android.graphics.Color.argb(255, 128, 128, 128)

    // loading state for when an item is being liked (to prevent liking or unliking before the current liking and unliking is finished)
    private var isBeingLiked: Boolean = false

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // init ui elements
        articleTitle = findViewById(R.id.articleTitle)
        articleImage = findViewById(R.id.articleImage)
        likeIcon = findViewById(R.id.likeIcon)
        articleSummary = findViewById(R.id.articleSummary)
        dateLabel = findViewById(R.id.dateLabel)
        tagsLabel = findViewById(R.id.tagsLabel)
        articleUrl = findViewById(R.id.articleUrl)
        relatedLabel = findViewById(R.id.relatedLabel)
        articleRelatedLinks = findViewById(R.id.articleRelatedLinks)

        // get the passed article from the intent
        article = (intent.getSerializableExtra("article") as? Article)!!

        // set attributes of the article in the view
        articleTitle.text = article.Title
        articleImage.load(article.Image)

        if(!article.IsLiked!!){
            likeIcon.setColorFilter(unLikedColor)
        }else{
            likeIcon.setColorFilter(likedColor)
        }

        articleSummary.text = Html.fromHtml(article.Summary, Html.FROM_HTML_MODE_COMPACT)

        // parse date into May 03, 2020
        val parser =  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val formatter = SimpleDateFormat("MMMM dd, yyyy")
        val formattedDate = formatter.format(parser.parse(article.PublishDate!!))

        dateLabel.text = formattedDate

        // append a element to label
        articleUrl.append(
            " " + Html.fromHtml(
                "<a href=\"${article.Url}\">${article.Url}</a>",
                Html.FROM_HTML_MODE_COMPACT
            )
        )

        val categories = article.Categories as ArrayList<Category>

        // for each category append it to the tags label
        if(categories.size > 0){
            tagsLabel.visibility = View.VISIBLE
            categories.forEachIndexed { index, element ->
                tagsLabel.append(" ${element.Name}")

                // don't add comma on last element
                if(index != categories.size -1){
                    tagsLabel.append(",")
                }
            }
        }

        val relatedLinks = article.Related as ArrayList<String>

        // for each related append it to the relatedLinks text as a html
        if(relatedLinks.size > 0){
            relatedLabel.visibility = View.VISIBLE
            relatedLinks.forEach {
                articleRelatedLinks.append(
                    Html.fromHtml(
                        "<a href=\"$it\">$it</a>",
                        Html.FROM_HTML_MODE_COMPACT
                    )
                )
                articleRelatedLinks.append("\n\n")
            }
        }

        likeIcon.setOnClickListener{
            // only like while we are not already liking
            if(!isBeingLiked){
                isBeingLiked = true
                likeArticle(article)
            }
        }
    }

    // to go back to the previous activity emulating the back button on android
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun likeArticle(article: Article){

        if(!article.IsLiked!!){

            Log.d(TAG, "likeArticle")

            articleService.likeArticle(article.Id, (SessionManager::getAuthToken)()).enqueue(object :
                Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code().toString()) {
                        "200" -> {
                            Log.d(TAG, response.code().toString())
                            article.IsLiked = true
                            likeIcon.setColorFilter(likedColor)
                        }
                        "401" -> {
                            (ToastHelper::shortToast)(getString(R.string.unauthenticated))
                        }
                        else -> { // Note the block
                            (ToastHelper::shortToast)(getString(R.string.unexpected_error))
                        }
                    }

                    // disable like loading
                    isBeingLiked = false
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d(TAG, t.message.toString())
                    (ToastHelper::shortToast)(getString(R.string.liking_error))
                    isBeingLiked = false  // disable like loading
                }
            })

        }else{

            Log.d(TAG, "unlikeArticle")

            articleService.unlikeArticle(article.Id, (SessionManager::getAuthToken)()).enqueue(
                object :
                    Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        when (response.code().toString()) {
                            "200" -> {
                                Log.d(TAG, response.code().toString())
                                article.IsLiked = false
                                likeIcon.setColorFilter(unLikedColor)
                            }
                            "401" -> {
                                (ToastHelper::shortToast)(getString(R.string.unauthenticated))
                            }
                            else -> { // Note the block
                                (ToastHelper::shortToast)(getString(R.string.unexpected_error))
                            }
                        }

                        // disable like loading
                        isBeingLiked = false
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.d(TAG, t.message.toString())
                        (ToastHelper::shortToast)(getString(R.string.liking_error))
                        isBeingLiked = false  // disable like loading
                    }
                })
        }
    }

    companion object {
        private const val TAG = "DetailsActivity"
    }
}