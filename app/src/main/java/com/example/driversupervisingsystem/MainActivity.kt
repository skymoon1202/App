package com.example.driversupervisingsystem

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.driversupervisingsystem.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private var auth : FirebaseAuth? = null
    private var receivedName : String? = null
    private var receivedEmail : String? = null
    private var binding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        setSupportActionBar(binding?.actionbarMain)
        binding?.actionbarMain?.setNavigationOnClickListener {
            onBackPressed()

        }
        receivedName = intent.getStringExtra(MemberInformation.Name)
        receivedEmail = intent.getStringExtra(MemberInformation.Email)
        // receivedName = try{
        //     intent.getStringExtra("key") as String
        // }catch(e: NullPointerException){
        //     intent.getStringExtra("key2") as String
        // }
        binding?.tvName?.text = receivedName.plus("님의 운전점수")

        binding?.btnInquiry?.setOnClickListener {
            val intent = Intent(this, DataInquiry::class.java)
            intent.putExtra(MemberInformation.Name,receivedName)
            intent.putExtra(MemberInformation.Email,receivedEmail)

            startActivity(intent)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}
