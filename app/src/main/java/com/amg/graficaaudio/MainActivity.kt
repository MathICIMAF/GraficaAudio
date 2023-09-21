package com.amg.graficaaudio

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.ActivityCompat
import java.io.File
import java.security.Permissions

class MainActivity : AppCompatActivity() {


    lateinit var output : String //Carpeta de salida
    lateinit var mediaRecorder: MediaRecorder // Clase para grabar sonidos
    var grabando = false
    var grabacionPausada = false
    lateinit var playButton : ImageButton
    lateinit var stopButton : ImageButton
    lateinit var mensajeTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playButton = findViewById(R.id.play_button)
        stopButton = findViewById(R.id.stop_button)
        mensajeTextView = findViewById(R.id.mensaje)


        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            inicializaBotones()
        }
        else{
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),1234)
        }


    }

    fun inicializaBotones(){
        playButton.setOnClickListener(View.OnClickListener {
            comenzarGrabar()
        })

        stopButton.setOnClickListener(View.OnClickListener {
            detenerGrabacion()
        })
    }

    fun inicializarMedia(){
        mediaRecorder = MediaRecorder()
        if (Build.VERSION.SDK_INT > 29){
            output = getExternalFilesDir(null)!!.absolutePath+"/Grabador Audio"
        }
        else
            output = Environment.getExternalStorageDirectory().absolutePath + "/Grabador Audio"
        val file = File(output)
        if (!file.exists())
            file.mkdir()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setOutputFile(output+"/grabacion.mp3")
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if(requestCode == 1234){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                inicializaBotones()
        }
    }

    private fun comenzarGrabar() {
        if(grabando){
            if (grabacionPausada){
                mediaRecorder.resume()
                playButton.setBackgroundResource(R.drawable.baseline_pause_circle_24)
                mensajeTextView.text = "Grabando audio..."
                grabacionPausada = false
            }
            else{
                mediaRecorder.pause()
                playButton.setBackgroundResource(R.drawable.baseline_play_circle_24)
                mensajeTextView.text = "Grabacion pausada..."
                grabacionPausada = true
            }
        }
        else{
            inicializarMedia()
            mediaRecorder.prepare()
            mediaRecorder.start()
            grabando = true
            grabacionPausada = false
            playButton.setBackgroundResource(R.drawable.baseline_pause_circle_24)
            mensajeTextView.text = "Grabando audio..."
        }

    }

    private fun detenerGrabacion(){
        if (grabando){
            mediaRecorder.stop()
            mediaRecorder.reset()
            mediaRecorder.release()
            grabando = false
            grabacionPausada = false
            mensajeTextView.text = "Comenzar a grabar..."
            playButton.setBackgroundResource(R.drawable.baseline_play_circle_24)
        }
    }
}