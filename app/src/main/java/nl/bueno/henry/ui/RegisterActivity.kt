package nl.bueno.henry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.passwordField
import kotlinx.android.synthetic.main.activity_register.usernameField
import nl.bueno.henry.common.Common
import nl.bueno.henry.service.AuthService
import nl.bueno.henry.service.response.LoginResponse
import nl.bueno.henry.service.response.RegisterResponse
import nl.bueno.henry.session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private val authService: AuthService = Common.authService

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerButton.setOnClickListener {
            registerEvent()
        }
    }

    private fun registerEvent(){
        if(usernameField.text.isNotEmpty() && passwordField.text.isNotEmpty()) {

            val username: String = usernameField.text.toString()
            val password: String = passwordField.text.toString()

            authService.register(username, password).enqueue(object :
                Callback<RegisterResponse> {

                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {

                    if(response.code().toString() == "200" && response.body() != null){
                        val responseValue = response.body()
                            if(!responseValue!!.Success){
                                Log.d(TAG, "something prevented register")
                            }else{
                                Log.d(TAG, "register successfull")
                                loginRegisteredUser(username, password)
                            }
                    }else{
                        Log.d(TAG, "something prevented register")
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Log.d(TAG, "The call failed")
                    Log.d(TAG, t.message.toString())
                }
            })
        }else{
            Log.d(TAG, "both fields are required")
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
                        else -> { // Note the block
                            Log.d(TAG, "something else prevented the login")
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.d(TAG, "The call failed")
                    Log.d(TAG, t.message.toString())
                }
            })
    }


    companion object {
        private const val TAG = "RegisterActivity"
    }
}