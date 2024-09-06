package com.sduduzog.slimlauncher.ui.main

import android.os.Bundle
import androidx.navigation.Navigation
import com.sduduzog.slimlauncher.data.model.App
import com.sduduzog.slimlauncher.databinding.AddAppFragmentBinding
import com.sduduzog.slimlauncher.ui.options.AddAppFragment


class SearchAppFragment : AddAppFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setShowAllApps(bool = false)
    }
    override fun onAppClicked(app: App) {
        onLaunch(app, getFragmentView())
        _popBackStack()
    }

}
