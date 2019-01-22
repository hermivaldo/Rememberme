package br.com.hermivaldo.rememberme.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import br.com.hermivaldo.rememberme.R
import br.com.hermivaldo.rememberme.adapter.LineMemoryAdap
import br.com.hermivaldo.rememberme.dao.AppDataBase
import br.com.hermivaldo.rememberme.dao.MemoryDAO
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ListMemory : Fragment() {

    private var db: AppDataBase? = null
    private var memoryDAO: MemoryDAO? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.fragment_list_memory, container, false)

        db = AppDataBase.getDatabasesIns(this.requireContext())
        memoryDAO = db?.memoryDAO()

        Observable.fromCallable({
            memoryDAO?.getAll()
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    val memoryAdap = LineMemoryAdap(it!!)
                    inflate.findViewById<RecyclerView>(R.id.my_list_of_memories).layoutManager = LinearLayoutManager(this.requireContext())
                    inflate.findViewById<RecyclerView>(R.id.my_list_of_memories).adapter = memoryAdap
                    inflate.findViewById<RecyclerView>(R.id.my_list_of_memories).addItemDecoration(DividerItemDecoration(
                            this.requireContext(), DividerItemDecoration.VERTICAL))
                }.subscribe()
        return inflate
    }
}
