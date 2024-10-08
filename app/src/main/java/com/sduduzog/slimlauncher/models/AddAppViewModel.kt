package com.sduduzog.slimlauncher.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sduduzog.slimlauncher.data.model.App
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAppViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private var filterQuery = ""
    private var showAllApps = true
    private val _ignored_symbols_regex = Regex("[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/? ]")
    private val _installedApps = mutableListOf<App>()
    private val _homeApps = mutableListOf<App>()
    private val homeAppsObserver = Observer<List<HomeApp>> {
        this._homeApps.clear()
        it.forEach { item -> this._homeApps.add(App.from(item)) }
        updateDisplayedApps()
    }
    val apps = MutableLiveData<List<App>>()

    init {
        repository.apps.observeForever(homeAppsObserver)
    }

    fun setShowAllApps(bool: Boolean){
        showAllApps = bool
    }
    fun filterApps(query: String = "") {
        this.filterQuery = _ignored_symbols_regex.replace(query, "")
        this.updateDisplayedApps()
    }

    private fun updateDisplayedApps() {
        if(!showAllApps && filterQuery.equals("")){
            this.apps.postValue(listOf<App>())
            return
        }

        val filteredApps = _installedApps.filterNot { _homeApps.contains(it) }
        this.apps.postValue(filteredApps.filter {
            _ignored_symbols_regex.replace(it.appName, "").contains(filterQuery, ignoreCase = true)
        })
    }

    fun setInstalledApps(apps: List<App>) {
        this.filterQuery = ""
        this._installedApps.clear()
        this._installedApps.addAll(apps)
    }

    fun addAppToHomeScreen(app: App) {
        val index = _homeApps.size
        viewModelScope.launch(Dispatchers.IO) {
            repository.add(HomeApp.from(app, index))
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.apps.removeObserver(homeAppsObserver)
    }
}