@file:Suppress("DEPRECATION")

package com.myproject.app.linuxlearn.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.myproject.app.linuxlearn.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        auth = Firebase.auth
        initAction()
        getUserData()

        return binding?.root
    }

    private fun initAction() {
        binding?.apply {
            btnSettings.setOnClickListener {
                val intent = Intent(requireContext(), SettingsActivity::class.java)
                startActivity(intent)
//                activity?.finish()
            }
        }
    }

    private fun getUserData() {
        val user = auth.currentUser
        database = FirebaseDatabase.getInstance("https://linux-learn-6bdc2-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("users")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (user?.let { snapshot.hasChild(it.uid) } == true) {
                    Handler().postDelayed({
                        binding?.apply {
                            tvEmail.text = snapshot.child(user.uid).child("email").getValue(String::class.java)
                            tvUsername.text = snapshot.child(user.uid).child("username").getValue(String::class.java)
                            binding?.tvUsernameBox?.text = snapshot.child(user.uid).child("username").getValue(String::class.java)
                            tvEmailBox.text = snapshot.child(user.uid).child("email").getValue(String::class.java)
                            //tvUsername.text = user.displayName
                            //tvUsernameBox.text = user.displayName
                            shimmerDisplayName.visibility = View.GONE
                            shimmerEmail.visibility = View.GONE
                            shimmerUsernameBox.visibility = View.GONE
                            tvUsernameBox.visibility = View.VISIBLE
                            shimmerEmailBox.visibility = View.GONE
                            tvEmailBox.visibility = View.VISIBLE
                        }
                    }, 2000)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}