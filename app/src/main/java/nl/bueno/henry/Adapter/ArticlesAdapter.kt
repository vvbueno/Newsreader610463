package nl.bueno.henry.Adapter

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kotlinx.android.synthetic.main.article_row.view.*
import nl.bueno.henry.Common.Common
import nl.bueno.henry.DetailsActivity
import nl.bueno.henry.Interface.ArticleService
import nl.bueno.henry.MainActivity
import nl.bueno.henry.Model.Category
import nl.bueno.henry.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticlesAdapter(private val activity: MainActivity) : RecyclerView.Adapter<ArticlesAdapter.ViewHolder>() {

    private val articleService: ArticleService = Common.articleService

    private val likedColor: Int = Color.argb(255, 255, 0, 0)
    private val unLikedColor: Int = Color.argb(255, 0, 0, 0)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.article_row, parent, false)

        val holder =  ViewHolder(view)

        view.setOnClickListener {
            val intent = Intent(parent.context, DetailsActivity::class.java)
            intent.putExtra("articleTitle", activity.articles[holder.adapterPosition].Title)
            intent.putExtra("articleSummary", activity.articles[holder.adapterPosition].Summary)

            intent.putExtra(
                "articlePublishDate",
                activity.articles[holder.adapterPosition].PublishDate
            )
            intent.putExtra("articleImage", activity.articles[holder.adapterPosition].Image)
            intent.putExtra("articleUrl", activity.articles[holder.adapterPosition].Url)

            val related = activity.articles[holder.adapterPosition].Related as ArrayList<String>
            Log.d("TEST", related.toString())
            intent.putExtra("articleRelatedLinks", related)

            val articles = activity.articles[holder.adapterPosition].Categories as ArrayList<Category>

            intent.putExtra(
                "articleCategories",
                articles
            )
            intent.putExtra("articleIsLiked", activity.articles[holder.adapterPosition].IsLiked)
            parent.context.startActivity(intent)
        }

        view.likeIcon.setOnClickListener {
            likeArticle(holder)
        }

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
     //   Log.d("ApiResponse", "adding a new article to the view")
        val article = activity.articles[position]
        holder.title.text = article.Title
        holder.itemView.thumbnail.load(article.Image)//{
     //       placeholder(R.drawable.ic_baseline_aspect_ratio_24)
      //  }
    }

    override fun getItemCount() = activity.articles.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.title
    }

    private fun likeArticle(holder: ViewHolder){
        Log.d("ApiResponse", "likeArticle")
        val article = activity.articles[holder.adapterPosition]

        if(!article.IsLiked!!){
            articleService.likeArticle(article.Id).enqueue(object :
                Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d("ApiResponse response", response.code().toString())
                    activity.articles[holder.adapterPosition].IsLiked = true
                    holder.itemView.likeIcon.setColorFilter(likedColor)
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("ApiResponse", "The call failed")
                    Log.d("ApiResponse error", t.message.toString())
                }
            })
        }else{
            articleService.unlikeArticle(article.Id).enqueue(object :
                Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d("ApiResponse response", response.code().toString())
                    activity.articles[holder.adapterPosition].IsLiked = false
                    holder.itemView.likeIcon.setColorFilter(unLikedColor)
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("ApiResponse", "The call failed")
                    Log.d("ApiResponse error", t.message.toString())
                }
            })
        }
    }
}