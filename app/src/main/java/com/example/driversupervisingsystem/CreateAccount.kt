package com.example.driversupervisingsystem

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateAccount : AppCompatActivity() {

    private var auth : FirebaseAuth? = null
    private val db = FirebaseFirestore.getInstance().collection("member")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val etName : EditText = findViewById(R.id.et_new_name)
        val etEmailAddress : EditText = findViewById(R.id.et_new_address)
        val etPassword : EditText= findViewById(R.id.et_new_password)
        val etPassword2 : EditText= findViewById(R.id.et_new_password2)
        val btnCreateAccount : Button = findViewById(R.id.btn_create_account)
        auth = Firebase.auth


        btnCreateAccount.setOnClickListener {
            when{
                etEmailAddress.text.isEmpty() -> Toast.makeText(this, "Please enter your email address", Toast.LENGTH_LONG).show()
                etPassword.text.isEmpty() -> Toast.makeText(this, "Please enter your password", Toast.LENGTH_LONG).show()
                etName.text.isEmpty() -> Toast.makeText(this, "Please enter your name", Toast.LENGTH_LONG).show()
                else -> if(etPassword.text.toString() == etPassword2.text.toString()) {
                    val txEmailAddress: String = etEmailAddress.text.toString()
                    val txName : String = etName.text.toString()
                    val txPassword : String = etPassword.text.toString()
                    createAccount(txEmailAddress,txPassword)
                    createUserField(txEmailAddress, txName)
                    val intent = Intent(this,MainActivity::class.java)
                    intent.putExtra("key2",txName)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this, "Please confirm your password", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

        private fun createAccount(EmailAddress: String, Password: String) {
            auth?.createUserWithEmailAndPassword(EmailAddress,Password)?.addOnCompleteListener(this) {
                task -> if (task.isSuccessful) {
                    Toast.makeText(baseContext,"Successfully created",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(baseContext,"Something wrong",Toast.LENGTH_LONG).show()
                }
            }
        }

        private fun createUserField(EmailAddress : String, Name : String) {
            val dataToSave = hashMapOf("Email" to EmailAddress, "Name" to Name)
            db.document(EmailAddress)
                .set(dataToSave)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
    }
