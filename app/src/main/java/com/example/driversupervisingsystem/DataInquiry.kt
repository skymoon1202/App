package com.example.driversupervisingsystem

import android.content.ContentValues.TAG
import android.graphics.BitmapFactory
import android.graphics.Color
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class DataInquiry : AppCompatActivity() {

    private var db = FirebaseFirestore.getInstance()
    private lateinit var tableLayout: TableLayout
    val firebaseStorage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_inquiry)

        val tvName : TextView = findViewById(R.id.tv_username)
        val ivDriverImage : ImageView = findViewById(R.id.iv_driver_image)

        val receivedName = intent.getStringExtra(MemberInformation.Name)
        val receivedEmail = intent.getStringExtra(MemberInformation.Email)
        val titleText = "'s data"
        tvName.text = receivedName.plus(titleText)

        tableLayout = findViewById(R.id.tl_background)

        if (receivedEmail != null) {
            if (receivedName != null) {
                fetchData(receivedEmail,ivDriverImage,firebaseStorage, receivedName)
            }
        }else{
            Log.d(TAG,"There is no such email address")
        }


    }

    private fun fetchData(userEmail: String, imageView: ImageView, storage: FirebaseStorage, userName : String) {
        db.collection(userEmail)
            .orderBy("1") // 정렬 기준을 여기에 적용합니다.
            .get()
            .addOnSuccessListener { querySnapshot ->
                var order = 1
                querySnapshot.documents.forEach {
                    // 이미지 목록에서 정보를 조회합니다.
                    //val imageUrl = it.getString("1") ?: return@forEach
                    val imageUrl = it.getString("1")

                    val tableRow = TableRow(this)
                    val tvDate = TextView(this)
                    val tvOrder = TextView(this)
                    val tvTime = TextView(this)

                    val imageTimeDay : String? = imageUrl?.substring(0,7)
                    val imageTimeType : String? = imageUrl?.substring(9,14)

                    val dateInputFormat = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
                    val timeInputFormat = SimpleDateFormat("HHmmss",Locale.KOREA)

                    val dateOutputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.KOREA)
                    val timeOutputFormat = SimpleDateFormat("HH:mm:ss",Locale.KOREA)

                    val date = imageTimeDay?.let { it1 -> dateInputFormat.parse(it1) }
                    val time = imageTimeType?.let { it1 -> timeInputFormat.parse(it1) }

                    val resultDate = date?.let { it1 -> dateOutputFormat.format(it1) }
                    val resultTime = time?.let { it1 -> timeOutputFormat.format(it1) }

                    tvDate.text = resultDate
                    tvOrder.text = "$order"
                    tvTime.text = resultTime

                    tvDate.textSize = 14F
                    tvOrder.textSize = 14F
                    tvTime.textSize = 14F
                    tvDate.gravity = Gravity.CENTER
                    tvOrder.gravity = Gravity.CENTER
                    tvTime.gravity = Gravity.CENTER
                    tvDate.setTextColor(Color.parseColor("#588157"))
                    tvOrder.setTextColor(Color.parseColor("#588157"))
                    tvTime.setTextColor(Color.parseColor("#588157"))


                    //val background = GradientDrawable()
                    //background.setColor(ContextCompat.getColor(this, R.color.white))
                    tvDate.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    tvOrder.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    tvTime.setBackgroundColor(Color.parseColor("#FFFFFF"))

                    val marginDate = TableRow.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                    )
                    marginDate.setMargins(2,2,2,1) // 원하는 마진 값으로 변경(예시: 16 dp)
                    tvDate.layoutParams = marginDate

                    val marginOrder = TableRow.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    marginOrder.setMargins(2,2,2,1)
                    tvOrder.layoutParams = marginOrder

                    val marginTime = TableRow.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    marginTime.setMargins(2,2,2,1)
                    tvTime.layoutParams = marginTime

                    tvDate.setPadding(2,2,2,2)
                    tvOrder.setPadding(2,2,2,2)
                    tvTime.setPadding(2,2,2,2)

                    tableRow.addView(tvOrder)
                    tableRow.addView(tvDate)
                    tableRow.addView(tvTime)

                    tableRow.layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        50
                    )
                    tableRow.setOnClickListener {
                        if (imageUrl != null) {
                            loadImageFromFirebaseStorage(imageView, storage, imageUrl)
                        }
                    }

                    tableLayout.addView(tableRow)

                    order++
                }
            }.addOnFailureListener {
                // 에러 처리
            Log.e(TAG,"error")
            }
    }

    private fun loadImageFromFirebaseStorage(imageView: ImageView, storage: FirebaseStorage, imagePath: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val bitmap = downloadImageFromFirebase(storage, imagePath)
            imageView.setImageBitmap(bitmap)
        }
    }

    private suspend fun downloadImageFromFirebase(storage: FirebaseStorage, imagePath: String) = withContext(Dispatchers.IO) {
        val imageRef = storage.reference.child(imagePath)
        val maximumSize: Long = 10 * 1024 * 1024 // 최대 10MB 사이즈로 설정
        try {
            val imageBytes = imageRef.getBytes(maximumSize).await()
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}