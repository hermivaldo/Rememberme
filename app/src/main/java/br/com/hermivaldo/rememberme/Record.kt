package br.com.hermivaldo.rememberme

import android.Manifest
import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

private const val LOG_TAG = "Record"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class Record : AppCompatActivity() {

    private var mFileName: String = ""

    private var mRecord: MediaRecorder? = null
    private var mRecordButton: RecordButton? = null

    private var mPlayer: MediaPlayer? = null
    private var mPlayButton: PlayButton? = null

    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permissionToRecordAccepted = false

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {grantResults[0] == PackageManager.PERMISSION_GRANTED} else {false}
        if (!permissionToRecordAccepted) finish()
    }

    private fun startPlaying(){
        mPlayer = MediaPlayer().apply {
            try {
                setDataSource(mFileName)
                prepare()
                start()
            }catch (e: Exception){
                Log.e(LOG_TAG, e.localizedMessage)
            }
        }
    }

    private fun stopPlaying(){
        mPlayer?.release()
        mPlayer = null
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }
    private fun startRecording(){
        mRecord = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(mFileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            }catch (e: Exception){
                Log.e(LOG_TAG, e.localizedMessage)
            }

            start()
        }
    }

    private fun stopRecording(){
        mRecord?.apply {
            stop()
            release()
        }

        mRecord = null

    }

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    @Throws(IOException::class)
    private fun createPath(prefix: String, typeFile: String) : File {
        var timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile("${prefix}_${timeStamp}", ".${typeFile}", storageDir).apply {
            mFileName = absolutePath
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createPath("record", "3gp")

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        mRecordButton = RecordButton(this)
        mPlayButton = PlayButton(this)

        val ll = LinearLayout(this).apply {
            addView(mPlayButton,
            LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0f
            ))
            addView(mRecordButton,
                    LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            0f
                    ))
        }

        setContentView(ll)
    }

    internal inner class RecordButton(ctx: Context) : Button(ctx){
        var mStartRecorging = false

        var clicker: OnClickListener = OnClickListener {
            onRecord(mStartRecorging)
            text = when (mStartRecorging) {
                true -> "Stop recording"
                false -> "Start recording"
            }

            mStartRecorging = !mStartRecorging
        }

        init {
            text = "Start recording"
            setOnClickListener(clicker)
        }
    }

    internal inner class PlayButton(ctx: Context): Button(ctx){
        var mStartPlaying = false
        var clicker: OnClickListener = OnClickListener {
            onPlay(mStartPlaying)
            text = when(mStartPlaying){
                true -> "Stop playing"
                false -> "Start playing"
            }
            mStartPlaying = !mStartPlaying
        }

        init {
            text = "Start playing"
            setOnClickListener(clicker)
        }
    }

    override fun onBackPressed() {
        var intent: Intent = Intent()
        intent.putExtra("AUDIO", mFileName)
        setResult(1, intent)
        super.onBackPressed()
    }
}
