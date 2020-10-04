package nl.bueno.henry.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import nl.bueno.henry.R
import nl.bueno.henry.session.SessionManager

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment() : BaseFragment() {

    // ui elements
    private lateinit var loggedInUsername : TextView
    private lateinit var logoutButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        // init ui elements
        loggedInUsername = view.findViewById(R.id.loggedInUsername)
        logoutButton = view.findViewById(R.id.logoutButton)

        // get username from session
        loggedInUsername.text = (SessionManager::getUserName)()

        // listener to destroy session
        logoutButton.setOnClickListener {
            (SessionManager::logout)()
        }
    }

    companion object {
        private const val TAG = "ProfileFragment"
    }
}