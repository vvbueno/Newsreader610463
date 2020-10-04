package nl.bueno.henry.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import nl.bueno.henry.R
import nl.bueno.henry.common.Common
import nl.bueno.henry.service.AuthService
import nl.bueno.henry.service.response.LoginResponse
import nl.bueno.henry.service.response.RegisterResponse
import nl.bueno.henry.session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    // service to connect to the api
    private val authService: AuthService = Common.authService

    // ui elements
    private lateinit var usernameField : EditText
    private lateinit var passwordField : EditText
    private lateinit var registerButton: Button
    private lateinit var errorLabel : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // init ui variables
        usernameField = findViewById(R.id.usernameField)
        passwordField = findViewById(R.id.passwordField)
        registerButton = findViewById(R.id.registerButton)
        errorLabel = findViewById(R.id.errorLabel)

        registerButton.setOnClickListener {
            registerEvent()
        }
    }

    private fun registerEvent(){

        // check that both fields are not empty
        if(usernameField.text.isNotEmpty() && passwordField.text.isNotEmpty()) {

            val username: String = usernameField.text.toString()
            val password: String = passwordField.text.toString()

            authService.register(username, password).enqueue(object :
                Callback<RegisterResponse> {

                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    // this call only returns 200
                    if(response.code().toString() == "200" && response.body() != null){
                        val responseValue = response.body()
                            // this property must be true in order to confirm we sucessfully created the user
                            if(!responseValue!!.Success){
                                showError(getString(R.string.error_prevented_register))
                            }else{
                                // if they are successfully registered, then log them into the app
                                loginRegisteredUser(username, password)
                            }
                    }else{
                        showError(getString(R.string.error_prevented_register))
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Log.d(TAG, t.message.toString())
                    showError("Error ${t.message.toString()}")
                }
            })
        }else{
            showError(getString(R.string.fields_required))
        }
    }

    private fun loginRegisteredUser(username: String, password: String){

            authService.login(username, password).enqueue(object :
                Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    when (response.code().toString()) {
                        "200" -> response.body()?.AuthToken?.let {
                            Log.d(TAG, "Login successful")
                            (SessionManager::createLoginSession)(username, it)
                        }
                        else -> {
                            (SessionManager::logout)()
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.d(TAG, t.message.toString())
                    (SessionManager::logout)() // on error destroy the session so user can log in manually
                }
            })
    }

    // to go back to the previous activity emulating the back button on android
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showError(message: String){
        errorLabel.visibility = View.VISIBLE
        errorLabel.text = message
    }

    companion object {
        private const val TAG = "RegisterActivity"
    }
}