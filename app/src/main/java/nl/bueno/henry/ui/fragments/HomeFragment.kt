package nl.bueno.henry.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import nl.bueno.henry.R
import nl.bueno.henry.R.layout.fragment_home
import nl.bueno.henry.adapter.ArticlesAdapter
import nl.bueno.henry.common.Common
import nl.bueno.henry.service.ArticleService
import nl.bueno.henry.service.response.ArticlesResponse
import nl.bueno.henry.session.SessionManager
import nl.bueno.henry.ui.DetailsActivity
import nl.bueno.henry.utils.ToastHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException


/**
 * A class to store the fragment that contains the articles (home) screen
 */
class HomeFragment() : BaseFragment() {

    // service used to connect with api
    private val articleService: ArticleService = Common.articleService

    // recycler view properties
    private lateinit var adapter: ArticlesAdapter
    private lateinit var layoutManager: LinearLayoutManager

    // ui elements
    private lateinit var articlesNestedScrollView: NestedScrollView
    private lateinit var articlesSwipeRefresh: SwipeRefreshLayout
    private lateinit var articlesRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorLabel : TextView

    private var nextId: Int? = null // store the next page of the articles to load increasingly
    private var isLoading: Boolean = false // to define whether we are loading new articles at the moment
    private val limitPerPage : Int = 20 // maximum articles to fetch at a time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init ui elements
        progressBar = view.findViewById(R.id.progressBar)
        errorLabel = view.findViewById(R.id.errorLabel)

        // add articles to ui
        getArticles(null)

        // listener for when we pull the screen down to refresh
        articlesSwipeRefresh.setOnRefreshListener(
            object : SwipeRefreshLayout.OnRefreshListener {
                override fun onRefresh() {
                    // only perform operations while we are not loading
                    if (!isLoading) {
                        isLoading = true
                        getArticles(null)
                    }
                }
            })

        // listener to load more articles when reaching the bottom
        articlesNestedScrollView.setOnScrollChangeListener(
            object : NestedScrollView.OnScrollChangeListener {
                override fun onScrollChange(
                    v: NestedScrollView,
                    scrollX: Int,
                    scrollY: Int,
                    oldScrollX: Int,
                    oldScrollY: Int
                ) {
                    // only perform operations while we are not loading
                    if (!isLoading) {
                        // defines reaching bottom
                        if (scrollY == (v.getChildAt(0).measuredHeight - v.measuredHeight)) {
                            showLoader()
                            getArticles(nextId)
                        }
                    }
                }
            })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(fragment_home, container, false)

        // init ui elements
        articlesRecyclerView = view.findViewById(R.id.articlesRv)
        articlesNestedScrollView = view.findViewById(R.id.articlesNestedScrollView)
        articlesSwipeRefresh = view.findViewById(R.id.articlesSwipeRefresh)

        // init rv properties
        layoutManager = LinearLayoutManager(this.context)
        adapter = ArticlesAdapter(this)

        articlesRecyclerView.layoutManager = layoutManager
        articlesRecyclerView.adapter = adapter

        return view
    }

    private fun getArticles(nextArticleId: Int?){

        // hide any error while we fetch articles
        hideError()

        // if we dont have a nextId, fetch the most recent articles, otherwise load them incrementally
        if(nextArticleId == null){
            Log.d(TAG, "getLatestArticles")
            articleService.getLatestArticles(limitPerPage, (SessionManager::getAuthToken)()).enqueue(
                object : Callback<ArticlesResponse> {
                    override fun onResponse(
                        call: Call<ArticlesResponse>,
                        response: Response<ArticlesResponse>
                    ) {
                        if (response.body() != null) {

                            // this check is performed to reset the view when refreshing,
                            // if there are items, clear the rv
                            if (adapter.itemCount > 0) {
                                adapter.clearArticles()
                            }

                            nextId = response.body()!!.NextId
                            adapter.addArticles(response.body()!!.Results)

                            // if no articles found, display error
                            if (adapter.itemCount == 0) {
                                showError(getString(R.string.no_articles_found))
                            }

                        } else {
                            showError(getString(R.string.no_articles_found))
                            Log.d(TAG, response.code().toString())
                        }
                        articlesSwipeRefresh.isRefreshing = false
                        hideLoader()
                    }

                    override fun onFailure(call: Call<ArticlesResponse>, t: Throwable) {
                        Log.d(TAG, "getLatestArticles failed, reason ${t.message.toString()}")

                        if (t is UnknownHostException) { // catch internet error
                            showError(getString(R.string.internet_error_get_articles))
                        } else if (t is TimeoutException) { // catch timeout error
                            showError(getString(R.string.error_refresh))
                        } else {
                            showError("${getString(R.string.error_refresh)}. Error: ${t.message.toString()}.")
                        }

                        // hide loaders
                        articlesSwipeRefresh.isRefreshing = false
                        hideLoader()
                    }
                })
        }else{
            Log.d(TAG, "getNextArticles")
            articleService.getNextArticles(
                nextArticleId,
                limitPerPage,
                (SessionManager::getAuthToken)()
            ).enqueue(object :
                Callback<ArticlesResponse> {
                override fun onResponse(
                    call: Call<ArticlesResponse>,
                    response: Response<ArticlesResponse>
                ) {
                    if (response.body() != null) {
                        nextId = response.body()!!.NextId
                        adapter.addArticles(response.body()!!.Results)

                        if (adapter.itemCount == 0) {
                            showError(getString(R.string.no_articles_found))
                        }

                    } else {
                        Log.d(TAG, response.code().toString())
                    }

                    articlesSwipeRefresh.isRefreshing = false
                    hideLoader()
                }

                override fun onFailure(call: Call<ArticlesResponse>, t: Throwable) {
                    Log.d(TAG, "getNextArticles failed, reason ${t.message.toString()}")

                    if (t is UnknownHostException) { // catch internet error
                        showError("Could not get articles. Please check your internet connection")
                        showError(getString(R.string.internet_error_get_articles))
                    } else if (t is TimeoutException) { // catch timeout error
                        showError(getString(R.string.error_refresh))
                    } else { // other errors
                        showError("${getString(R.string.error_refresh)}. Error: ${t.message.toString()}.")
                    }

                    // hide loaders
                    articlesSwipeRefresh.isRefreshing = false
                    hideLoader()
                }
            })
        }
    }

    fun showLoader(){
        isLoading = true
        progressBar.visibility = View.VISIBLE
    }

    fun hideLoader(){
        isLoading = false
        progressBar.visibility = View.GONE
    }

    private fun showError(message: String){
        errorLabel.visibility = View.VISIBLE
        errorLabel.text = message
    }

    private fun hideError(){
        errorLabel.visibility = View.GONE
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}