package com.example.myloan_app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.otpview.OTPTextView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var userName: TextInputEditText
    private lateinit var userLastName: TextInputEditText
    private lateinit var userNumber: TextInputEditText
    private lateinit var inputNumber: TextInputLayout
    private lateinit var inputName: TextInputLayout
    private lateinit var inputLastName: TextInputLayout
    private lateinit var userSendOtp: TextView
    private lateinit var userVerifyOtp: Button
    private lateinit var userGoogleClick: LinearLayout
    private lateinit var database: DatabaseReference
    private lateinit var userPolicyClick: CheckBox
    private lateinit var userOTP: OTPTextView
    private lateinit var sharedP: SharedPref
    private lateinit var auth: FirebaseAuth
    private lateinit var GoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100
    private lateinit var verificationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        userName = findViewById(R.id.edt_UserName)
        userLastName = findViewById(R.id.edt_UserLastName)
        inputNumber = findViewById(R.id.txt_UserInputNumber)
        inputName = findViewById(R.id.txt_userInputName)
        inputLastName = findViewById(R.id.txt_userInputLastName)
        userNumber = findViewById(R.id.UserPhoneNumber)
        userSendOtp = findViewById(R.id.send_otp)
        userVerifyOtp = findViewById(R.id.btn_UserVerify)
        userGoogleClick = findViewById(R.id.btn_google)
        userPolicyClick = findViewById(R.id.check_policy)
        userOTP = findViewById(R.id.otp_view)



        inputName.endIconMode = TextInputLayout.END_ICON_NONE
        inputLastName.endIconMode = TextInputLayout.END_ICON_NONE
        inputNumber.endIconMode = TextInputLayout.END_ICON_NONE

        sharedP = SharedPref(this)
        database = FirebaseDatabase.getInstance().getReference("Users")
        auth = FirebaseAuth.getInstance()
        userOTP.visibility = View.GONE

        userIconSet()
        val user = auth.currentUser
        if (user != null) {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
            finish()
        }

        userName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (userName.length() == 0) {
                    inputName.endIconMode = TextInputLayout.END_ICON_NONE
                } else {
                    inputName.endIconMode = TextInputLayout.END_ICON_CUSTOM
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        userLastName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (userLastName.length() == 0) {
                    inputLastName.endIconMode = TextInputLayout.END_ICON_NONE
                } else {
                    inputLastName.endIconMode = TextInputLayout.END_ICON_CUSTOM
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        userSendOtp.setOnClickListener {
            val phoneNumber = "+91" + userNumber.text.toString()

            if (phoneNumber.length != 13) {
                userNumber.error = "Enter 10 digit  phone number"
            } else {
                userOTP.visibility = View.VISIBLE
                phoneAuth(phoneNumber)
            }
        }

        userGoogleClick.setOnClickListener {
            if (userPolicyClick.isChecked) {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

                GoogleSignInClient = GoogleSignIn.getClient(this, gso)
                val signInIntent = GoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)

            } else {
                Toast.makeText(this, "Please accept policy", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, DetailActivity::class.java)

                    sharedP.saveData("Name", account.givenName.toString())
                    sharedP.saveData("LastName", account.familyName.toString())
                    sharedP.saveData("Email", account.email.toString())
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
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
                                this@MainActivity,
                                "Invalid phone number format.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        is FirebaseTooManyRequestsException -> {
                            Toast.makeText(
                                this@MainActivity,
                                "Quota exceeded, try again later.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> {
                            Toast.makeText(
                                this@MainActivity,
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
                    Toast.makeText(this@MainActivity, "OTP SENT", Toast.LENGTH_SHORT).show()
                }

            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        userVerifyOtp.setOnClickListener {
            val otp = userOTP.otpEditText!!.text.toString()
            val setNumber = userNumber.text.toString()


                if (otp.isNotEmpty()) {
                    val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                    signInWithPhoneAuthCredential(credential,setNumber)
                } else {
                    Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show()
                }

        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, setNumber: String) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    database.child(setNumber).get().addOnSuccessListener { snapshot ->
                        if (snapshot.exists()) {
                            val storedNumber = snapshot.child("number").value.toString()
                            if (setNumber == storedNumber) {
                                val intent = Intent(this, HomePageActivity::class.java)
                                sharedP.saveData("userNumber", setNumber)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this, "Number does not match our records. Please register.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "No user found with this number. Please register.", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Error fetching data from Firebase", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Login failed!", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun userIconSet() {
        userNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int,
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (userNumber.text.toString().length == 10) {
                    inputNumber.endIconMode = TextInputLayout.END_ICON_CUSTOM
                } else {
                    inputNumber.endIconMode = TextInputLayout.END_ICON_NONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

}