package nl.bueno.henry.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import nl.bueno.henry.common.Common
import nl.bueno.henry.service.ArticleService
import nl.bueno.henry.service.response.ArticlesResponse
import nl.bueno.henry.R
import nl.bueno.henry.R.layout.fragment_liked
import nl.bueno.henry.adapter.LikedArticlesAdapter
import nl.bueno.henry.session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException


/**
 * A class to store the fragment that contains the liked screen
 */
class LikedFragment : BaseFragment() {

    // service to connect to the api
    private val articleService: ArticleService = Common.articleService

    // rv properties
    private lateinit var adapter: LikedArticlesAdapter
    private lateinit var layoutManager: LinearLayoutManager

    // ui elements
    private lateinit var articlesSwipeRefresh: SwipeRefreshLayout
    private lateinit var articlesRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorLabel : TextView

    private var nextId: Int? = null
    private var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
    }

    override fun onResume() {
        Log.d(TAG, "onResume: called")
        super.onResume()

        if(adapter.itemCount > 0){
            adapter.clearArticles()
            showLoader()
            getLikedArticles()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progressBar)
        errorLabel = view.findViewById(R.id.errorLabel)

        articlesSwipeRefresh.setOnRefreshListener (
            object: SwipeRefreshLayout.OnRefreshListener {
                override fun onRefresh() {
                    // only perform operations while we are not loading
                    if(!isLoading){
                        isLoading = true
                        getLikedArticles()
                    }
                }
            })

        showLoader()
        getLikedArticles()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view =  inflater.inflate(fragment_liked, container, false)

        // init ui elements
        articlesRecyclerView = view.findViewById(R.id.likedArticlesRv)
        articlesSwipeRefresh = view.findViewById(R.id.articlesSwipeRefresh)

        // rv properties
        layoutManager = LinearLayoutManager(this.context)
        adapter = LikedArticlesAdapter(this)

        articlesRecyclerView.layoutManager = layoutManager
        articlesRecyclerView.adapter = adapter

        return view
    }

    private fun getLikedArticles(){
        // hide any error while we fetch articles
        hideError()

        Log.d("ApiResponse", "getLikedArticles")
        articleService.getLikedArticles((SessionManager::getAuthToken)()).enqueue(object :
            Callback<ArticlesResponse> {
            override fun onResponse(call: Call<ArticlesResponse>, response: Response<ArticlesResponse>) {
                if(response.body() != null){

                    // this check is performed to reset the view when refreshing,
                    // if there are items, clear the rv
                    if(adapter.itemCount > 0){
                        adapter.clearArticles()
                    }

                    nextId = response.body()!!.NextId
                    adapter.addArticles(response.body()!!.Results)

                    // show error if no articles where fetched
                    if(adapter.itemCount == 0){
                        showError(getString(R.string.no_articles_yet))
                    }
                }else{
                    Log.d(TAG, response.code().toString())
                }
                articlesSwipeRefresh.isRefreshing = false
                hideLoader()
            }
            override fun onFailure(call: Call<ArticlesResponse>, t: Throwable) {
                Log.d(TAG, "getLikedArticles failed, reason ${t.message.toString()}")

                if(t is UnknownHostException){ // catch internet error
                    showError(getString(R.string.internet_error_get_articles))
                }else if(t is TimeoutException){ // catch timeout error
                    showError(getString(R.string.error_refresh))
                }else{
                    showError("Error: ${t.message.toString()}.")
                }

                articlesSwipeRefresh.isRefreshing = false
                hideLoader()
            }
        })
    }

    private fun showLoader(){
        isLoading = true
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoader(){
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
        private const val TAG = "LikedFragment"
    }
}