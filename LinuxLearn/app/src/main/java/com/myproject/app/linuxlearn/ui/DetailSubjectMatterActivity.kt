@file:Suppress("DEPRECATION")

package com.myproject.app.linuxlearn.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.myproject.app.linuxlearn.R
import com.myproject.app.linuxlearn.adapter.SubjectMatterAdapter
import com.myproject.app.linuxlearn.data.model.SubjectMatterModel
import com.myproject.app.linuxlearn.databinding.ActivityDetailSubjectMatterBinding

class DetailSubjectMatterActivity : AppCompatActivity() {
    private var _binding: ActivityDetailSubjectMatterBinding? = null
    private val binding get() = _binding
    private lateinit var database: DatabaseReference
    private lateinit var subjectMatterArrayList : ArrayList<SubjectMatterModel>
    private var currentSubjectMatterPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailSubjectMatterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setToolbar(getString(R.string.subject_matter))
        getDetailSubjectMatter()
        getDetailInfo()
        setIndicator()

        binding?.tvNext?.setOnClickListener {
            val subjectMatter = subjectMatterArrayList[currentSubjectMatterPosition]
            binding?.tvContent?.text = subjectMatter.section
            currentSubjectMatterPosition++
        }

        binding?.tvBack?.setOnClickListener {
            val subjectMatter = subjectMatterArrayList[currentSubjectMatterPosition]
            binding?.tvContent?.text = subjectMatter.section
            currentSubjectMatterPosition--
        }
    }

    private fun setToolbar(title: String) {
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            this.title = title
        }
    }

    private fun getDetailInfo() {
        val subjectMatterIntent = intent
        val subjectMatterName = subjectMatterIntent.getStringExtra("name")
        val subjectMatterLabel = subjectMatterIntent.getStringExtra("label")

        binding?.apply {
            tvName.text = subjectMatterName
            tvLabel1.text = subjectMatterLabel
        }
    }

    private fun getDetailSubjectMatter() {
        database = FirebaseDatabase.getInstance("https://linux-learn-6bdc2-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("subjectmatter")
        subjectMatterArrayList = arrayListOf()

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Handler().postDelayed({
                        val subjectMatterId = intent.extras?.getString("id")
                        for (subjectMatterSnapshot in snapshot.child(subjectMatterId.toString()).child("content").children) {
                            val subjectMatter =
                                subjectMatterSnapshot.getValue(SubjectMatterModel::class.java)
                            if (subjectMatterSnapshot.key == "1") {
                                binding?.apply {
                                    tvContent.text = subjectMatter?.section
                                }
                            }
                            subjectMatterArrayList.add(subjectMatter!!)
                            SubjectMatterAdapter(application, subjectMatterArrayList)
                        }
                    }, 2000)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailSubjectMatterActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setIndicator() {
        when(currentSubjectMatterPosition) {
            0 -> {
                binding?.apply {
                    nav1.setBackgroundResource(R.drawable.bg_nav_active)
                    nav2.setBackgroundResource(R.drawable.bg_nav_nonactive)
                    nav3.setBackgroundResource(R.drawable.bg_nav_nonactive)
                    tvBack.visibility = View.GONE
                }
            }
            1 -> {
                binding?.apply {
                    nav1.setBackgroundResource(R.drawable.bg_nav_active)
                    nav2.setBackgroundResource(R.drawable.bg_nav_active)
                    nav3.setBackgroundResource(R.drawable.bg_nav_nonactive)
                    tvBack.visibility = View.VISIBLE
                }
            }
            2 -> {
                binding?.apply {
                    nav1.setBackgroundResource(R.drawable.bg_nav_active)
                    nav2.setBackgroundResource(R.drawable.bg_nav_active)
                    nav3.setBackgroundResource(R.drawable.bg_nav_active)
                    tvBack.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return super.onSupportNavigateUp()
    }
}