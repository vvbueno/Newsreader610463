package nl.bueno.henry.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import nl.bueno.henry.Common.Common
import nl.bueno.henry.Interface.ArticleService
import nl.bueno.henry.Interface.AuthService
import nl.bueno.henry.Model.ArticlesResult
import nl.bueno.henry.R
import nl.bueno.henry.Session.AuthToken
import nl.bueno.henry.Session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment() : BaseFragment() {

    private val authService: AuthService = Common.authService

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        loginButton.setOnClickListener {
            loginEvent()
        }
    }

    fun loginEvent(){
        if(usernameField.text.isNotEmpty() && passwordField.text.isNotEmpty()) {

            val username: String = usernameField.text.toString()
            val password: String = passwordField.text.toString()

            authService.login(username, password).enqueue(object :
                Callback<AuthToken> {
                override fun onResponse(call: Call<AuthToken>, response: Response<AuthToken>) {
                    when (response.code().toString()) {
                        "200" -> response.body()?.AuthToken?.let { successLogin(username, it) }
                        "401" -> unauthorizedLogin()
                        "400" ->badRequestLogin()
                        else -> { // Note the block
                            elseLogin()
                        }
                    }
                }

                override fun onFailure(call: Call<AuthToken>, t: Throwable) {
                    Log.d(TAG, "The call failed")
                    Log.d(TAG, t.message.toString())
                }
            })
        }else{
            Log.d(TAG, "both fields are required")
        }
    }

    fun successLogin(username: String, authToken: String){
        Log.d(TAG, "Login successful")
        (SessionManager::createLoginSession)(username, authToken)
    }

    fun unauthorizedLogin(){
        Log.d(TAG, "Login unauthorized")
        (SessionManager::logout)()
    }

    fun badRequestLogin(){
        Log.d(TAG, "Login bad request")
    }

    fun elseLogin(){
        Log.d(TAG, "Login something else happened")
    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}