package nl.bueno.henry.session

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import nl.bueno.henry.MainActivity

object SessionManager {

    // preferred private mode
    private const val PRIVATE_MODE: Int = 0

    // keys to retrieve variables from session
    private const val PREF_NAME: String = "Newsreader610463"
    private const val IS_LOGIN: String = "isLoggedIn"
    private const val KEY_USERNAME: String = "UserName"
    private const val KEY_XAUTHTOKEN: String = "x-authtoken"

    // application context
    private var context : Context? = null

    private var preferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null


    @SuppressLint("CommitPrefEdits")
    fun setup(context: Context){
        this.context = context
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = preferences!!.edit()
    }

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

    // destroy session
    fun logout(){
        editor!!.clear()
        editor!!.apply()
        reloadMainActivity()
    }

    // reload and redirect to main activity
    private fun reloadMainActivity(){
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent)
    }

}