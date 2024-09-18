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

class LoanAmountActivity : AppCompatActivity() {

    private lateinit var loanAmount: TextInputEditText
    private lateinit var loan: TextInputLayout
    private lateinit var nextButton: MaterialButton
    private lateinit var backButton: TextView
    private lateinit var database: DatabaseReference
    private lateinit var sharePref: SharedPref
    lateinit var auth: FirebaseAuth
    private var isClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loan_amount)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loanAmount = findViewById(R.id.txt_loanAmount)
        loan = findViewById(R.id.txtInputLoan)
        nextButton = findViewById(R.id.btn_next)
        backButton = findViewById(R.id.btn_back)

        database = FirebaseDatabase.getInstance().getReference("Users")
        sharePref = SharedPref(this)
        auth = FirebaseAuth.getInstance()

      /*  val user = auth.currentUser
        if (user != null) {
            val intent = Intent(this, DOBActivity::class.java)
            startActivity(intent)
        }*/

        val userNumber = intent.getStringExtra("userNumber") ?: sharePref.getData("userNumber")

        loanAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (loanAmount.length() == 0) {
                    loan.endIconMode = TextInputLayout.END_ICON_NONE
                } else {
                    loan.endIconMode = TextInputLayout.END_ICON_CUSTOM
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
                val amount = loanAmount.text.toString()
                sharePref.saveData("loanAmount", amount)

                if (amount.isEmpty()) {
                    isClicked = false
                    loanAmount.error = "Enter a  loan amount"
                } else {
                    database.child(userNumber.toString()).child("loanAmount").setValue(amount)
                        .addOnSuccessListener {
                            val intent = Intent(this, DOBActivity::class.java)
                            intent.putExtra("userNumber", userNumber)
                            startActivity(intent)
                        }
                }
            }
        }
        backButton.setOnClickListener {
            finish()
        }
    }
    override fun onResume() {
        super.onResume()
        isClicked =false
        val savedNumber = sharePref.getData("loanAmount")
        loanAmount.setText(savedNumber)
    }
}