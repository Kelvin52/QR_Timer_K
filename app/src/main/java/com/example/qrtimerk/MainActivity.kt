package com.example.qrtimerk

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


private const val CAMERA_REQUEST_CODE = 101



class MainActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner

    private var seconds = 0

    private var secCounter = 0

    private var timerRunning: Boolean = true

    private val handler = Handler()

//    private val chronometer: Chronometer = findViewById(R.id.chronometer)
//    private val pauseOffset: Long = 0
//    private val running = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupPermissions()
        codeScanner()


        val button: Button = findViewById(R.id.stBut)
        button.setOnClickListener {
            timerRunning = false
//            handler.removeCallbacks(null)
            Log.d("OMG", "stBut pressed")
            Log.d("OMG", "timerRunning :$timerRunning")
        }

        runTimer()

    }



    private fun codeScanner(){

       codeScanner = CodeScanner(this, scanner_view)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                runOnUiThread {
                    tv_textView.text = it.text

                    timerRunning = false
                    //Todo hide timer at start and shown after QR is scanned
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread{
                    Log.e("Main", "Camera initialization error:${it.message}")
                }
            }

        }

    }



    private fun runTimer(){

        val timeView = findViewById<TextView>(R.id.timerView)

        handler.post(object : Runnable {
            override fun run() {
                val hours = seconds / 3600
                val minutes = seconds % 3600 / 60
                val secs = seconds % 60

                val time = java.lang.String
                    .format(
                        Locale.getDefault(),
                        "%d:%02d:%02d", hours,
                        minutes, secs
                    )

                timeView.text = time

                if (timerRunning) {
                    secCounter++
                    if ( secCounter == 2){
                        seconds += 1
                        secCounter = 0
                    }

                }

                handler.postDelayed(this, 500)
            }
        })


    }



    private fun save(){
        //Todo: Save Time, in local, as database.
    }

    private fun postDAT(){
        //Todo: POSt to cloudflare, Need link to do that
    }



    override fun onResume(){
        super.onResume()
        codeScanner.startPreview()

    }

    override fun onPause(){
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions(){
        val permission = ContextCompat.checkSelfPermission(this,
        android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(this,
        arrayOf(android.Manifest.permission.CAMERA),
        CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode){
            CAMERA_REQUEST_CODE -> run {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You need camera permission to be able to use this app!", Toast.LENGTH_SHORT).show()
                } else {
                    //it was successful
                }
            }
        }
    }

}