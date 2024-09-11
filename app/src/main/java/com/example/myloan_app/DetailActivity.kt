package com.example.myloan_app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
 import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.otpview.OTPTextView
import java.util.concurrent.TimeUnit

class DetailActivity : AppCompatActivity() {

    private lateinit var userName: MaterialTextView
    private lateinit var userLastName: MaterialTextView
    private lateinit var userEmailId: MaterialTextView
    private lateinit var userNumber: TextInputEditText
    private lateinit var userPinCode: TextInputEditText
    private lateinit var txtInputNumber: TextInputLayout
    private lateinit var txtInputPinCode: TextInputLayout
    private lateinit var userSendOtp: TextView
    private lateinit var userVerifyOtp: MaterialButton
    private lateinit var verificationId: String
    private lateinit var sharePref: SharedPref
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var userOTP: OTPTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userName = findViewById(R.id.Detail_Name)
        userLastName = findViewById(R.id.Detail_LastName)
        userEmailId = findViewById(R.id.Detail_EmailID)
        userNumber = findViewById(R.id.Detail_Number)
        userPinCode = findViewById(R.id.Detail_PinCode)
        userSendOtp = findViewById(R.id.Detail_sendOTP)
        userVerifyOtp = findViewById(R.id.Details_Verify)
        userOTP = findViewById(R.id.Detail_otpView)
        txtInputPinCode = findViewById(R.id.txt_InputPincode)
        txtInputNumber = findViewById(R.id.txt_InputNumber)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")
        sharePref = SharedPref(this)

        txtInputPinCode.endIconMode = TextInputLayout.END_ICON_NONE
        txtInputNumber.endIconMode = TextInputLayout.END_ICON_NONE

        userIconSet(userPinCode, txtInputPinCode, 6)
        userIconSet(userNumber, txtInputNumber, 10)

        userSendOtp.visibility = View.VISIBLE
        userOTP.visibility = View.GONE

       /* val user = auth.currentUser
        if (user != null) {
//            userSendOtp.visibility = View.GONE
//            userOTP.visibility = View.GONE
            userVerifyOtp.setText("Next")
            val intent = Intent(this, LoanAmountActivity::class.java)
            startActivity(intent)
        }*/

        val name = sharePref.getData("Name")
        val lastName = sharePref.getData("LastName")
        val email = sharePref.getData("Email")

        userName.text = name
        userLastName.text = lastName
        userEmailId.text = email


        userSendOtp.setOnClickListener {
            val phoneNumber = "+91" + userNumber.text.toString()

            when {
                phoneNumber.length != 13 -> {
                    userNumber.error = "Enter 10 digit  phone number"
                }

                userPinCode.text.toString().isEmpty() -> {
                    userPinCode.error = "Enter a valid pin-code"
                }

                userPinCode.length() != 6 -> {
                    userPinCode.error = "Enter a valid pin-code"
                }

                else -> {
                    userOTP.visibility = View.VISIBLE
                    phoneAuth(phoneNumber)
                }
            }

        }
    }

    private fun userIconSet(
        user: TextInputEditText,
        txtInput: TextInputLayout,
        numberLength: Number,
    ) {
        user.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (user.length() == numberLength) {
                    txtInput.endIconMode = TextInputLayout.END_ICON_CUSTOM
                } else {
                    txtInput.endIconMode = TextInputLayout.END_ICON_NONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    override fun onResume() {
        super.onResume()

        // Retrieve data
        val savedNumber = sharePref.getData("UserNumber")
        val savedPinCode = sharePref.getData("UserPinCode")

        // Update UI with saved data
        userNumber.setText(savedNumber)
        userPinCode.setText(savedPinCode)
    }

    private fun phoneAuth(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    when (e) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            Toast.makeText(
                                this@DetailActivity,
                                "Invalid phone number format.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        is FirebaseTooManyRequestsException -> {
                            Toast.makeText(
                                this@DetailActivity,
                                "Quota exceeded, try again later.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> {
                            Toast.makeText(
                                this@DetailActivity,
                                "Verification failed: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    Log.e("OTPVerifyActivity", "onVerificationFailed: " + e.message)
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)

                    verificationId = p0
                }

            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        userVerifyOtp.setOnClickListener {
            val otp = userOTP.otpEditText!!.text.toString()
            val user = user(
                userName.text.toString(),
                userLastName.text.toString(),
                userEmailId.text.toString(),
                userNumber.text.toString(),
                userPinCode.text.toString())

            sharePref.saveData("UserNumber",userNumber.text.toString())
            sharePref.saveData("UserPinCode",userPinCode.text.toString())

            database.child(userNumber.text.toString()).setValue(user).addOnSuccessListener {
                val intent = Intent(this, LoanAmountActivity::class.java)
                intent.putExtra("userNumber", userNumber.text.toString())
                startActivity(intent)
            }
            if (otp.isNotEmpty()) {
                val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "OTP Verified", Toast.LENGTH_SHORT).show()
                    userSendOtp.visibility = View.GONE
                    userOTP.visibility = View.GONE
                    userVerifyOtp.setText("Next")
                } else {
                    Toast.makeText(this, "${task.exception}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}