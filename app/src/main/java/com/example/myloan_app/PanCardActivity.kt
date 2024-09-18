package com.example.myloan_app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class PanCardActivity : AppCompatActivity() {

    private lateinit var panCardAmount: TextInputEditText
    private lateinit var panCard: TextInputLayout
    private lateinit var nextButton: MaterialButton
    private lateinit var skipButton: TextView
    private lateinit var database: DatabaseReference
    private lateinit var sharePref: SharedPref
    private var isClicked = false
    lateinit var auth: FirebaseAuth

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
        skipButton = findViewById(R.id.btn_skip)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")
        sharePref = SharedPref(this)

//          val user = auth.currentUser
//        if (user!= null || skipButton.isClickable) {
//            val intent = Intent(this, HomePageActivity::class.java)
//            startActivity(intent)
//        }

        val userNumber = intent.getStringExtra("userNumber") ?: sharePref.getData("userNumber")
        panCard.endIconMode = TextInputLayout.END_ICON_NONE

        panCardAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val panCardNumber = panCardAmount.text.toString()
                if (validatePAN(panCardNumber) && panCardNumber.isNotEmpty()) {
                    panCard.endIconMode = TextInputLayout.END_ICON_CUSTOM // Show the success icon
                } else {
//                    panCardAmount.error = "Enter a valid PAN card number"
                    panCard.endIconMode = TextInputLayout.END_ICON_NONE // Hide the icon
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        if (userNumber != null) {
            sharePref.saveData("userNumber", userNumber)
        }
        nextButton.setOnClickListener {
            if (!isClicked) {
                isClicked = true

                val panCardNumber = panCardAmount.text.toString()

                if (panCardNumber.isEmpty()) {
                    isClicked = false
                    panCardAmount.error = "Enter a valid pan-card Number"
                } else if (validatePAN(panCardNumber)) {
                    sharePref.saveData("panCard", panCardNumber)
                    database.child(userNumber.toString()).child("panCardNumber").setValue(panCardNumber)
                        .addOnSuccessListener {
                            val intent = Intent(this, HomePageActivity::class.java)
                            intent.putExtra("userNumber", userNumber)
                            startActivity(intent)
                        }
                } else {
                    isClicked = false
                    panCardAmount.error = "Enter a valid PAN card number"
                    panCard.endIconMode = TextInputLayout.END_ICON_NONE // Hide the icon
                }
            }
        }
        skipButton.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validatePAN(pan: String): Boolean {
        val panPattern = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$"
        return pan.matches(Regex(panPattern))
    }

    override fun onResume() {
        super.onResume()
        isClicked = false
        val savedNumber =  sharePref.getData("panCard")
        panCardAmount.setText(savedNumber)
    }
}