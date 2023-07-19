package com.example.driversupervisingsystem

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SignIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)

        val tvSignIn : TextView = findViewById(R.id.btn_sign_in)
        val tvCreateAccount : TextView = findViewById(R.id.btn_create_account_signin)

        tvSignIn.setOnClickListener {
            val intent = Intent(this, EnteringAccount::class.java)
            startActivity(intent)
            finish()
        }

        tvCreateAccount.setOnClickListener {
            val intent2 = Intent(this, CreateAccount::class.java)
            startActivity(intent2)
            finish()
        }
    }
}