package com.example.myloan_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class IncomeActivity : AppCompatActivity() {

    private lateinit var noIncome: MaterialTextView
    private lateinit var lessIncome: MaterialTextView
    private lateinit var middleIncome: MaterialTextView
    private lateinit var aboveIncome: MaterialTextView
    private lateinit var imgRightNo : ImageView
    private lateinit var imgRightLess : ImageView
    private lateinit var imgRightMiddle : ImageView
    private lateinit var imgRightAbove : ImageView
    private lateinit var btnNext: MaterialButton
    private lateinit var btnBack: TextView
    private lateinit var database: DatabaseReference
    private lateinit var sharePref: SharedPref
    private var selectIncome: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_income)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        noIncome = findViewById(R.id.income_No)
        lessIncome = findViewById(R.id.Income_Less)
        middleIncome = findViewById(R.id.Income_middle)
        aboveIncome = findViewById(R.id.Income_above)
        btnNext = findViewById(R.id.btn_nextIncome)
        btnBack = findViewById(R.id.btn_backIncome)

        database = FirebaseDatabase.getInstance().getReference("Users")
        sharePref = SharedPref(this)
        val userNumber = intent.getStringExtra("userNumber") ?: sharePref.getData("userNumber")

        imgRightNo.visibility = View.INVISIBLE
        imgRightLess.visibility = View.INVISIBLE
        imgRightMiddle.visibility = View.INVISIBLE
        imgRightAbove.visibility = View.INVISIBLE

        noIncome.setOnClickListener {
            selectIncome("IncomeNo")
        }
        lessIncome.setOnClickListener {
            selectIncome("Less")
        }
        middleIncome.setOnClickListener {
            selectIncome("Middle")
        }
        aboveIncome.setOnClickListener {
            selectIncome("Above")
        }
        if (userNumber != null) {
            sharePref.saveData("userNumber", userNumber)
        }
        btnNext.setOnClickListener {

            if (selectIncome != null) {
                database.child(userNumber.toString()).child("gender").setValue(selectIncome)
                    .addOnSuccessListener {
                        val intent = Intent(this, ProfessionActivity::class.java)
                        intent.putExtra("userNumber", userNumber)
                        startActivity(intent)
                    }
            } else {
                Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show()
            }
        }
        btnBack.setOnClickListener {
            finish()
        }
    }
    private fun selectIncome(income: String) {
        selectIncome = income
        sharePref.saveData("Income", income)
        resetButtonStyles(noIncome, lessIncome, middleIncome,aboveIncome)
        when (income) {
            "IncomeNo" -> {
                noIncome.setBackgroundResource(R.drawable.bg_all_details)
                noIncome.setTextColor(getColor(R.color.black))
                imgRightNo.visibility = View.VISIBLE
                imgRightLess.visibility = View.INVISIBLE
                imgRightMiddle.visibility = View.INVISIBLE
                imgRightAbove.visibility = View.INVISIBLE
            }
            "Less" -> {
                lessIncome.setBackgroundResource(R.drawable.bg_all_details)
                lessIncome.setTextColor(getColor(R.color.black))
                imgRightLess.visibility = View.VISIBLE
                imgRightMiddle.visibility = View.INVISIBLE
                imgRightNo.visibility = View.INVISIBLE
                imgRightAbove.visibility = View.INVISIBLE
            }
            "Middle" -> {
                middleIncome.setBackgroundResource(R.drawable.bg_all_details)
                middleIncome.setTextColor(getColor(R.color.black))
                imgRightMiddle.visibility = View.VISIBLE
                imgRightLess.visibility = View.INVISIBLE
                imgRightNo.visibility = View.INVISIBLE
                imgRightAbove.visibility = View.INVISIBLE
            }
            "Above" -> {
                aboveIncome.setBackgroundResource(R.drawable.bg_all_details)
                aboveIncome.setTextColor(getColor(R.color.black))
                imgRightAbove.visibility = View.VISIBLE
                imgRightLess.visibility = View.INVISIBLE
                imgRightNo.visibility = View.INVISIBLE
                imgRightAbove.visibility = View.INVISIBLE
            }
        }
    }

    private fun resetButtonStyles(vararg buttons: MaterialTextView) {
        for (button in buttons) {
            button.setBackgroundResource(R.drawable.bg_item)
            button.setTextColor(getColor(R.color.white))
        }
    }
    private fun updateSelectedGenderUI(savedGender: String?) {
        when (savedGender) {
            "IncomeNo" -> selectIncome("IncomeNo")
            "Less" -> selectIncome("Less")
            "Middle" -> selectIncome("Middle")
            "Above" -> selectIncome("Above")
        }
    }

    override fun onResume() {
        super.onResume()
        selectIncome = sharePref.getData("Income")
        updateSelectedGenderUI(selectIncome)
    }
}