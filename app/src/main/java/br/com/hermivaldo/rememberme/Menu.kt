package br.com.hermivaldo.rememberme

import android.app.DatePickerDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import br.com.hermivaldo.rememberme.dao.AppDataBase
import br.com.hermivaldo.rememberme.dao.MemoryDAO
import br.com.hermivaldo.rememberme.databinding.ActivityMenuBinding
import br.com.hermivaldo.rememberme.entidades.Memory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_menu.*

private const val REQUEST_AUDIO_RECORD = 200

class Menu : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private var db: AppDataBase? = null
    private var memoryDAO: MemoryDAO? = null
    private var mMemory: Memory? = null

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        dEscolhida.text = "" + dayOfMonth+"/" + (month + 1) + "/" + year
        this.mMemory?.dEscolhida =  dEscolhida.text.toString()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMemory = Memory(audio = "Caminho audio",  dEscolhida =  "Selecione uma data")
        val mainBinding: ActivityMenuBinding = DataBindingUtil.setContentView(this, R.layout.activity_menu)

        mainBinding.setVariable(BR.memory, mMemory)
        mainBinding.executePendingBindings()

        db = AppDataBase.getDatabasesIns(this)
        memoryDAO = db?.memoryDAO()

    }

    fun salvar(view: View){

        Observable.fromCallable({
            memoryDAO?.insert(mMemory!!)
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext({
                    Toast.makeText(this, "conteudo inserido com sucesso", Toast.LENGTH_LONG ).show()
                })
                .subscribe()


    }

    fun gravarAudio(view: View){
        startActivityForResult( Intent(this, Record::class.java), REQUEST_AUDIO_RECORD)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            REQUEST_AUDIO_RECORD -> {
                this.mMemory?.audio = data?.getStringExtra("AUDIO")!!
                cAudio.text = this.mMemory?.audio
            }

        }
    }

    fun showDatePickerDialog(v: View) {
        val newFragment = br.com.hermivaldo.rememberme.fragments.DatePicker()
        newFragment.show(supportFragmentManager, "datePicker")
    }

}