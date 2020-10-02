package nl.bueno.henry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import nl.bueno.henry.Adapter.ArticlesAdapter
import nl.bueno.henry.Interface.ArticleService
import nl.bueno.henry.Common.Common
import nl.bueno.henry.Interface.AuthService
import nl.bueno.henry.Interface.FeedService
import nl.bueno.henry.Model.Article
import nl.bueno.henry.Model.ArticlesResult
import nl.bueno.henry.Session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val articleService: ArticleService = Common.articleService
    private val feedService: FeedService = Common.feedService
    private val authService: AuthService = Common.authService

    val articles: MutableList<Article> = ArrayList()

    lateinit var adapter: ArticlesAdapter
    lateinit var layoutManager: LinearLayoutManager

    private var nextId: Int? = null

    private var page: Int = 0

    private var isLoading: Boolean = false

    private val limitPerPage : Int = 20;

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        layoutManager = LinearLayoutManager(this@MainActivity)
        adapter = ArticlesAdapter(this@MainActivity)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        if(articles.isEmpty()){
            getArticles()
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if (dy > 0) {
                    val visibleItemCount = layoutManager.childCount
                    val pastVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                    val total = adapter.itemCount

                    if (!isLoading) {
                        if ((visibleItemCount + pastVisibleItem) >= total) {
                            isLoading = true
                            progressBar.visibility = View.VISIBLE
                            page++
                            Log.d("ApiResponse", "nextId: $nextId")
                            getNextArticles(nextId)
                        }

                    }
                }

                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    private fun getArticles(){

        Log.d("ApiResponse", "getArticles")

        articleService.getLatestArticles(limitPerPage).enqueue(object : Callback<ArticlesResult> {
            override fun onResponse(call: Call<ArticlesResult>, response: Response<ArticlesResult>) {
                if(response.body() != null){
                    articles.addAll(response.body()!!.Results)
                    nextId = response.body()!!.NextId
                    adapter.notifyDataSetChanged()
                    isLoading = false
                    progressBar.visibility = View.GONE
                }
            }
            override fun onFailure(call: Call<ArticlesResult>, t: Throwable) {
                Log.d("ApiResponse", "The call failed")
                Log.d("ApiResponse error", t.message.toString())
                if( t.message.toString() == "timeout"){
                    Log.d("ApiResponse", "Trying again....")
                    getArticles()
                }
            }
        })
    }

    private fun getNextArticles(nextArticleId: Int?){

        Log.d("ApiResponse", "getNextArticles")

        articleService.getNextArticles(nextArticleId, limitPerPage).enqueue(object : Callback<ArticlesResult> {
            override fun onResponse(call: Call<ArticlesResult>, response: Response<ArticlesResult>) {
                if(response.body() != null){
                    articles.addAll(response.body()!!.Results)
                    nextId = response.body()!!.NextId
                    adapter.notifyDataSetChanged()
                    isLoading = false
                    progressBar.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ArticlesResult>, t: Throwable) {
                Log.d("ApiResponse", "The call failed")
                Log.d("ApiResponse error", t.message.toString())
            }
        })
    }


    companion object {
        private const val TAG = "MainActivity"
    }
}