package nl.bueno.henry

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import coil.load
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.activity_details.view.*
import nl.bueno.henry.Interface.ArticleService
import nl.bueno.henry.Common.Common


class DetailsActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)


        articleTitle.text = intent.getStringExtra("articleTitle")
        articleImage.load(intent.getStringExtra("articleImage"))
        articleSummary.text = Html.fromHtml( intent.getStringExtra("articleSummary"), Html.FROM_HTML_MODE_COMPACT)

        val relatedLinks = intent.getStringArrayListExtra("articleRelatedLinks") as ArrayList<String>

        if(relatedLinks.size > 0){
            relatedLabel.visibility = View.VISIBLE
            relatedLinks.forEach {
                articleRelatedLinks?.append(Html.fromHtml("<a href=\"$it\">$it</a>", Html.FROM_HTML_MODE_COMPACT))
                articleRelatedLinks?.append("\n")
            }
        }
    }

    companion object {
        private const val TAG = "DetailsActivity"
    }
}