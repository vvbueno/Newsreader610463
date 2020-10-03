package nl.bueno.henry.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import coil.load
import nl.bueno.henry.R


class DetailsActivity : AppCompatActivity() {

    private lateinit var articleTitle : TextView
    private lateinit var articleImage : ImageView
    private lateinit var articleSummary : TextView
    private lateinit var relatedLabel : TextView
    private lateinit var articleRelatedLinks : TextView

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        articleTitle = findViewById(R.id.articleTitle)
        articleImage = findViewById(R.id.articleImage)
        articleSummary = findViewById(R.id.articleSummary)
        relatedLabel = findViewById(R.id.relatedLabel)
        articleRelatedLinks = findViewById(R.id.articleRelatedLinks)


        articleTitle.text = intent.getStringExtra("articleTitle")
        articleImage.load(intent.getStringExtra("articleImage"))
        articleSummary.text = Html.fromHtml( intent.getStringExtra("articleSummary"), Html.FROM_HTML_MODE_COMPACT)

        val relatedLinks = intent.getStringArrayListExtra("articleRelatedLinks") as ArrayList<String>

        if(relatedLinks.size > 0){
            relatedLabel.visibility = View.VISIBLE
            relatedLinks.forEach {
                articleRelatedLinks.append(Html.fromHtml("<a href=\"$it\">$it</a>", Html.FROM_HTML_MODE_COMPACT))
                articleRelatedLinks.append("\n")
            }
        }
    }

    companion object {
        private const val TAG = "DetailsActivity"
    }
}