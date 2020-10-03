package nl.bueno.henry.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home.progressBar
import nl.bueno.henry.Adapter.ArticlesAdapter
import nl.bueno.henry.Common.Common
import nl.bueno.henry.Interface.ArticleService
import nl.bueno.henry.Model.ArticlesResult
import nl.bueno.henry.R
import nl.bueno.henry.R.layout.fragment_home
import nl.bueno.henry.Session.SessionManager
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

    lateinit var adapter: ArticlesAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var articlesRecyclerView: RecyclerView

    private var nextId: Int? = null

    private var isLoading: Boolean = false

    private val limitPerPage : Int = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("is logged in?", (SessionManager::isLoggedIn)().toString())

        getArticles(null)

        articlesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(!isLoading){
                    if (!recyclerView.canScrollVertically(1)) {
                        showLoader()
                        Log.d(TAG, "nextId: $nextId")
                        getArticles(nextId)
                    }else{
                        hideLoader()
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    fun showLoader(){
        isLoading = true
        progressBar.visibility = View.VISIBLE
    }

    fun hideLoader(){
        isLoading = false
        progressBar.visibility = View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view =  inflater.inflate(fragment_home, container, false)

        articlesRecyclerView = view.findViewById(R.id.articlesRv)

        layoutManager = LinearLayoutManager(this.context)
        adapter = ArticlesAdapter(this)

        articlesRecyclerView.layoutManager = layoutManager
        articlesRecyclerView.adapter = adapter

        return view
    }

    private fun getArticles(nextArticleId: Int?){


        if(nextArticleId == null){
            Log.d("ApiResponse", "getArticles")
            articleService.getLatestArticles(limitPerPage, (SessionManager::getAuthToken)()).enqueue(object : Callback<ArticlesResult> {
                override fun onResponse(call: Call<ArticlesResult>, response: Response<ArticlesResult>) {
                    if(response.body() != null){
                        nextId = response.body()!!.NextId
                        adapter.addArticles(response.body()!!.Results)
                        hideLoader()
                    }else{
                        Log.d(TAG, response.code().toString())
                    }
                }
                override fun onFailure(call: Call<ArticlesResult>, t: Throwable) {
                    Log.d(TAG, "The call failed")
                    Log.d(TAG, t.message.toString())
                    if( t.message.toString() == "timeout"){
                        Log.d("ApiResponse", "Trying again....")
                        getArticles(null)
                    }
                    //hideLoader()
                }
            })
        }else{
            Log.d("ApiResponse", "getNextArticles")
            articleService.getNextArticles(nextArticleId, limitPerPage, (SessionManager::getAuthToken)()).enqueue(object :
                Callback<ArticlesResult> {
                override fun onResponse(call: Call<ArticlesResult>, response: Response<ArticlesResult>) {
                    if(response.body() != null){
                        nextId = response.body()!!.NextId
                        adapter.addArticles(response.body()!!.Results)
                        hideLoader()
                    }
                }

                override fun onFailure(call: Call<ArticlesResult>, t: Throwable) {
                    Log.d(TAG, "The call failed")
                    Log.d(TAG, t.message.toString())
                    if( t.message.toString() == "timeout"){
                        Log.d("ApiResponse", "Trying again....")
                        getArticles(null)
                    }
                    //hideLoader()
                }
            })
        }
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}