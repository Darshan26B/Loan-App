package com.example.myloan_app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PanCardActivity : AppCompatActivity() {

    private lateinit var panCardAmount: TextInputEditText
    private lateinit var panCard: TextInputLayout
    private lateinit var nextButton: MaterialButton
    private lateinit var backButton: TextView
    private lateinit var database: DatabaseReference
    private lateinit var sharePref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pan_card)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        panCardAmount = findViewById(R.id.txt_panCardNumber)
        panCard = findViewById(R.id.txtInputPanCard)
        nextButton = findViewById(R.id.btn_next)
        backButton = findViewById(R.id.btn_back)

        database = FirebaseDatabase.getInstance().getReference("Users")
        sharePref = SharedPref(this)
        val userNumber = intent.getStringExtra("userNumber") ?: sharePref.getData("userNumber")

        panCardAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pan = panCardAmount.text.toString().trim()
                if (pan.length == 10 && validatePAN(pan)) {
                    panCard.endIconMode = TextInputLayout.END_ICON_CUSTOM
                } else {
                    panCardAmount.error = "Enter a valid pan-card Number"
                    panCard.endIconMode = TextInputLayout.END_ICON_NONE
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        if (userNumber != null) {
            sharePref.saveData("userNumber", userNumber)
        }
        nextButton.setOnClickListener {
            val number = panCardAmount.text.toString()
            sharePref.saveData("panCardNum", number)

            if (number.isEmpty()) {
                panCardAmount.error = "Enter a pan-card Number"
            } else {
                database.child(userNumber.toString()).child("panCardNum").setValue(number)
                    .addOnSuccessListener {
                        val intent = Intent(this, DOBActivity::class.java)
                        intent.putExtra("userNumber", userNumber)
                        startActivity(intent)
                    }
            }
        }

        backButton.setOnClickListener {
            finish()
        }
    }
    private fun validatePAN(pan: String): Boolean {
        // PAN regex pattern
        val panPattern = "[A-Z]{5}[0-9]{4}[A-Z]{1}"
        return pan.matches(Regex(panPattern))
    }
}