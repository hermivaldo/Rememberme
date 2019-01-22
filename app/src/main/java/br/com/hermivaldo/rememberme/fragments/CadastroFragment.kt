package br.com.hermivaldo.rememberme.fragments


import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast

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

private const val REQUEST_AUDIO_RECORD = 200

class CadastroFragment : Fragment(), OnDateSetListener{

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
       this.mMemory?.dEscolhida = ""+ dayOfMonth + "/" + (month + 1) + "/" + year

       inflate!!.setVariable(BR.memory, mMemory)
       inflate!!.executePendingBindings()
    }

    private var db: AppDataBase? = null
    private var memoryDAO: MemoryDAO? = null
    private var mMemory: Memory? = null
    private var inflate : FragmentCadastroBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        inflate = DataBindingUtil.inflate<FragmentCadastroBinding>(inflater, R.layout.fragment_cadastro, container, false)
        mMemory = Memory(audio = "Caminho audio",  dEscolhida =  "Selecione uma data")

        val root: View = inflate!!.root

        inflate!!.setVariable(BR.memory, mMemory)
        inflate!!.executePendingBindings()

        root.findViewById<Button>(R.id.btnAudio).setOnClickListener({it -> gravarAudio(it)})
        root.findViewById<Button>(R.id.btnData).setOnClickListener({it -> showDatePickerDialog(it)})
        root.findViewById<Button>(R.id.btnSalvar).setOnClickListener({it -> salvar(it)})
        return root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = AppDataBase.getDatabasesIns(this.context!!)
        memoryDAO = db?.memoryDAO()


    }

    fun salvar(view: View){
        Observable.fromCallable({
            memoryDAO?.insert(mMemory!!)
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext({
                    Toast.makeText(this.context, "conteudo inserido com sucesso", Toast.LENGTH_LONG ).show()
                })
                .subscribe()


    }

    fun gravarAudio(view: View){
        startActivityForResult( Intent(this.context, Record::class.java), REQUEST_AUDIO_RECORD)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            REQUEST_AUDIO_RECORD -> {
                this.mMemory?.audio = data?.getStringExtra("AUDIO")!!
                inflate!!.setVariable(BR.memory, mMemory)
                inflate!!.executePendingBindings()
            }
        }
    }

    fun showDatePickerDialog(v: View) {
        val newFragment = br.com.hermivaldo.rememberme.fragments.DatePicker()
        newFragment.show(this.activity!!.supportFragmentManager, "datePicker")
    }
}
