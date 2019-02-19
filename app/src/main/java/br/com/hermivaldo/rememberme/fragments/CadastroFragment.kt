package br.com.hermivaldo.rememberme.fragments


import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import android.widget.VideoView

import br.com.hermivaldo.rememberme.R
import br.com.hermivaldo.rememberme.Record
import br.com.hermivaldo.rememberme.dao.AppDataBase
import br.com.hermivaldo.rememberme.dao.MemoryDAO
import br.com.hermivaldo.rememberme.databinding.FragmentCadastroBinding
import br.com.hermivaldo.rememberme.entidades.Memory
import com.android.databinding.library.baseAdapters.BR
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_cadastro.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val REQUEST_AUDIO_RECORD = 200
const val REQUEST_IMAGE_CAPTURE = 1
const val REQUEST_VIDEO_CAPTURE = 2

class CadastroFragment : Fragment(), OnDateSetListener{

    var mCurrentPath: String? = null
    var mCurrentPathCamera: String? = null
    private var db: AppDataBase? = null
    private var memoryDAO: MemoryDAO? = null
    private var mMemory: Memory? = null
    private var inflate : FragmentCadastroBinding? = null



    @Throws(IOException::class)
    private fun createPath(mPath: String, typeFile: String) : File{
        var timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = this.activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile("${mPath}_${timeStamp}", ".${typeFile}", storageDir).apply {
            when (typeFile){
                "jpg" -> mCurrentPath = absolutePath
                "mp4" -> mCurrentPathCamera = absolutePath
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
       this.mMemory?.dEscolhida = ""+ dayOfMonth + "/" + (month + 1) + "/" + year

       inflate!!.setVariable(BR.memory, mMemory)
       inflate!!.executePendingBindings()
    }

    fun playVideo(v: View){
        var videoView = v as VideoView

        videoView.setVideoURI(Uri.parse(mCurrentPathCamera))
        videoView.start()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        inflate = DataBindingUtil.inflate<FragmentCadastroBinding>(inflater, R.layout.fragment_cadastro, container, false)
        mMemory = Memory(audio = "Caminho audio",  dEscolhida =  "Selecione uma data")

        val root: View = inflate!!.root

        inflate!!.setVariable(BR.memory, mMemory)
        inflate!!.executePendingBindings()

        root.findViewById<Button>(R.id.btnAudio).setOnClickListener({it -> gravarAudio()})
        root.findViewById<Button>(R.id.btnData).setOnClickListener({it -> showDatePickerDialog()})
        root.findViewById<Button>(R.id.btnSalvar).setOnClickListener({it -> salvar()})
        root.findViewById<Button>(R.id.btnFoto).setOnClickListener({it -> tirarFoto()})
        root.findViewById<Button>(R.id.btnCamera).setOnClickListener({it -> gravarVideo()})
        root.findViewById<VideoView>(R.id.vView).setOnClickListener { it ->  playVideo(it) }
        return root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = AppDataBase.getDatabasesIns(this.context!!)
        memoryDAO = db?.memoryDAO()


    }

    fun salvar(){
        Observable.fromCallable({
            memoryDAO?.insert(mMemory!!)
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext({
                    Toast.makeText(this.context, "conteudo inserido com sucesso", Toast.LENGTH_LONG ).show()
                })
                .subscribe()


    }

    fun gravarAudio(){
        startActivityForResult( Intent(this.context, Record::class.java), REQUEST_AUDIO_RECORD)
    }

    fun tirarFoto(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            takePicture -> takePicture.resolveActivity(this.context!!.packageManager)?.also {

                var photoFile: File? = try {
                    createPath("JPEG", "jpg")
                }catch (ex: IOException){

                    null
                }

                photoFile?.also {
                    val photoURI: Uri? = FileProvider.getUriForFile(
                            this.context!!,
                            "com.example.android.fileprovider",
                            it
                    )
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    fun gravarVideo(){
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also {
            recordVideo -> recordVideo.resolveActivity(this.context!!.packageManager)?.also {

                var videoPath: File? = try {
                    createPath("record", "mp4")
                }catch (ex: IOException){
                    null
                }

                videoPath?.also {
                    var recordURI: Uri? = FileProvider.getUriForFile(
                            this.context!!,
                            "com.example.android.fileprovider",
                            it
                    )

                    recordVideo.putExtra(MediaStore.EXTRA_OUTPUT, recordURI)
                    startActivityForResult(recordVideo, REQUEST_VIDEO_CAPTURE)
                }
            }
        }
    }


    private fun setPic(){
        val targetW: Int = mImageView.width
        val targetH: Int = mImageView.height

        val bmOptions =  BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(mCurrentPath, this)
            val photoW = outWidth
            val protoH = outHeight

            val scaleFactory = Math.min(photoW/ targetW, protoH / targetH)

            inJustDecodeBounds = false
            inSampleSize = scaleFactory
            //inPurgeable = true


        }

        BitmapFactory.decodeFile(mCurrentPath, bmOptions)?.also { bitmap ->
            val ei = ExifInterface(mCurrentPath)
            val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED)
            var mBitmap = bitmap
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> {
                    mBitmap = rotate(bitmap, 90f)
                }
                ExifInterface.ORIENTATION_ROTATE_180 ->{
                    mBitmap = rotate(bitmap, 180f)
                }
                ExifInterface.ORIENTATION_ROTATE_270 -> {
                    mBitmap = rotate(bitmap, 270f)
                }
            }

            mImageView.setImageBitmap(mBitmap)
        }
    }

    fun rotate(source: Bitmap, angle: Float): Bitmap{
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            REQUEST_AUDIO_RECORD -> {
                this.mMemory?.audio = data?.getStringExtra("AUDIO")!!
                inflate!!.setVariable(BR.memory, mMemory)
                inflate!!.executePendingBindings()
            }
            REQUEST_IMAGE_CAPTURE -> {
               setPic()
            }
            REQUEST_VIDEO_CAPTURE -> {

            }
        }
    }

    fun showDatePickerDialog() {
        val newFragment = br.com.hermivaldo.rememberme.fragments.DatePicker()
        newFragment.show(this.activity!!.supportFragmentManager, "datePicker")
    }
}
