package nl.bueno.henry

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import nl.bueno.henry.session.SessionManager
import nl.bueno.henry.ui.fragments.HomeFragment
import nl.bueno.henry.ui.fragments.LikedFragment
import nl.bueno.henry.ui.fragments.LoginFragment
import nl.bueno.henry.ui.fragments.ProfileFragment
import nl.bueno.henry.utils.ToastHelper


class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottom_navigation)

        (SessionManager::setup)(applicationContext)
        (ToastHelper::setup)(applicationContext)

        val homeFragment = HomeFragment()
        val likedFragment = LikedFragment()
        val profileFragment : Fragment

        if((SessionManager::isLoggedIn)()){
            profileFragment = ProfileFragment()
            bottomNavigation.menu.findItem(R.id.navLiked).isEnabled = true
            makeCurrentFragment(profileFragment)
        }else{
            profileFragment = LoginFragment()
            bottomNavigation.menu.findItem(R.id.navLiked).isEnabled = false
            makeCurrentFragment(homeFragment)
        }

        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navHome -> makeCurrentFragment(homeFragment)
                R.id.navLiked -> makeCurrentFragment(likedFragment)
                R.id.navProfile -> makeCurrentFragment(profileFragment)
            }
            true
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }

    companion object {
        private const val TAG = "MainActivity"
    }
}