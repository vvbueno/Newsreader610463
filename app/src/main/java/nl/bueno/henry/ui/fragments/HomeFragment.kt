package nl.bueno.henry.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
import nl.bueno.henry.utils.ToastHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment() : BaseFragment() {

    private val articleService: ArticleService = Common.articleService

    private lateinit var adapter: ArticlesAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var articlesNestedScrollView: NestedScrollView
    private lateinit var articlesSwipeRefresh: SwipeRefreshLayout
    private lateinit var articlesRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar


    private var nextId: Int? = null
    private var isLoading: Boolean = false
    private val limitPerPage : Int = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progressBar)

        getArticles(null)

        articlesSwipeRefresh.setOnRefreshListener(
            object : SwipeRefreshLayout.OnRefreshListener {
                override fun onRefresh() {
                    if (!isLoading) {
                        isLoading = true
                        getArticles(null)
                    }
                }
            })

        articlesNestedScrollView.setOnScrollChangeListener(
            object : NestedScrollView.OnScrollChangeListener {
                override fun onScrollChange(
                    v: NestedScrollView,
                    scrollX: Int,
                    scrollY: Int,
                    oldScrollX: Int,
                    oldScrollY: Int
                ) {
                    if (!isLoading) {
                        if (scrollY == (v.getChildAt(0).measuredHeight - v.measuredHeight)) {
                            (ToastHelper::shortToast)("loading 20 more")
                            showLoader()
                            Log.d(TAG, "nextId: $nextId")
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

        articlesRecyclerView = view.findViewById(R.id.articlesRv)
        articlesNestedScrollView = view.findViewById(R.id.articlesNestedScrollView)
        articlesSwipeRefresh = view.findViewById(R.id.articlesSwipeRefresh)

        layoutManager = LinearLayoutManager(this.context)
        adapter = ArticlesAdapter(this)

        articlesRecyclerView.layoutManager = layoutManager
        articlesRecyclerView.adapter = adapter

        return view
    }

    fun showLoader(){
        isLoading = true
        progressBar.visibility = View.VISIBLE
    }

    fun hideLoader(){
        isLoading = false
        progressBar.visibility = View.GONE
    }

    private fun getArticles(nextArticleId: Int?){
        if(nextArticleId == null){
            Log.d(TAG, "getLatestArticles")
            articleService.getLatestArticles(limitPerPage, (SessionManager::getAuthToken)()).enqueue(
                object : Callback<ArticlesResponse> {
                    override fun onResponse(
                        call: Call<ArticlesResponse>,
                        response: Response<ArticlesResponse>
                    ) {
                        if (response.body() != null) {
                            if (adapter.itemCount > 0) {
                                adapter.clearArticles()
                            }
                            nextId = response.body()!!.NextId
                            adapter.addArticles(response.body()!!.Results)
                        } else {
                            Log.d(TAG, response.code().toString())
                        }
                        articlesSwipeRefresh.isRefreshing = false
                        hideLoader()
                    }

                    override fun onFailure(call: Call<ArticlesResponse>, t: Throwable) {
                        Log.d(TAG, "getLatestArticles failed, reason ${t.message.toString()}")
                        hideLoader()
                        articlesSwipeRefresh.isRefreshing = false
                        (ToastHelper::shortToast)("Error: ${t.message.toString()}. Try again.")
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
                    } else {
                        Log.d(TAG, response.code().toString())
                    }
                    hideLoader()
                }

                override fun onFailure(call: Call<ArticlesResponse>, t: Throwable) {
                    Log.d(TAG, "getNextArticles failed, reason ${t.message.toString()}")
                    hideLoader()
                    (ToastHelper::shortToast)("Error: ${t.message.toString()}. Try again.")
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        //do something with your id
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}