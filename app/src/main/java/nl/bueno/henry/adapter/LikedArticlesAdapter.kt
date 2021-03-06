package nl.bueno.henry.adapter

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import kotlinx.android.synthetic.main.article_row.view.*
import nl.bueno.henry.common.Common
import nl.bueno.henry.ui.DetailsActivity
import nl.bueno.henry.service.ArticleService
import nl.bueno.henry.model.Article
import nl.bueno.henry.R
import nl.bueno.henry.session.SessionManager
import nl.bueno.henry.ui.fragments.BaseFragment
import nl.bueno.henry.ui.fragments.LoginFragment
import nl.bueno.henry.utils.ToastHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LikedArticlesAdapter(private var fragment: BaseFragment) : RecyclerView.Adapter<LikedArticlesAdapter.ViewHolder>() {

    // Data list to be included in the recyclerview
    private var articles : MutableList<Article> = ArrayList()

    // service used to like an article
    private val articleService: ArticleService = Common.articleService

    // Colors for the like icon state
    private val likedColor: Int = Color.argb(255, 255, 0, 0)
    private val unLikedColor: Int = Color.argb(255, 128, 128, 128)

    // loading state for when an item is being liked (to prevent liking or unliking before the current liking and unliking is finished)
    private var isBeingLiked: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.article_row, parent, false)

        val holder = ViewHolder(view)

        view.setOnClickListener {
            // Open the details page of the article
            val intent = Intent(parent.context, DetailsActivity::class.java)
            intent.putExtra("article", articles[holder.adapterPosition])
            parent.context.startActivity(intent)
        }

        view.likeIcon.setOnClickListener {

            // if the user is not logged In, redirect them to the login screen
            if(!(SessionManager::isLoggedIn)()){
                fragment.activity?.supportFragmentManager?.beginTransaction()?.apply {
                    replace(R.id.fl_wrapper, LoginFragment())
                    commit()
                }
            }else{
                // otherwise perform like action
                if(!isBeingLiked){
                    isBeingLiked = true
                    likeArticle(holder)
                }
            }
        }
        return holder
    }

    // update the list of articles to display
    fun addArticles(articles: List<Article>) {
        this.articles.addAll(articles)
        notifyDataSetChanged()
    }

    // clear the list of articles
    fun clearArticles() {
        this.articles.removeAll(this.articles)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        holder.title.text = article.Title

        // load view properties
        holder.thumbnail.load(article.Image){
            placeholder(R.drawable.ic_baseline_aspect_ratio_24)
            scale(Scale.FILL)
        }

        // set color depending of the state of isLiked
        if(!article.IsLiked!!){
            holder.likeIcon.setColorFilter(unLikedColor)
        }else{
            holder.likeIcon.setColorFilter(likedColor)
        }
    }

    override fun getItemCount() = articles.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.title
        val thumbnail: ImageView = itemView.thumbnail
        val likeIcon: ImageButton = itemView.likeIcon
    }

    private fun likeArticle(holder: ViewHolder){

        val article = articles[holder.adapterPosition] // get current article

        if(!article.IsLiked!!){

            Log.d(TAG, "likeArticle")

            articleService.likeArticle(article.Id, (SessionManager::getAuthToken)()).enqueue(object :
                Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code().toString()) {
                        "200" -> {
                            // update article visually on 200 response
                            articles[holder.adapterPosition].IsLiked = true
                            holder.itemView.likeIcon.setColorFilter(likedColor)
                            notifyDataSetChanged()
                        }
                        "401" -> {
                            fragment.context?.getString(R.string.unauthenticated)?.let {
                                (ToastHelper::shortToast)(
                                    it
                                )
                            }
                        }
                        else -> {
                            fragment.context?.getString(R.string.unexpected_error)?.let {
                                (ToastHelper::shortToast)(
                                    it
                                )
                            }
                        }
                    }
                    // disable like load
                    isBeingLiked = false
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d(TAG, t.message.toString())
                    fragment.context?.getString(R.string.liking_error)?.let {
                        (ToastHelper::shortToast)(
                            it
                        )
                    }
                    // disable like load
                    isBeingLiked = false
                }
            })

        }else{

            Log.d(TAG, "unlikeArticle")

            articleService.unlikeArticle(article.Id, (SessionManager::getAuthToken)()).enqueue(object :
                Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code().toString()) {
                        "200" -> {
                            // update article visually on 200 response
                            articles[holder.adapterPosition].IsLiked = false
                            articles.remove(articles[holder.adapterPosition])
                            notifyDataSetChanged()
                        }
                        "401" -> {
                            fragment.context?.getString(R.string.unauthenticated)?.let {
                                (ToastHelper::shortToast)(
                                    it
                                )
                            }
                        }
                        else -> {
                            fragment.context?.getString(R.string.unexpected_error)?.let {
                                (ToastHelper::shortToast)(
                                    it
                                )
                            }
                        }
                    }
                    // disable like load
                    isBeingLiked = false
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d(TAG, "The call failed")
                    Log.d(TAG, t.message.toString())
                    fragment.context?.getString(R.string.liking_error)?.let {
                        (ToastHelper::shortToast)(
                            it
                        )
                    }
                    // disable like load
                    isBeingLiked = false
                }
            })
        }
    }

    companion object {
        private const val TAG = "LikedArticlesAdapter"
    }
}