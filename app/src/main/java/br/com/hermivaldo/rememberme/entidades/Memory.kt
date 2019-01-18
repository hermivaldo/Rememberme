package br.com.hermivaldo.rememberme.entidades


import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "memories")
data class Memory(@PrimaryKey var mID: String = UUID.randomUUID().toString(), var audio: String, var dEscolhida: String)