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
 * Use the [BaseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
open class BaseFragment() : Fragment() {
    companion object {
        private const val TAG = "BaseFragment"
    }
}