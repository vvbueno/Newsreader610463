package nl.bueno.henry.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import nl.bueno.henry.adapter.ArticlesAdapter
import nl.bueno.henry.common.Common
import nl.bueno.henry.service.ArticleService
import nl.bueno.henry.service.response.ArticlesResponse
import nl.bueno.henry.R
import nl.bueno.henry.R.layout.fragment_liked
import nl.bueno.henry.adapter.LikedArticlesAdapter
import nl.bueno.henry.session.SessionManager
import nl.bueno.henry.utils.ToastHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 * Use the [LikedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LikedFragment : BaseFragment() {

    private val articleService: ArticleService = Common.articleService

    private lateinit var adapter: LikedArticlesAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var articlesSwipeRefresh: SwipeRefreshLayout
    private lateinit var articlesRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: called")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progressBar)

        articlesSwipeRefresh.setOnRefreshListener (
            object: SwipeRefreshLayout.OnRefreshListener {
                override fun onRefresh() {
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

        articlesRecyclerView = view.findViewById(R.id.likedArticlesRv)
        articlesSwipeRefresh = view.findViewById(R.id.articlesSwipeRefresh)
        layoutManager = LinearLayoutManager(this.context)
        adapter = LikedArticlesAdapter(this)

        articlesRecyclerView.layoutManager = layoutManager
        articlesRecyclerView.adapter = adapter


        return view
    }

    private fun showLoader(){
        isLoading = true
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoader(){
        isLoading = false
        progressBar.visibility = View.GONE
    }

    private fun getLikedArticles(){
            Log.d("ApiResponse", "getLikedArticles")
            articleService.getLikedArticles((SessionManager::getAuthToken)()).enqueue(object :
                Callback<ArticlesResponse> {
                override fun onResponse(call: Call<ArticlesResponse>, response: Response<ArticlesResponse>) {
                    if(response.body() != null){
                        if(adapter.itemCount > 0){
                            adapter.clearArticles()
                        }
                        nextId = response.body()!!.NextId
                        adapter.addArticles(response.body()!!.Results)
                    }else{
                        Log.d(TAG, response.code().toString())
                    }
                    articlesSwipeRefresh.isRefreshing = false
                    hideLoader()
                }
                override fun onFailure(call: Call<ArticlesResponse>, t: Throwable) {
                    Log.d(TAG, "getLikedArticles failed, reason ${t.message.toString()}")
                    articlesSwipeRefresh.isRefreshing = false
                    hideLoader()
                    (ToastHelper::shortToast)("Error: ${t.message.toString()}. Try again.")
                }
            })
    }

    companion object {
        private const val TAG = "LikedFragment"
    }
}