package br.com.hermivaldo.rememberme

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import br.com.hermivaldo.rememberme.adapter.LineMemoryAdap
import br.com.hermivaldo.rememberme.dao.AppDataBase
import br.com.hermivaldo.rememberme.dao.MemoryDAO
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_list.view.*

class ListAct : AppCompatActivity() {
    private var db: AppDataBase? = null
    private var memoryDAO: MemoryDAO? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                //message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                //message.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                //message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        // Definicao do layout manager, sem isso a lista nao é carregada
        my_list_memories.layoutManager = LinearLayoutManager(this)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        db = AppDataBase.getDatabasesIns(this)
        memoryDAO = db?.memoryDAO()

        Observable.fromCallable({
            memoryDAO?.getAll()
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    val memoryAdap = LineMemoryAdap(it!!)
                    my_list_memories.adapter = memoryAdap
                    my_list_memories.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
                }.subscribe()
    }
}
