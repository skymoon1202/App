package com.example.driversupervisingsystem

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailVerification : AppCompatActivity() {

    private var auth : FirebaseAuth? = null
    private var user : FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verification)

        val btnEmailVerification : Button = findViewById(R.id.btn_email_verification)
        val btnVerificationDone : Button = findViewById(R.id.btn_verification_done)
        val txUserEmail : TextView = findViewById(R.id.user_email)

        auth = Firebase.auth
        user = auth!!.currentUser


        txUserEmail.text = user?.email
        Log.d(TAG,"Current Email : ${user?.email}")

        btnEmailVerification.setOnClickListener {
            user?.let { emailVerification(it) }
        }

        btnVerificationDone.setOnClickListener {
            if (user != null) {
                if(user!!.isEmailVerified){
                    val intent = Intent(this,MainActivity::class.java)
                }else{
                    Toast.makeText(this,"이메일 인증을 완료해주세요",Toast.LENGTH_LONG).show()
                }
            }
        }

    }


    private fun emailVerification(user: FirebaseUser) {
        user.sendEmailVerification()
            .addOnCompleteListener { verificationTask ->
                if (verificationTask.isSuccessful) {
                    Log.d(TAG,"Email successfully sent")
                } else {
                    Log.e(TAG,"error: ${verificationTask.exception}")
                }
            }
    }
}