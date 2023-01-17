package com.myproject.app.linuxlearn.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.myproject.app.linuxlearn.R
import com.myproject.app.linuxlearn.adapter.SubjectMatterAdapter
import com.myproject.app.linuxlearn.data.model.SubjectMatterModel
import com.myproject.app.linuxlearn.databinding.ActivitySeeAllSubjectMatterBinding

class SeeAllSubjectMatterActivity : AppCompatActivity() {
    private var _binding: ActivitySeeAllSubjectMatterBinding? = null
    private val binding get() =_binding
    private lateinit var database: DatabaseReference
    private lateinit var subjectMatterArrayList : ArrayList<SubjectMatterModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySeeAllSubjectMatterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setToolbar(getString(R.string.subject_matter))
        getSubjectMatter()
    }

    private fun setToolbar(title: String) {
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            this.title = title
        }
    }

    private fun getSubjectMatter() {
        database = FirebaseDatabase.getInstance("https://linux-learn-6bdc2-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("subjectmatter")
        subjectMatterArrayList = arrayListOf()
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Handler().postDelayed({
                        for (subjectMatterSnapshot in snapshot.children) {
                            val subjectMatter =
                                subjectMatterSnapshot.getValue(SubjectMatterModel::class.java)
                            subjectMatterArrayList.add(subjectMatter!!)
                            binding?.progressbar?.visibility = View.GONE
                        }

                        binding?.rvSubjectMatter?.apply {
                            layoutManager = LinearLayoutManager(this@SeeAllSubjectMatterActivity, LinearLayoutManager.VERTICAL, false)
                            adapter = SubjectMatterAdapter(context, subjectMatterArrayList)
                            setHasFixedSize(true)
                        }
                    }, 2000)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}