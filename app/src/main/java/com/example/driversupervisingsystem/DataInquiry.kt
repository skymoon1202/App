package com.example.driversupervisingsystem

import android.content.ContentValues.TAG
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
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
    private val firebaseStorage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_inquiry)

        val tvName : TextView = findViewById(R.id.tv_username)
        val ivDriverImage : ImageView = findViewById(R.id.iv_driver_image)

        val receivedName = intent.getStringExtra(MemberInformation.Name)
        val receivedEmail = intent.getStringExtra(MemberInformation.Email)
        val titleText = "님의 기록"
        tvName.text = receivedName.plus(titleText)

        tableLayout = findViewById(R.id.tl_background)

        if (receivedEmail != null) {
            fetchData(receivedEmail,ivDriverImage,firebaseStorage)
        }else{
            Log.d(TAG,"There is no such email address")
        }


    }

    private fun fetchData(userEmail: String, imageView: ImageView, storage: FirebaseStorage) {
        db.collection(userEmail)
            .orderBy("url")
            .get()
            .addOnSuccessListener { querySnapshot ->
                var order = 1
                var position = 0
                var selectedPosition : Int? = null
                querySnapshot.documents.forEach {

                    val imageUrl = it.getString("url")

                    val tableRow = TableRow(this)
                    val tvDate = TextView(this)
                    val tvOrder = TextView(this)
                    val tvTime = TextView(this)

                    val imageTimeDay : String? = imageUrl?.substring(0,8)
                    val imageTimeType : String? = imageUrl?.substring(9,15)

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

                    //val fixedOrder = tvOrder.text.toString().toInt()

                    tvDate.textSize = 20F
                    tvOrder.textSize = 20F
                    tvTime.textSize = 20F
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
                    //defaultRow(tvOrder,tvDate,tvTime)

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

                    tvDate.setPadding(2,10,2,10)
                    tvOrder.setPadding(2,10,2,10)
                    tvTime.setPadding(2,10,2,10)

                    tableRow.addView(tvOrder)
                    tableRow.addView(tvDate)
                    tableRow.addView(tvTime)

                    tableRow.layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        50
                    )
                    tableRow.setOnClickListener {
                        position = tableLayout.indexOfChild(tableRow)
                        selectedRow(tvOrder, tvDate, tvTime)
                        if (selectedPosition != null) {
                            val previousSelectedRow = tableLayout.getChildAt(selectedPosition!!) as? TableRow
                            if (previousSelectedRow != null) {
                                defaultRow(
                                    previousSelectedRow.getChildAt(0) as TextView,
                                    previousSelectedRow.getChildAt(1) as TextView,
                                    previousSelectedRow.getChildAt(2) as TextView
                                )
                            }
                        }
                        Log.d(TAG,"Current : $position, Previous : $selectedPosition")
                        selectedPosition = position
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

    private fun selectedRow (tvOrder : TextView, tvDate : TextView, tvTime : TextView){
        tvOrder.setTextColor(Color.parseColor("#FFFFFFFF"))
        tvDate.setTextColor(Color.parseColor("#FFFFFFFF"))
        tvTime.setTextColor(Color.parseColor("#FFFFFFFF"))
        tvOrder.setTypeface(tvOrder.typeface, Typeface.BOLD)
        tvTime.setTypeface(tvTime.typeface, Typeface.BOLD)
        tvDate.setTypeface(tvDate.typeface, Typeface.BOLD)
        tvOrder.setBackgroundColor(Color.parseColor("#FF3700B3"))
        tvDate.setBackgroundColor(Color.parseColor("#FF3700B3"))
        tvTime.setBackgroundColor(Color.parseColor("#FF3700B3"))
    }
    private fun defaultRow (tvOrder : TextView, tvDate : TextView, tvTime : TextView){
        tvDate.setTextColor(Color.parseColor("#588157"))
        tvOrder.setTextColor(Color.parseColor("#588157"))
        tvTime.setTextColor(Color.parseColor("#588157"))
        tvOrder.setTypeface(tvOrder.typeface, Typeface.NORMAL)
        tvTime.setTypeface(tvTime.typeface, Typeface.NORMAL)
        tvDate.setTypeface(tvDate.typeface, Typeface.NORMAL)
        tvOrder.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
        tvDate.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
        tvTime.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
    }
}