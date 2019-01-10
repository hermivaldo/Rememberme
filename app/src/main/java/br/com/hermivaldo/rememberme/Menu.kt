package br.com.hermivaldo.rememberme

import android.app.DatePickerDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import br.com.hermivaldo.rememberme.entidades.Memory

private const val REQUEST_AUDIO_RECORD = 200

class Menu : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        Toast.makeText(this, ""+ year, Toast.LENGTH_LONG).show()
        dEscolhida.text = "" + dayOfMonth+"/" + month + "/" + year
        this.mMemory?.dEscolhida =  dEscolhida.text.toString()

    }

    private var mMemory: Memory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        mMemory = Memory()

    }

    fun gravarAudio(view: View){
        var intent: Intent = Intent(this, Record::class.java)
        startActivityForResult(intent, REQUEST_AUDIO_RECORD)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            REQUEST_AUDIO_RECORD -> {
                this.mMemory?.audio = data?.getStringExtra("AUDIO")
                cAudio.text = this.mMemory?.audio
            }

        }
    }

    fun showDatePickerDialog(v: View) {
        val newFragment = br.com.hermivaldo.rememberme.fragments.DatePicker()
        newFragment.show(supportFragmentManager, "datePicker")
    }

}