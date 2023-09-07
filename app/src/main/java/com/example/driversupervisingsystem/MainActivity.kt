package com.example.driversupervisingsystem

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.driversupervisingsystem.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

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
        val txName = intent.getStringExtra(MemberInformation.Name)
        Log.d(ContentValues.TAG,"Current User Name in MainActivity : $txName")
        receivedName = txName
        receivedEmail = intent.getStringExtra(MemberInformation.Email)
        binding?.tvName?.text = receivedName

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
