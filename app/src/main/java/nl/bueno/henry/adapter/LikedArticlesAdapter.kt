package nl.bueno.henry.adapter

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
import nl.bueno.henry.common.Common
import nl.bueno.henry.ui.DetailsActivity
import nl.bueno.henry.service.ArticleService
import nl.bueno.henry.model.Article
import nl.bueno.henry.model.Category
import nl.bueno.henry.R
import nl.bueno.henry.session.SessionManager
import nl.bueno.henry.ui.fragments.BaseFragment
import nl.bueno.henry.ui.fragments.LoginFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LikedArticlesAdapter(private var fragment: BaseFragment) : RecyclerView.Adapter<LikedArticlesAdapter.ViewHolder>() {

    private var articles : MutableList<Article> = ArrayList()

    private val articleService: ArticleService = Common.articleService

    private val likedColor: Int = Color.argb(255, 255, 0, 0)
    private val unLikedColor: Int = Color.argb(255, 0, 0, 0)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.article_row, parent, false)

        val holder = ViewHolder(view)

        view.setOnClickListener {
            val intent = Intent(parent.context, DetailsActivity::class.java)
            intent.putExtra("articleTitle", articles[holder.adapterPosition].Title)
            intent.putExtra("articleSummary", articles[holder.adapterPosition].Summary)

            intent.putExtra(
                "articlePublishDate",
                articles[holder.adapterPosition].PublishDate
            )
            intent.putExtra("articleImage", articles[holder.adapterPosition].Image)
            intent.putExtra("articleUrl", articles[holder.adapterPosition].Url)

            val related = articles[holder.adapterPosition].Related as ArrayList<String>
            Log.d("TEST", related.toString())
            intent.putExtra("articleRelatedLinks", related)

            val categories = articles[holder.adapterPosition].Categories as ArrayList<Category>

            intent.putExtra(
                "articleCategories",
                categories
            )
            intent.putExtra("articleIsLiked", articles[holder.adapterPosition].IsLiked)
            parent.context.startActivity(intent)
        }

        view.likeIcon.setOnClickListener {
            if(!(SessionManager::isLoggedIn)()){
                fragment.activity?.supportFragmentManager?.beginTransaction()?.apply {
                    replace(R.id.fl_wrapper, LoginFragment())
                    commit()
                }
            }else{
                likeArticle(holder)
            }
        }
        return holder
    }

    fun addArticles(articles: List<Article>) {
        this.articles.addAll(articles)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //   Log.d("ApiResponse", "adding a new article to the view")
        val article = articles[position]
        holder.title.text = article.Title
        holder.itemView.thumbnail.load(article.Image)//{
        //       placeholder(R.drawable.ic_baseline_aspect_ratio_24)
        //  }
    }

    override fun getItemCount() = articles.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.title
    }

    private fun likeArticle(holder: ViewHolder){
        Log.d("ApiResponse", "likeArticle")
        val article = articles[holder.adapterPosition]

        if(!article.IsLiked!!){
            articleService.likeArticle(article.Id, (SessionManager::getAuthToken)()).enqueue(object :
                Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d("ApiResponse response", response.code().toString())
                    articles[holder.adapterPosition].IsLiked = true
                    holder.itemView.likeIcon.setColorFilter(likedColor)
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("ApiResponse", "The call failed")
                    Log.d("ApiResponse error", t.message.toString())
                }
            })
        }else{
            articleService.unlikeArticle(article.Id, (SessionManager::getAuthToken)()).enqueue(object :
                Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d("ApiResponse response", response.code().toString())
                    articles[holder.adapterPosition].IsLiked = false
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