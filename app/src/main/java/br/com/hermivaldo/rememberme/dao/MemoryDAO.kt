package br.com.hermivaldo.rememberme.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import br.com.hermivaldo.rememberme.entidades.Memory

@Dao
interface MemoryDAO{

    @Query("select * from memories")
    fun getAll() : List<Memory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(memory: Memory)

}