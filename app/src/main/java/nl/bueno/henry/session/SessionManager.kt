package nl.bueno.henry.Session

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import nl.bueno.henry.Common.Common
import nl.bueno.henry.MainActivity
import nl.bueno.henry.Model.User
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object SessionManager {

    private var context : Context? = null
    private var preferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null


    fun setup(context: Context){
        this.context = context
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = preferences!!.edit()
    }

    private const val PRIVATE_MODE: Int = 0
    const val PREF_NAME: String = "Newsreader610463"
    private const val IS_LOGIN: String = "isLoggedIn"
    const val KEY_USERNAME: String = "UserName"
    const val KEY_XAUTHTOKEN: String = "x-authtoken"


    @SuppressLint("CommitPrefEdits")
    fun createLoginSession(username: String, xauthtoken: String){
        editor!!.putBoolean(IS_LOGIN, true)
        editor!!.putString(KEY_USERNAME, username)
        editor!!.putString(KEY_XAUTHTOKEN, xauthtoken)
        editor!!.commit()
        reloadMainActivity()
    }


    fun getUserName(): String? {
        return preferences!!.getString(KEY_USERNAME, null)
    }

    fun getAuthToken(): String? {
        return preferences!!.getString(KEY_XAUTHTOKEN, null)
    }

    fun isLoggedIn(): Boolean {
        return preferences!!.getBoolean(IS_LOGIN, false)
    }

    @SuppressLint("CommitPrefEdits")
    fun logout(){
        editor!!.clear()
        editor!!.apply()
        reloadMainActivity()
    }

    private fun reloadMainActivity(){
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent)
    }

}