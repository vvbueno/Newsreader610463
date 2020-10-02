package nl.bueno.henry.Session

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import nl.bueno.henry.MainActivity
import nl.bueno.henry.Model.User

class SessionManager(private var context: Context) {

    private var preferences: SharedPreferences
    private var editor: SharedPreferences.Editor
    private var PRIVATE_MODE: Int = 0

    init {
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = preferences.edit()
    }

    companion object {
        val PREF_NAME: String = "Newsreader610463"
        val IS_LOGIN: String = "isLoggedIn"
        val KEY_USERNAME: String = "UserName"
    }

    fun createLoginSession(username: String){
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(KEY_USERNAME, username)
        editor.commit()
    }

    fun checkLogin(){
        if(!this.isLoggedIn()){
            val i = Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        }
    }

    fun getUserDetails(): User {
        return User(preferences.getString(KEY_USERNAME, null).toString())
    }

    private fun isLoggedIn(): Boolean {
        return preferences.getBoolean(IS_LOGIN, false)
    }

    fun logout(){
        editor.clear()
        editor.commit()

        val i = Intent(context, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)
    }

}