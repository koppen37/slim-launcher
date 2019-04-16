package com.sduduzog.slimlauncher.data

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData

class AppRepository(application: Application) {
    private val db: DataRoomDatabase = DataRoomDatabase.getDatabase(application)!!
    private val appDao: AppDao = db.appDao()

    private val _apps = appDao.homeApps

    val apps: LiveData<List<HomeApp>>
        get() = _apps

    fun add(app: HomeApp){
        AddAppAsyncTask(appDao).execute(app)
    }

    fun update(vararg list: HomeApp) {
        UpdateAppAsyncTask(appDao).execute(*list)
    }


    private class AddAppAsyncTask(private val mAsyncTaskDao: AppDao) : AsyncTask<HomeApp, Void, Void>() {

        override fun doInBackground(vararg params: HomeApp): Void? {
            mAsyncTaskDao.add(params[0])
            return null
        }
    }

    private class UpdateAppAsyncTask(private val mAsyncTaskDao: AppDao) : AsyncTask<HomeApp, Void, Void>() {

        override fun doInBackground(vararg params: HomeApp): Void? {
            mAsyncTaskDao.update(*params)
            return null
        }
    }
}