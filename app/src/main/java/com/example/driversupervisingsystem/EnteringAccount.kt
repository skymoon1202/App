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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EnteringAccount : AppCompatActivity() {

    private var auth : FirebaseAuth? = null
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entering_account)
        val etEmail : EditText = findViewById(R.id.et_address)
        val etPassword : EditText = findViewById(R.id.et_password)
        val btnEnter : Button = findViewById(R.id.btn_enter)
        auth = Firebase.auth

        btnEnter.setOnClickListener {
            if(etEmail.text.isEmpty() or etPassword.text.isEmpty()){
                Toast.makeText(this, "이메일과 비밀번호를 모두 입력해주세요",Toast.LENGTH_LONG).show()
            }else{
                val txEmail : String = etEmail.text.toString()
                val txPassword : String = etPassword.text.toString()
                signIn(txEmail,txPassword)
            }
        }

    }

    private fun signIn(email: String, password: String) {
        auth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                    val userRef = db.collection("member").document(email)
                    userRef.get().addOnSuccessListener {document ->
                        if (document != null) {
                            val information = document.data
                            val dataName = information?.get("Name") as String
                            val dataEmail = information["Email"] as String
                            if(auth!!.currentUser?.isEmailVerified == true){
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra(MemberInformation.Name,dataName)
                                intent.putExtra(MemberInformation.Email,dataEmail)
                                startActivity(intent)
                                finish()
                            }else{
                                val intent2 = Intent(this, EmailVerification::class.java)
                                intent2.putExtra(MemberInformation.Name,dataName)
                                intent2.putExtra(MemberInformation.Email,dataEmail)
                                Log.d(TAG,"Current User Name : $dataName")
                                startActivity(intent2)
                                finish()
                            }

                        } else {
                            Log.d(TAG, "No such document")
                        }
                        }

                }else{
                    Toast.makeText(baseContext, "이메일과 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun goToMain(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
