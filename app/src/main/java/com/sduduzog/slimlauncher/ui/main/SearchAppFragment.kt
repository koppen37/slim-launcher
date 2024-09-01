package com.sduduzog.slimlauncher.ui.main

import android.os.Bundle
import androidx.navigation.NavController
import com.sduduzog.slimlauncher.data.model.App
import com.sduduzog.slimlauncher.ui.options.AddAppFragment
import androidx.navigation.Navigation
//import com.sduduzog.slimlauncher.databinding.HomeFragmentBinding
//import kotlinx.android.synthetic.main.add_app_fragment.*
import com.sduduzog.slimlauncher.databinding.AddAppFragmentBinding


class SearchAppFragment : AddAppFragment() {
    private var _binding: AddAppFragmentBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        viewModel.setShowAllApps(bool = false)
    }
    override fun onAppClicked(app: App) {
        onLaunch(app, getFragmentView())
        parentFragmentManager.popBackStack()
    }

}
