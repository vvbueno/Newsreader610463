package nl.bueno.henry.ui

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
import nl.bueno.henry.service.ArticleService
import nl.bueno.henry.session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailsActivity : AppCompatActivity() {

    private val articleService: ArticleService = Common.articleService

    private lateinit var article : Article

    private lateinit var articleTitle : TextView
    private lateinit var articleImage : ImageView
    private lateinit var likeIcon : ImageButton
    private lateinit var articleSummary : TextView
    private lateinit var relatedLabel : TextView
    private lateinit var articleRelatedLinks : TextView

    private val likedColor: Int = android.graphics.Color.argb(255, 255, 0, 0)
    private val unLikedColor: Int = android.graphics.Color.argb(255, 128, 128, 128)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        articleTitle = findViewById(R.id.articleTitle)
        articleImage = findViewById(R.id.articleImage)
        likeIcon = findViewById(R.id.likeIcon)
        articleSummary = findViewById(R.id.articleSummary)
        relatedLabel = findViewById(R.id.relatedLabel)
        articleRelatedLinks = findViewById(R.id.articleRelatedLinks)

        article = (intent.getSerializableExtra("article") as? Article)!!

        articleTitle.text = article.Title
        articleImage.load(article.Image)

        if(!article.IsLiked!!){
            likeIcon.setColorFilter(unLikedColor)
        }else{
            likeIcon.setColorFilter(likedColor)
        }

        articleSummary.text = Html.fromHtml(article.Summary, Html.FROM_HTML_MODE_COMPACT)

        val relatedLinks = article.Related as ArrayList<String>

        if(relatedLinks.size > 0){
            relatedLabel.visibility = View.VISIBLE
            relatedLinks.forEach {
                articleRelatedLinks.append(
                    Html.fromHtml(
                        "<a href=\"$it\">$it</a>",
                        Html.FROM_HTML_MODE_COMPACT
                    )
                )
                articleRelatedLinks.append("\n")
            }
        }

        likeIcon.setOnClickListener{
            likeArticle(article)
        }
    }

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

            Log.d("ApiResponse", "likeArticle")

            articleService.likeArticle(article.Id, (SessionManager::getAuthToken)())
                .enqueue(object : Callback<Void> {

                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Log.d("ApiResponse response", response.code().toString())
                        article.IsLiked = true
                        likeIcon.setColorFilter(likedColor)
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.d("ApiResponse", "The call failed")
                        Log.d("ApiResponse error", t.message.toString())
                    }
                })

        }else{

            Log.d("ApiResponse", "unlikeArticle")

            articleService.unlikeArticle(article.Id, (SessionManager::getAuthToken)())
                .enqueue(object : Callback<Void> {

                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Log.d("ApiResponse response", response.code().toString())
                        article.IsLiked = false
                        likeIcon.setColorFilter(unLikedColor)
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.d("ApiResponse", "The call failed")
                        Log.d("ApiResponse error", t.message.toString())
                    }
                })
        }
    }

    companion object {
        private const val TAG = "DetailsActivity"
    }
}