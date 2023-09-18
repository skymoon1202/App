package com.example.driversupervisingsystem

import android.content.ContentValues.TAG
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

import java.net.Socket
import java.text.SimpleDateFormat
import java.util.*

private var socket : SocketClient? = null

class DriveStart : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drive_start)

        val btnDriveStart: Button = findViewById(R.id.btn_drive_start)
        val btnDriveFinish : Button = findViewById(R.id.btn_drive_finish)
        val ivDriverPhoto : ImageView = findViewById(R.id.iv_driver_photo)
        val internalPath = this.filesDir.absolutePath
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss_", Locale.getDefault())
        val userName = intent.getStringExtra(MemberInformation.Name)
        val userEmail = intent.getStringExtra(MemberInformation.Email)
        //val scope : CoroutineScope = CoroutineScope(Dispatchers.Main)
        var connectionJob: Job? = null

        btnDriveStart.setOnClickListener {
            connectionJob = lifecycleScope.launch {
                while(true){
                    try {
                        socket = SocketClient("192.168.32.1",9999,internalPath)
                        val currentTime: String = sdf.format(Date())
                        val receivedPhoto = socket!!.receivePhoto(currentTime.plus(userEmail))
                        withContext(Dispatchers.Main) {
                            if (receivedPhoto != null) {
                                Toast.makeText(this@DriveStart,"${receivedPhoto.name} downloaded", Toast.LENGTH_LONG).show()
                                Log.d(TAG,"Image downloaded at $internalPath, and absolutepath is ${receivedPhoto.absolutePath}")
                                val bitmap = BitmapFactory.decodeFile(receivedPhoto.absolutePath)
                                ivDriverPhoto.setImageBitmap(bitmap)
                                ivDriverPhoto.setOnClickListener {
                                    val storageRef = FirebaseStorage.getInstance().reference.child(receivedPhoto.name)
                                    storageRef.putFile(Uri.fromFile(receivedPhoto)).addOnSuccessListener {
                                            // Firestore 에 이미지 URL 저장
                                            val db = FirebaseFirestore.getInstance()
                                            if (userEmail != null) {
                                                db.collection(userEmail).document(receivedPhoto.name.substring(0,15))
                                                    .set(mapOf("url" to receivedPhoto.name))
                                                    .addOnSuccessListener {
                                                        Toast.makeText(
                                                            this@DriveStart,
                                                            "Image URL saved to Firestore.",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }.addOnFailureListener { exception ->
                                                        Toast.makeText(
                                                            this@DriveStart,
                                                            "Failed to save image URL: ${exception.message}",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                            }

                                    }.addOnFailureListener { exception ->
                                        Toast.makeText(this@DriveStart,"Failed to upload image: ${exception.message}",Toast.LENGTH_LONG).show()
                                    }
                                }

                            } else {
                                Toast.makeText(this@DriveStart, "사진이 전송되지 않음.", Toast.LENGTH_LONG).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@DriveStart,"connection wrong",Toast.LENGTH_LONG).show()
                        }
                    }
                    delay(10000)
                }
            }
        }

        btnDriveFinish.setOnClickListener {
            connectionJob?.cancel()
            if (connectionJob?.isActive == true){
                Toast.makeText(this,"Coroutine is running.",Toast.LENGTH_LONG).show()
                connectionJob = null
            }else{
                Toast.makeText(this,"Coroutine is cancelled",Toast.LENGTH_LONG).show()
            }
        }
    }
}
