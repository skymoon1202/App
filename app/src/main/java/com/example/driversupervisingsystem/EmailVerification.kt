package com.example.driversupervisingsystem

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
        val txName = intent.getStringExtra(MemberInformation.Name)

        auth = Firebase.auth
        user = auth!!.currentUser

        txUserEmail.text = user?.email

        btnEmailVerification.setOnClickListener {
            user?.let { emailVerification(it) }
        }

        btnVerificationDone.setOnClickListener {
            user?.reload()?.addOnCompleteListener {
                if (it.isSuccessful) {
                    if (user!!.isEmailVerified) {
                        Log.d(TAG,"Current User Name in EmailVerification: $txName")
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra(MemberInformation.Name,txName)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "이메일 인증이 완료되지 않았습니다", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.e(TAG, "Error : ${it.exception}")
                }
            }
        }
    }

    /*private fun emailDialog1(user : FirebaseUser){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("이메일 인증 완료")
        builder.setMessage("위 주소로 도착한 메일의 링크로 인증하셨나요?")
        builder.setIcon(R.drawable.email_icon)
        builder.setPositiveButton("Yes"){ dialogInterface, which ->
            user.reload().addOnCompleteListener {
                if (it.isSuccessful) {
                    if (user.isEmailVerified) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this,"이메일 인증이 완료되지 않았습니다",Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.e(TAG,"Error : ${it.exception}")
                }
            }

            dialogInterface.dismiss()
        }
        builder.setNegativeButton("No"){ dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertdialog : AlertDialog = builder.create()
        alertdialog.setCancelable(false)
        alertdialog.show()
    }*/

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