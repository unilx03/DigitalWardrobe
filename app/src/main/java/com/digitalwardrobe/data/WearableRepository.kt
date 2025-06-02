package com.digitalwardrobe.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WearableRepository(app: Application, private val dao: WearableDao) {
    val allWearables: LiveData<List<Wearable>> = dao.getAllWearables()

    suspend fun insert(wearable: Wearable) {
        dao.insert(wearable)
    }

    suspend fun delete(wearable: Wearable) {
        dao.delete(wearable)
    }
/*
    var wearableDao: WearableDao
    val listOfWearables: LiveData<List<Wearable>>

    init {
        val db = WearableRoomDatabase.getDatabase(app)
        //wearableDao = db.wearableDao()
        //listOfWearables = wearableDao.getListOfWearables()
    }

    fun getAllWearables(): LiveData<List<Wearable>> {

        /* RETROFIT BEGIN */

        val service: GetWearablesRemote =
            RetrofitClientInstance.getRetrofitInstance().create(GetWearablesRemote::class.java)
        val call: Call<List<Wearable>> = service.getAllWearables()
        call.enqueue(object : Callback<List<Wearable>> {

            override fun onResponse(
                call: Call<List<Wearable>>,
                response: Response<List<Wearable>>
            ) {
                WearableRoomDatabase.databaseWriteExecutor.execute {
                    response.body()?.let { wearableDao.insertMultiple(it) }
                }
            }

            override fun onFailure(call: Call<List<Wearable>>, t: Throwable?) {
                Log.e("RETROFIT", "something went wrong... but life goes on")
            }
        })

        /* RETROFIT END */

        return wearableDao.getListOfWearables()
    }

    fun insertTodo(todo: Wearable) {
        WearableRoomDatabase.databaseWriteExecutor.execute{
            wearableDao.insert(todo)
        }
    }*/
}