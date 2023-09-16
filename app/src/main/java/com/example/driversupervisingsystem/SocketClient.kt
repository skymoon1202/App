package com.example.driversupervisingsystem

import android.content.ContentValues.TAG
import android.content.Context
import android.nfc.Tag
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.google.android.material.tabs.TabLayout.TabGravity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.*
import java.net.Socket

class SocketClient(private val host: String, private val port: Int, private val pathToSave: String) {

    private var clientSocket: Socket? = null

    /*suspend fun startConnection() = withContext(Dispatchers.IO) {
        clientSocket = Socket(host, port)
        while (isActive) { // Check if the coroutine is still active
            receiveFile(clientSocket!!.getInputStream(), pathToSave)
            delay(10000) // Wait for 10 seconds
        }
    }

    private suspend fun receiveFile(inputStream: InputStream, pathToSave: String) = withContext(Dispatchers.IO) {
        try {
            val bis = BufferedInputStream(inputStream)
            val fos = FileOutputStream(pathToSave)
            val buffer = ByteArray(1024)
            var bytes: Int
            while (true) {
                bytes = bis.read(buffer)
                if (bytes == -1) {
                    break
                }
                fos.write(buffer, 0, bytes)
            }
            Log.d(TAG,"Downloaded")
            // Close the streams.
            bis.close()
            fos.close()
            /*withContext(Dispatchers.Main) {
                Toast.makeText(context, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show()
            }*/
        } catch (e: IOException) {
            e.printStackTrace()
            /*withContext(Dispatchers.Main) {
                Toast.makeText(context, "사진이 전송되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }*/
        }

    }*/

    suspend fun receivePhoto(fileName: String): File? =
        withContext(Dispatchers.IO) {
            return@withContext try {
                Socket(host, port).use { socket ->
                    val receivedFile = File(pathToSave, fileName)
                    FileOutputStream(receivedFile).use { fileOutput ->
                        BufferedInputStream(socket.getInputStream()).use { inputStream ->
                            val buffer = ByteArray(1024)
                            var bytesRead: Int

                            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                fileOutput.write(buffer, 0, bytesRead)
                            }

                            fileOutput.flush()
                        }
                    }
                    receivedFile
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    fun closeConnection() {
        try {
            clientSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }




    /*private fun receiveFile(input: InputStream, pathToSave: String) {
        val fileOutputStream = FileOutputStream(File(pathToSave))
        input.copyTo(fileOutputStream)
    }*/
}