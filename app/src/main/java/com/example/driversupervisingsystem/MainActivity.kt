package com.example.driversupervisingsystem

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private var auth : FirebaseAuth? = null
    private var receivedName : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ivQrcode : ImageView = findViewById(R.id.qr_code)
        val tvName : TextView = findViewById(R.id.tv_name)

        receivedName = try{
            intent.getStringExtra("key") as String
        }catch(e: NullPointerException){
            intent.getStringExtra("key2") as String
        }
        tvName.text = receivedName
    }

}
