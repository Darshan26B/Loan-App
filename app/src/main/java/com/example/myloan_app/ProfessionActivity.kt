package com.example.myloan_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfessionActivity : AppCompatActivity() {

    private lateinit var salary: MaterialTextView
    private lateinit var employed: MaterialTextView
    private lateinit var selfEmploy: MaterialTextView
    private lateinit var subSalary: MaterialTextView
    private lateinit var subEmployed: MaterialTextView
    private lateinit var subSelfEmploy: MaterialTextView
    private lateinit var student: MaterialTextView
    private lateinit var subStudent: MaterialTextView
    private lateinit var rSalary: RelativeLayout
    private lateinit var rEmployed: RelativeLayout
    private lateinit var rSelfEmploy: RelativeLayout
    private lateinit var rStudent: RelativeLayout
    private lateinit var rightSalary: ImageView
    private lateinit var rightEmployed: ImageView
    private lateinit var rightSelfEmploy: ImageView
    private lateinit var rightStudent: ImageView
    private lateinit var btnNext: MaterialButton
    private lateinit var btnBack: MaterialTextView
    private lateinit var database: DatabaseReference
    private lateinit var sharePref: SharedPref
    private var selectedProfession: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profession)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        salary = findViewById(R.id.p_salaried)
        employed = findViewById(R.id.p_employed)
        selfEmploy = findViewById(R.id.p_SelfEmploy)
        student = findViewById(R.id.p_Student)
        subSalary = findViewById(R.id.p_sub_salaried)
        subEmployed = findViewById(R.id.p_sub_employed)
        subSelfEmploy = findViewById(R.id.p_sub_selfEmployed)
        subStudent = findViewById(R.id.p_sub_student)
        rSalary = findViewById(R.id.relativeLayoutSalary)
        rEmployed = findViewById(R.id.relativeLayoutEmployed)
        rSelfEmploy = findViewById(R.id.relativeLayoutSelfEmploy)
        rStudent = findViewById(R.id.relativeLayoutStudent)
        rightSalary = findViewById(R.id.P_RightSalaried)
        rightEmployed = findViewById(R.id.p_RightEmploy)
        rightSelfEmploy = findViewById(R.id.p_RightSelfEmploy)
        rightStudent = findViewById(R.id.p_RightStudent)
        btnNext = findViewById(R.id.btn_nextP)
        btnBack = findViewById(R.id.btn_backP)

//        -----------  this code is data store in firebase and user number store in sharePref -----------
        database = FirebaseDatabase.getInstance().getReference("Users")
        sharePref = SharedPref(this)
        val userNumber = intent.getStringExtra("userNumber") ?: sharePref.getData("userNumber")

        rightSalary.visibility = View.INVISIBLE
        rightEmployed.visibility = View.INVISIBLE
        rightSelfEmploy.visibility = View.INVISIBLE
        rightStudent.visibility = View.INVISIBLE

        rSalary.setOnClickListener {
            selectProfession("salary")
         }
        rEmployed.setOnClickListener {
            selectProfession("employed")
        }
        rSelfEmploy.setOnClickListener {
            selectProfession("selfEmploy")
        }
        rStudent.setOnClickListener {
            selectProfession("student")
        }
        if (userNumber != null) {
            sharePref.saveData("userNumber", userNumber)
        }
        btnNext.setOnClickListener {

            if (selectedProfession != null) {
                database.child(userNumber.toString()).child("profession").setValue(selectedProfession)
                    .addOnSuccessListener {
                        val intent = Intent(this, IncomeActivity::class.java)
                        intent.putExtra("userNumber", userNumber)
                        startActivity(intent)
                    }
            } else {
                Toast.makeText(this, "Please select your Profession", Toast.LENGTH_SHORT).show()
            }
        }
        btnBack.setOnClickListener {
            finish()
        }
    }
    private fun selectProfession(profession: String) {
        selectedProfession = profession
        sharePref.saveData("profession", profession)
        resetButtonStyles(salary,subSalary,employed,subEmployed,selfEmploy,subSelfEmploy,student,subStudent)
        when (profession) {
            "salary" -> {
                rSalary.setBackgroundResource(R.drawable.bg_all_details)
                salary.setTextColor(getColor(R.color.black))
                subSalary.setTextColor(getColor(R.color.black))
                rightSalary.visibility = View.VISIBLE
                rightEmployed.visibility = View.INVISIBLE
                rightSelfEmploy.visibility = View.INVISIBLE
                rightStudent.visibility = View.INVISIBLE
            }

            "employed" -> {
                rEmployed.setBackgroundResource(R.drawable.bg_all_details)
                employed.setTextColor(getColor(R.color.black))
                subEmployed.setTextColor(getColor(R.color.black))
                rightEmployed.visibility = View.VISIBLE
                rightSalary.visibility = View.INVISIBLE
                rightSelfEmploy.visibility = View.INVISIBLE
                rightStudent.visibility = View.INVISIBLE

            }

            "selfEmploy" -> {
                rSelfEmploy.setBackgroundResource(R.drawable.bg_all_details)
                selfEmploy.setTextColor(getColor(R.color.black))
                subSelfEmploy.setTextColor(getColor(R.color.black))
                rightSelfEmploy.visibility = View.VISIBLE
                rightSalary.visibility = View.INVISIBLE
                rightEmployed.visibility = View.INVISIBLE
                rightStudent.visibility = View.INVISIBLE
            }
            "student" -> {
                rStudent.setBackgroundResource(R.drawable.bg_all_details)
                student.setTextColor(getColor(R.color.black))
                subStudent.setTextColor(getColor(R.color.black))
                rightStudent.visibility = View.VISIBLE
                rightSalary.visibility = View.INVISIBLE
                rightEmployed.visibility = View.INVISIBLE
                rightSelfEmploy.visibility = View.INVISIBLE
            }
        }
    }

    private fun resetButtonStyles(vararg buttons: MaterialTextView) {
        for (button in buttons) {
            button.setBackgroundResource(R.drawable.bg_item)
            button.setTextColor(getColor(R.color.white))
        }
    }

    private fun updateSelectedProfessionUI(savedGender: String?) {
        when (savedGender) {
            "salary" -> selectProfession("salary")
            "employed" -> selectProfession("employed")
            "selfEmploy" -> selectProfession("selfEmploy")
            "student" -> selectProfession("student")
        }
    }

    override fun onResume() {
        super.onResume()
        selectedProfession = sharePref.getData("profession")
        updateSelectedProfessionUI(selectedProfession)
    }
}