package nl.bueno.henry

import android.content.ClipData
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import nl.bueno.henry.Session.SessionManager
import nl.bueno.henry.fragments.HomeFragment
import nl.bueno.henry.fragments.LikedFragment
import nl.bueno.henry.fragments.LoginFragment
import nl.bueno.henry.fragments.ProfileFragment


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (SessionManager::setup)(applicationContext)

        val homeFragment = HomeFragment()
        val likedFragment = LikedFragment()

        val profileFragment : Fragment

        if((SessionManager::isLoggedIn)()){
            profileFragment = ProfileFragment()
            bottom_navigation.menu.findItem(R.id.navLiked).isEnabled = true
            makeCurrentFragment(profileFragment)
        }else{
            profileFragment = LoginFragment()
            bottom_navigation.menu.findItem(R.id.navLiked).isEnabled = false
            makeCurrentFragment(homeFragment)
        }

        bottom_navigation.setOnNavigationItemSelectedListener {
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