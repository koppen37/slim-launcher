package com.sduduzog.slimlauncher.ui.options

import android.content.Context
import android.content.pm.LauncherApps
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.os.UserManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.sduduzog.slimlauncher.adapters.AddAppAdapter
import com.sduduzog.slimlauncher.data.model.App
import com.sduduzog.slimlauncher.databinding.AddAppFragmentBinding
import com.sduduzog.slimlauncher.models.AddAppViewModel
import com.sduduzog.slimlauncher.utils.BaseFragment
import com.sduduzog.slimlauncher.utils.OnAppClickedListener
import dagger.hilt.android.AndroidEntryPoint

//import kotlinx.android.synthetic.main.add_app_fragment.*

@AndroidEntryPoint
open class AddAppFragment : BaseFragment(), OnAppClickedListener {

    private var _binding: AddAppFragmentBinding? = null
    private val binding get() = _binding
    override fun getFragmentView(): ViewGroup = binding!!.root

    open val viewModel: AddAppViewModel by viewModels()

    fun _show_keyboard() {
        binding!!.addAppFragmentEditText.requestFocus()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.show(WindowInsetsCompat.Type.ime())
        } else {
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(
                binding!!.addAppFragmentEditText,
                InputMethodManager.SHOW_IMPLICIT
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = AddAppFragmentBinding.inflate(inflater, container, false)
        val adapter = AddAppAdapter(this)

        binding!!.addAppFragmentList.adapter = adapter
        _show_keyboard()
        viewModel.apps.observe(viewLifecycleOwner) {
            it?.let { apps ->
                adapter.setItems(apps)
                binding!!.addAppFragmentProgressBar.visibility = View.GONE
            } ?: run {
               binding!!.addAppFragmentProgressBar.visibility = View.VISIBLE
            }
        }
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.setInstalledApps(getInstalledApps())
        viewModel.filterApps("")
        binding!!.addAppFragmentEditText.addTextChangedListener(onTextChangeListener)
        _show_keyboard()
    }

    override fun onPause() {
        super.onPause()
        binding!!.addAppFragmentEditText.removeTextChangedListener(onTextChangeListener)
    }

    private val onTextChangeListener: TextWatcher = object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            // Do nothing
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Do nothing
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            viewModel.filterApps(s.toString())
        }
    }

    override fun onAppClicked(app: App) {
        viewModel.addAppToHomeScreen(app)
        _popBackStack()
    }

    fun _popBackStack() {
        Navigation.findNavController(binding!!.root).popBackStack()
    }

    private fun getInstalledApps(): List<App> {
        val list = mutableListOf<App>()

        val manager = requireContext().getSystemService(Context.USER_SERVICE) as UserManager
        val launcher = requireContext().getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        val myUserHandle = Process.myUserHandle()

        for (profile in manager.userProfiles) {
            val prefix = if (profile.equals(myUserHandle)) "" else "\uD83C\uDD46 " //Unicode for boxed w
            val profileSerial = manager.getSerialNumberForUser(profile)

            for (activityInfo in launcher.getActivityList(null, profile)) {
                val app = App(
                        appName = prefix + activityInfo.label.toString(),
                        packageName = activityInfo.applicationInfo.packageName,
                        activityName = activityInfo.name,
                        userSerial = profileSerial
                )
                list.add(app)
            }
        }

        list.sortBy{it.appName}

        val filter = mutableListOf<String>()
        filter.add(requireContext().packageName)
        return list.filterNot { filter.contains(it.packageName) }
    }
}