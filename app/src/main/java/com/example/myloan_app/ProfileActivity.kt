package com.example.myloan_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.log

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileName: TextInputEditText
    private lateinit var profileLastName: TextInputEditText
    private lateinit var profileEmailID: TextInputEditText
    private lateinit var profileNumber: TextInputEditText
    private lateinit var profilePinCode: TextInputEditText
    private lateinit var profileLoanAmount: TextInputEditText
    private lateinit var profileDOB: TextInputEditText
    private lateinit var profileGender: TextInputEditText
    private lateinit var profileProfession: TextInputEditText
    private lateinit var profileIncome: TextInputEditText
    private lateinit var profilePanCardNumber: TextInputEditText
    private lateinit var profileSubmit: MaterialButton
    private lateinit var sharePref: SharedPref
    private lateinit var database: DatabaseReference
    private var userNumber = ""
    private var setNumber = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        profileName = findViewById(R.id.profile_Name)
        profileLastName = findViewById(R.id.profile_LastName)
        profileEmailID = findViewById(R.id.profile_EmailID)
        profileNumber = findViewById(R.id.profile_Number)
        profilePinCode = findViewById(R.id.profile_PinCode)
        profileLoanAmount = findViewById(R.id.profile_LoanAmount)
        profileDOB = findViewById(R.id.profile_DOB)
        profileGender = findViewById(R.id.profile_Gender)
        profileProfession = findViewById(R.id.profile_Profession)
        profileIncome = findViewById(R.id.profile_Income)
        profilePanCardNumber = findViewById(R.id.profile_PanCard)
        profileSubmit = findViewById(R.id.profile_Submit)

        sharePref = SharedPref(this)
        database = FirebaseDatabase.getInstance().getReference("Users")

        userNumber = (intent.getStringExtra("userNumber") ?: sharePref.getData("userNumber")).toString()
        Log.e("fetchUserData", "userNumber: $userNumber")
        setNumber = sharePref.getData("setNumber").toString()
        Log.e("fetchUserData", "setNumber: $setNumber")


            fetchUserData1()
            Log.e("fetchUserData", "fetchUserData1: "+fetchUserData1())
//        } else {
//            fetchUserData()
//            Log.e("fetchUserData", "fetchUserData: "+fetchUserData())
//        }


//        if (userNumber.isEmpty()) {
//            fetchUserData()
//        }

        profileSubmit.setOnClickListener {
            editProfile()
            startActivity(Intent(this, HomePageActivity::class.java))
        }
    }

    private fun fetchUserData() {

        database.child(userNumber).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.value as? Map<String, Any>

                    if (user != null) {
                        val name = user["name"] as? String ?: ""
                        val lastName = user["lastName"] as? String ?: ""
                        val email = user["email"] as? String ?: ""
                        val number = user["number"] as? String ?: ""
                        val pinCode = user["pinCode"] as? String ?: ""
                        val loanAmount = user["loanAmount"] as? String ?: ""
                        val birthDate = user["birthDate"] as? String ?: ""
                        val gender = user["gender"] as? String ?: ""
                        val income = user["income"] as? String ?: ""

                        val professionSnapshot = snapshot.child("profession/profession")
                        val profession = professionSnapshot.getValue(String::class.java) ?: ""

                        val panCardNumber = user["panCardNumber"] as? String ?: "N/A"
                        val panCardDisplay = if (panCardNumber.isEmpty()) {
                            "N/A"
                        } else {
                            panCardNumber
                        }

                        // Set the values in TextInputEditText
                        profileName.setText(name)
                        profileLastName.setText(lastName)
                        profileNumber.setText(number)
                        profileEmailID.setText(email)
                        profilePinCode.setText(pinCode)
                        profileLoanAmount.setText(loanAmount)
                        profileDOB.setText(birthDate)
                        profileGender.setText(gender)
                        profileProfession.setText(profession)
                        profileIncome.setText(income)
                        profilePanCardNumber.setText(panCardDisplay)
                    } else {
                        Toast.makeText(
                            this@ProfileActivity,
                            "User data is empty",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "User not found", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Failed to load data", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun fetchUserData1() {
        database.child(userNumber).get().addOnSuccessListener {

            if (it.exists()) {

                val firstname = it.child("name").value
                val lastName = it.child("lastName").value
                val email = it.child("email").value
                val number = it.child("number").value
                val pinCode = it.child("pinCode").value
                val loanAmount = it.child("loanAmount").value
                val birthDate = it.child("birthDate").value
                val gender = it.child("gender").value
                val profession = it.child("profession/profession").value
                val income = it.child("income").value
                val  panCardNumber = it.child("panCardNumber").value

                profileName.setText(firstname.toString())
                profileLastName.setText(lastName.toString())
                profileDOB.setText(email.toString())
                profileNumber.setText(number.toString())
                profilePinCode.setText(pinCode.toString())
                profileLoanAmount.setText(loanAmount.toString())
                profileDOB.setText(birthDate.toString())
                profileGender.setText(gender.toString())
                profileProfession.setText(profession.toString())
                profileIncome.setText(income.toString())
                profilePanCardNumber.setText(panCardNumber.toString())

                Log.e("fetchUserData", "firstname$firstname")

            } else {

                Toast.makeText(this, "User Doesn't Exist", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {

            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }


    private fun editProfile() {

        val loanAmountText = profileLoanAmount.text.toString()
        val loanAmount = loanAmountText.toDoubleOrNull() ?: 0.0
        val maxLoanAmount = 5000000.0
        if (loanAmount > maxLoanAmount) {
            profileLoanAmount.error = "Loan amount should not exceed $maxLoanAmount"
            return
        }

        val editData = mapOf(
            "name" to profileName.text.toString(),
            "lastName" to profileLastName.text.toString(),
            "email" to profileEmailID.text.toString(),
            "number" to profileNumber.text.toString(),
            "pinCode" to profilePinCode.text.toString(),
            "loanAmount" to loanAmountText,
            "birthDate" to profileDOB.text.toString(),
            "gender" to profileGender.text.toString(),
            "income" to profileIncome.text.toString(),
        )
        database.child(userNumber).updateChildren(editData).addOnSuccessListener {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
        }
    }

}