package nl.bueno.henry

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import nl.bueno.henry.session.SessionManager
import nl.bueno.henry.ui.fragments.*
import nl.bueno.henry.utils.ToastHelper


class MainActivity : AppCompatActivity() {

    // ui element
    private lateinit var bottomNavigation : BottomNavigationView

    // fragments
    private lateinit var homeFragment : BaseFragment
    private lateinit var likedFragment : BaseFragment
    private lateinit var profileFragment : BaseFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottom_navigation)

        // setup singletons
        (SessionManager::setup)(applicationContext)
        (ToastHelper::setup)(applicationContext)

        homeFragment = HomeFragment()

        // if user is logged in, set each variable to the correct page
        if((SessionManager::isLoggedIn)()){
            profileFragment = ProfileFragment()
            likedFragment = LikedFragment()
            makeCurrentFragment(profileFragment, "PROFILE")
        }else{ // if the user is not logged in, redirect user to the login page on the profile and liked tab
            profileFragment = LoginFragment()
            likedFragment = profileFragment
            makeCurrentFragment(homeFragment, "HOME")
        }
        
        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navHome -> makeCurrentFragment(homeFragment, "HOME")
                R.id.navLiked -> makeCurrentFragment(likedFragment, "LIKED")
                R.id.navProfile -> makeCurrentFragment(profileFragment, "PROFILE")
            }
            true
        }
    }

    // a function to change current fragment in place
    private fun makeCurrentFragment(fragment: Fragment, tag: String) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment, tag)
            commit()
        }

    companion object {
        private const val TAG = "MainActivity"
    }
}