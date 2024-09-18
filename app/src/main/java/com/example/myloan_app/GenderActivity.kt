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

class GenderActivity : AppCompatActivity() {

    private lateinit var male: MaterialTextView
    private lateinit var feMale: MaterialTextView
    private lateinit var other: MaterialTextView
    private lateinit var rightMale: ImageView
    private lateinit var rightFeMale: ImageView
    private lateinit var rightOther: ImageView
    private lateinit var next: MaterialButton
    private lateinit var back: TextView
    private lateinit var database: DatabaseReference
    private lateinit var sharePref: SharedPref
    private var isClicked = false
    var selectedGender: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gender)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        male = findViewById(R.id.gender_male)
        feMale = findViewById(R.id.gender_feMale)
        other = findViewById(R.id.gender_other)
        rightMale = findViewById(R.id.right_male)
        rightFeMale = findViewById(R.id.right_feMale)
        rightOther = findViewById(R.id.right_other)
        next = findViewById(R.id.btn_nextGender)
        back = findViewById(R.id.btn_backGender)

        database = FirebaseDatabase.getInstance().getReference("Users")
        sharePref = SharedPref(this)
        val userNumber = intent.getStringExtra("userNumber") ?: sharePref.getData("userNumber")

        rightMale.visibility = View.INVISIBLE
        rightFeMale.visibility = View.INVISIBLE
        rightOther.visibility = View.INVISIBLE

        male.setOnClickListener {
            selectGender("Male")
        }
        feMale.setOnClickListener {
            selectGender("Female")
        }
        other.setOnClickListener {
            selectGender("Other")
        }
        if (userNumber != null) {
            sharePref.saveData("userNumber", userNumber)
        }
        next.setOnClickListener {
            if (!isClicked) {
                isClicked = true
                if (selectedGender != null) {
                    database.child(userNumber.toString()).child("gender").setValue(selectedGender)
                        .addOnSuccessListener {
                            val intent = Intent(this, ProfessionActivity::class.java)
                            intent.putExtra("userNumber", userNumber)
                            startActivity(intent)

                        }
                } else {
                    isClicked = false
                    Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show()
                }
            }
        }
        back.setOnClickListener {
            finish()
        }
    }

    private fun selectGender(gender: String) {
        selectedGender = gender
        sharePref.saveData("gender", gender)
        resetButtonStyles(male, feMale, other)
        when (gender) {
            "Male" -> {
                male.setBackgroundResource(R.drawable.bg_all_details)
                male.setTextColor(getColor(R.color.black))
                rightMale.visibility = View.VISIBLE
                rightFeMale.visibility = View.INVISIBLE
                rightOther.visibility = View.INVISIBLE
            }

            "Female" -> {
                feMale.setBackgroundResource(R.drawable.bg_all_details)
                feMale.setTextColor(getColor(R.color.black))
                rightFeMale.visibility = View.VISIBLE
                rightMale.visibility = View.INVISIBLE
                rightOther.visibility = View.INVISIBLE

            }

            "Other" -> {
                other.setBackgroundResource(R.drawable.bg_all_details)
                other.setTextColor(getColor(R.color.black))
                rightOther.visibility = View.VISIBLE
                rightFeMale.visibility = View.INVISIBLE
                rightMale.visibility = View.INVISIBLE
            }
        }
    }

    private fun resetButtonStyles(vararg buttons: MaterialTextView) {
        for (button in buttons) {
            button.setBackgroundResource(R.drawable.bg_item)
            button.setTextColor(getColor(R.color.blue))
        }
    }

    private fun updateSelectedGenderUI(savedGender: String?) {
        when (savedGender) {
            "Male" -> selectGender("Male")
            "Female" -> selectGender("Female")
            "Other" -> selectGender("Other")
        }
    }

    override fun onResume() {
        super.onResume()
        isClicked = false
        selectedGender = sharePref.getData("gender")
        updateSelectedGenderUI(selectedGender)
    }
}