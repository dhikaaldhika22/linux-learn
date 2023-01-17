@file:Suppress("DEPRECATION")

package com.myproject.app.linuxlearn.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.myproject.app.linuxlearn.R
import com.myproject.app.linuxlearn.adapter.ExerciseAdapter
import com.myproject.app.linuxlearn.data.model.ExerciseModel
import com.myproject.app.linuxlearn.databinding.ActivityDetailExercisesBinding


class DetailExercisesActivity : AppCompatActivity() {
    private var _binding: ActivityDetailExercisesBinding? = null
    private val binding get() = _binding
    private lateinit var database: DatabaseReference
    private lateinit var exerciseArrayList : ArrayList<ExerciseModel>

    private var selectedOption = 0
    private var currentQuestionPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailExercisesBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setToolbar(getString(R.string.exercise))
        getDetailExercise()
        multipleButtonAction()

        binding?.tvNext?.setOnClickListener {
            if (selectedOption != 0) {
                resetOptions()
                var exercise = exerciseArrayList[currentQuestionPosition]
                binding?.apply {
                    tvName.text = exercise.name
                    tvQuestion.text = exercise.question
                    tvOption1.text = exercise.optionA
                    tvOption2.text = exercise.optionB
                    tvOption3.text = exercise.optionC
                    tvOption4.text = exercise.optionD
                }
                currentQuestionPosition++
                binding?.tvCurrentQuestion?.text = "Pertanyaan $currentQuestionPosition"
            } else {
                Toast.makeText(this@DetailExercisesActivity, "Pilih jawaban terlebih dahulu!", Toast.LENGTH_SHORT).show()
            }
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

    private fun getDetailExercise() {
        database = FirebaseDatabase.getInstance("https://linux-learn-6bdc2-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("exercises")
        exerciseArrayList = arrayListOf()

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Handler().postDelayed({
                        val exerciseId = intent.extras?.getString("id")
                        for (exerciseSnapshot in snapshot.child(exerciseId.toString()).child("questions").children) {
                            val exercise = exerciseSnapshot.getValue(ExerciseModel::class.java)

                            if(exerciseSnapshot.key == "1") {
                                binding?.apply {
                                    tvName.text = exercise?.name
                                    tvQuestion.text = exercise?.question
                                    tvOption1.text = exercise?.optionA
                                    tvOption2.text = exercise?.optionB
                                    tvOption3.text = exercise?.optionC
                                    tvOption4.text = exercise?.optionD
                                }
                            }
                            exerciseArrayList.add(exercise!!)
                            ExerciseAdapter(applicationContext, exerciseArrayList)
                            binding?.tvTotalQuestion?.text = "/" + exerciseArrayList.size.toString()
                        }
                    }, 2000)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailExercisesActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun multipleButtonAction() {
        binding?.apply {
            rlOption1.setOnClickListener {
                selectedOption = 1
                multipleSelectedOptions(rlOption1, ivSelect1)
            }

            rlOption2.setOnClickListener {
                selectedOption = 2
                multipleSelectedOptions(rlOption2, ivSelect2)
            }

            rlOption3.setOnClickListener {
                selectedOption = 3
                multipleSelectedOptions(rlOption3, ivSelect3)
            }

            rlOption4.setOnClickListener {
                selectedOption = 4
                multipleSelectedOptions(rlOption4, ivSelect4)
            }
        }
    }

    private fun multipleSelectedOptions(selectedOption: RelativeLayout, selectedOptionIcon: ImageView) {
        resetOptions()

        selectedOptionIcon.setImageResource(R.drawable.ic_check)
        selectedOption.setBackgroundResource(R.drawable.bg_selected_option)
    }

    private fun resetOptions() {
        binding?.apply {
            rlOption1.setBackgroundResource(R.drawable.round_back_white)
            rlOption2.setBackgroundResource(R.drawable.round_back_white)
            rlOption3.setBackgroundResource(R.drawable.round_back_white)
            rlOption4.setBackgroundResource(R.drawable.round_back_white)


            ivSelect1.setImageResource(R.drawable.round_back_white)
            ivSelect2.setImageResource(R.drawable.round_back_white)
            ivSelect3.setImageResource(R.drawable.round_back_white)
            ivSelect4.setImageResource(R.drawable.round_back_white)
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