package br.com.hermivaldo.rememberme.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.hermivaldo.rememberme.R
import br.com.hermivaldo.rememberme.entidades.Memory

class LineMemoryAdap(val listMemory: List<Memory>) : RecyclerView.Adapter<LineMemoryAdap.LineMemoryViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): LineMemoryViewHolder {
        return LineMemoryViewHolder(LayoutInflater.from(p0!!.context).inflate(
                R.layout.layout_list_memories, p0, false))
    }

    override fun getItemCount(): Int {
        return this.listMemory.size
    }

    override fun onBindViewHolder(p0: LineMemoryViewHolder, p1: Int) {
        var memory = listMemory.get(p1)
        p0.let {
            it.bindView(memory)
        }
    }

    class LineMemoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(memory: Memory){

            var evento = itemView.findViewById<TextView>(R.id.evento)
            evento.text = memory.dEscolhida
        }

    }

}