package com.example.myloan_app

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DOBActivity : AppCompatActivity() {

    private lateinit var dobPicker: DatePicker
    private lateinit var btnNext: MaterialButton
    private lateinit var btnBack: TextView
    private lateinit var database: DatabaseReference
    private lateinit var sharePref: SharedPref
    private var isClicked = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dobactivity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        dobPicker = findViewById(R.id.DOB_Picker)
        btnNext = findViewById(R.id.btn_nextDOB)
        btnBack = findViewById(R.id.btn_backDOB)

        sharePref = SharedPref(this)
        val userNumber = intent.getStringExtra("userNumber") ?: sharePref.getData("userNumber")
        database = FirebaseDatabase.getInstance().getReference("Users")

        if (userNumber != null) {
            sharePref.saveData("userNumber", userNumber)
        }

        loadData()
        btnNext.setOnClickListener {
            if (!isClicked) {
                isClicked = true
                if (isDateValid()) {
                    saveDate(userNumber.toString())
                } else {
                    isClicked = false
                    Toast.makeText(
                        this,
                        "Please select a valid date (18+ years date)",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadData() {

        val savedDate = sharePref.getData("saveDate")
        if (savedDate != null) {
            val dateParts = savedDate.split("-")
            if (dateParts.size == 3) {
                val day = dateParts[0].toInt()
                val month = dateParts[1].toInt() - 1
                val year = dateParts[2].toInt()

                dobPicker.init(year, month, day, null)
            }
        } else {
            val calendar = Calendar.getInstance()
            dobPicker.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                null
            )
        }
    }

    private fun isDateValid(): Boolean {
        val selectedYear = dobPicker.year
        val selectedMonth = dobPicker.month
        val selectedDay = dobPicker.dayOfMonth
        val today = Calendar.getInstance()
        val eighteenYearsAgo = Calendar.getInstance().apply {
            add(Calendar.YEAR, -18)
        }
        val selectedDate = Calendar.getInstance().apply {
            set(selectedYear, selectedMonth, selectedDay)
        }
        return selectedDate.before(eighteenYearsAgo) && !selectedDate.after(today)
    }

    private fun saveDate(number: String) {
        val day = dobPicker.dayOfMonth
        val month = dobPicker.month + 1
        val year = dobPicker.year

        val selectDate = "$day-$month-$year"
        sharePref.saveData("saveDate", selectDate)

        database.child(number).child("birthDate").setValue(selectDate)
            .addOnSuccessListener {
                val intent = Intent(this, GenderActivity::class.java)
                startActivity(intent)
            }
    }

    override fun onResume() {
        super.onResume()
        isClicked = false

    }
}