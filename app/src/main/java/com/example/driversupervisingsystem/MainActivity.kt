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
        binding?.btnDriveStart?.setOnClickListener {
            val intent2 = Intent(this, DriveStart::class.java)
            intent2.putExtra(MemberInformation.Name,receivedName)
            intent2.putExtra(MemberInformation.Email,receivedEmail)
            startActivity(intent2)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}
