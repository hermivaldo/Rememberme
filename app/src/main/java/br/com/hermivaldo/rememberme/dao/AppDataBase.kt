package br.com.hermivaldo.rememberme.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import br.com.hermivaldo.rememberme.entidades.Memory

@Database(entities = arrayOf(Memory::class), version = 1)
abstract class AppDataBase : RoomDatabase(){

    abstract fun memoryDAO(): MemoryDAO

    companion object {
        var INSTANCE: AppDataBase? = null

        fun getDatabasesIns(context: Context) : AppDataBase? {
            if (INSTANCE == null){
                synchronized(AppDataBase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            AppDataBase::class.java, "rememberDb").build()
                }
            }

            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }

}