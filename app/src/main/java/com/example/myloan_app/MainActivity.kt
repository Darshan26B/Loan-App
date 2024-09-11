package com.example.myloan_app

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.otpview.OTPTextView

class MainActivity : AppCompatActivity() {

    private lateinit var userName: TextInputEditText
    private lateinit var userLastName: TextInputEditText
    private lateinit var userNumber: TextInputEditText
    private lateinit var userSendOtp: TextView
    private lateinit var userVerifyOtp: Button
    private lateinit var userGoogleClick: LinearLayout
    private lateinit var userPolicyClick: CheckBox
    private lateinit var userOTP: OTPTextView
    private lateinit var sharedP:SharedPref
     lateinit var auth: FirebaseAuth
    lateinit var GoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100

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
        userNumber = findViewById(R.id.UserPhoneNumber)
        userSendOtp = findViewById(R.id.send_otp)
        userVerifyOtp = findViewById(R.id.btn_UserVerify)
        userGoogleClick = findViewById(R.id.btn_google)
        userPolicyClick = findViewById(R.id.check_policy)
        userOTP = findViewById(R.id.otp_view)


        sharedP = SharedPref(this)

        auth = FirebaseAuth.getInstance()
        userOTP.visibility = View.GONE


        val user = auth.currentUser
        if (user != null) {
            val intent = Intent(this, DetailActivity::class.java)
            startActivity(intent)
            finish()
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



}