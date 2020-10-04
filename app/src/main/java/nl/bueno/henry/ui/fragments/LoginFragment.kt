package nl.bueno.henry.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import nl.bueno.henry.common.Common
import nl.bueno.henry.service.AuthService
import nl.bueno.henry.R
import nl.bueno.henry.service.response.LoginResponse
import nl.bueno.henry.ui.RegisterActivity
import nl.bueno.henry.session.SessionManager
import nl.bueno.henry.utils.ToastHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A class to store the fragment that contains the login screen
 */
class LoginFragment() : BaseFragment() {

    // service to connect to the api
    private val authService: AuthService = Common.authService

    // ui elements
    private lateinit var usernameField : EditText
    private lateinit var passwordField : EditText
    private lateinit var loginButton : Button
    private lateinit var signUpLabel : TextView
    private lateinit var errorLabel : TextView

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

        // init ui elements
        usernameField = view.findViewById(R.id.usernameField)
        passwordField = view.findViewById(R.id.passwordField)

        loginButton = view.findViewById(R.id.loginButton)

        signUpLabel = view.findViewById(R.id.signUpLabel)
        errorLabel = view.findViewById(R.id.errorLabel)

        loginButton.setOnClickListener {
            loginEvent()
        }

        // redirect to register activity
        signUpLabel.setOnClickListener{
            val intent = Intent(this.context, RegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.context?.startActivity(intent)
        }
    }

    fun loginEvent(){

        // verify both username and password are present
        if(usernameField.text.isNotEmpty() && passwordField.text.isNotEmpty()) {

            val username: String = usernameField.text.toString()
            val password: String = passwordField.text.toString()

            authService.login(username, password).enqueue(object :
                Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    when (response.code().toString()) {
                        "200" -> response.body()?.AuthToken?.let { successLogin(username, it) }
                        "401" -> unauthorizedLogin()
                        "400" ->badRequestLogin()
                        else -> { // Note the block
                            elseLogin()
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.d(TAG, t.message.toString())
                    showError("Error: ${t.message.toString()}.")
                }
            })
        }else{
            showError(getString(R.string.fields_required))
        }
    }

    fun successLogin(username: String, authToken: String){
        Log.d(TAG, "Login successful")
        (SessionManager::createLoginSession)(username, authToken)
    }

    fun unauthorizedLogin(){
        Log.d(TAG, "Login unauthorized")
        (ToastHelper::shortToast)(getString(R.string.fields_mismatch))
        showError(getString(R.string.fields_mismatch))
    }

    fun badRequestLogin(){
        Log.d(TAG, "Login bad request")
        (ToastHelper::shortToast)(getString(R.string.fields_required))
        showError(getString(R.string.fields_required))
    }

    fun elseLogin(){
        Log.d(TAG, "Login something else happened")
        (ToastHelper::shortToast)(getString(R.string.unexpected_error))
        showError(getString(R.string.unexpected_error))
    }

    private fun showError(message: String){
        errorLabel.visibility = View.VISIBLE
        errorLabel.text = message
    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}