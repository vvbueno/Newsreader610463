package nl.bueno.henry.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nl.bueno.henry.adapter.ArticlesAdapter
import nl.bueno.henry.common.Common
import nl.bueno.henry.service.ArticleService
import nl.bueno.henry.service.response.ArticlesResponse
import nl.bueno.henry.R
import nl.bueno.henry.R.layout.fragment_liked
import nl.bueno.henry.session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 * Use the [LikedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LikedFragment() : BaseFragment() {

    private val articleService: ArticleService = Common.articleService

    private lateinit var adapter: ArticlesAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var articlesRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    private var nextId: Int? = null
    private var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progressBar)

        showLoader()
        getLikedArticles()
    }

    private fun showLoader(){
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

        val view =  inflater.inflate(fragment_liked, container, false)

        articlesRecyclerView = view.findViewById(R.id.likedArticlesRv)

        layoutManager = LinearLayoutManager(this.context)
        adapter = ArticlesAdapter(this)

        articlesRecyclerView.layoutManager = layoutManager
        articlesRecyclerView.adapter = adapter
        return view
    }

    private fun getLikedArticles(){
            Log.d("ApiResponse", "getLikedArticles")
            articleService.getLikedArticles((SessionManager::getAuthToken)()).enqueue(object :
                Callback<ArticlesResponse> {
                override fun onResponse(call: Call<ArticlesResponse>, response: Response<ArticlesResponse>) {
                    if(response.body() != null){
                        nextId = response.body()!!.NextId
                        adapter.addArticles(response.body()!!.Results)
                        hideLoader()
                    }
                }
                override fun onFailure(call: Call<ArticlesResponse>, t: Throwable) {
                    Log.d(TAG, "The call failed")
                    Log.d(TAG, t.message.toString())
                    if( t.message.toString() == "timeout"){
                        Log.d("ApiResponse", "Trying again....")
                        getLikedArticles()
                    }
                    //hideLoader()
                }
            })
    }

    companion object {
        private const val TAG = "LikedFragment"
    }
}