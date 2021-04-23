package com.sduduzog.slimlauncher.ui.main

import android.os.Bundle
import com.sduduzog.slimlauncher.data.model.App
import com.sduduzog.slimlauncher.ui.options.AddAppFragment
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.add_app_fragment.*


class SearchAppFragment : AddAppFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setShowAllApps(bool = false)
    }
    override fun onAppClicked(app: App) {
        onLaunch(app, getFragmentView())

        inputMethodManager.hideSoftInputFromWindow(add_app_fragment_edit_text.windowToken, 0)
        Navigation.findNavController(add_app_fragment).popBackStack()
    }

}