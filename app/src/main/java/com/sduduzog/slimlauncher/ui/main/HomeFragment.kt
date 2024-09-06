package com.sduduzog.slimlauncher.ui.main


import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import com.sduduzog.slimlauncher.R
import com.sduduzog.slimlauncher.adapters.HomeAdapter
import com.sduduzog.slimlauncher.databinding.HomeFragmentBinding
import com.sduduzog.slimlauncher.models.MainViewModel
import com.sduduzog.slimlauncher.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private lateinit var settings : SharedPreferences
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding
    private val viewModel: MainViewModel by viewModels()

    private lateinit var receiver: BroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        val adapter1 = HomeAdapter(this)
        val adapter2 = HomeAdapter(this)
        binding!!.homeFragmentList.adapter = adapter1
        binding!!.homeFragmentListExp.adapter = adapter2

        settings = PreferenceManager.getDefaultSharedPreferences(requireContext())
        viewModel.apps.observe(viewLifecycleOwner) { list ->
            list?.let { apps ->
                adapter1.setItems(apps.filter {
                    it.sortingIndex < 4
                })
                adapter2.setItems(apps.filter {
                    it.sortingIndex >= 4
                })
            }
        }
//
        setEventListeners()
        // Maybe homeFragmentOptions..
        binding!!.homeFragmentOptions.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_optionsFragment))
        binding!!.homeFragmentSearch.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_searchFragment))

        return binding?.root
    }

    override fun onStart() {
        super.onStart()
        setViewVisibility()
        receiver = ClockReceiver()
        activity?.registerReceiver(receiver, IntentFilter(Intent.ACTION_TIME_TICK), Context.RECEIVER_EXPORTED)
    }

    override fun getFragmentView(): ViewGroup = binding!!.root

    override fun onResume() {
        super.onResume()
        updateClock()
    }

    override fun onStop() {
        super.onStop()
        activity?.unregisterReceiver(receiver)
    }

    private fun setEventListeners() {
        val timeIsShortcut  = settings.getBoolean(getString(R.string.prefs_settings_key_shortcut_time), false)
        val dateIsShortcut = settings.getBoolean(getString(R.string.prefs_settings_key_shortcut_date), false)

        if (timeIsShortcut) {
            binding!!.homeFragmentTime.setOnClickListener {
                try {
                    val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    launchActivity(it, intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    // Do nothing, we've failed :(
                }
            }
        }

        if (dateIsShortcut) {
            binding!!.homeFragmentDate.setOnClickListener {
                try {
                    val builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time")
                    val intent = Intent(Intent.ACTION_VIEW, builder.build())
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    launchActivity(it, intent)
                } catch (e: ActivityNotFoundException) {
                    // Do nothing, we've failed :(
                }
            }
        }

        binding!!.homeFragmentCall .setOnClickListener { view ->
            try {
                val pm = context?.packageManager!!
                val intent = Intent(Intent.ACTION_DIAL)
                val componentName = intent.resolveActivity(pm)
                if (componentName == null) launchActivity(view, intent) else
                    pm.getLaunchIntentForPackage(componentName.packageName)?.let {
                        launchActivity(view, it)
                    } ?: run { launchActivity(view, intent) }
            } catch (e: Exception) {
                // Do nothing
            }
        }

        binding!!.homeFragmentCamera .setOnClickListener {
            try {
                val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
                launchActivity(it, intent)
            } catch (e: Exception) {
                // Do nothing
            }
        }
    }

    fun updateClock() {
        val active =
            context?.getSharedPreferences(getString(R.string.prefs_settings), Context.MODE_PRIVATE)
                ?.getInt(getString(R.string.prefs_settings_key_time_format), 0)
        val date = Date()

        val currentLocale = Locale.getDefault()
        val fWatchTime = when(active) {
            1 -> SimpleDateFormat("H:mm", currentLocale)
            2 -> SimpleDateFormat("h:mm aa", currentLocale)
            else -> DateFormat.getTimeInstance(DateFormat.SHORT)
        }
        binding!!.homeFragmentTime .text = fWatchTime.format(date)


        val fWatchDate = SimpleDateFormat("EEE, MMM dd", currentLocale)
        binding!!.homeFragmentDate .text = fWatchDate.format(date)
    }

//    override fun onLaunch(app: HomeApp, view: View) {
//        try {
//            val manager = requireContext().getSystemService(Context.USER_SERVICE) as UserManager
//            val launcher =
//                requireContext().getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
//
//            val componentName = ComponentName(app.packageName, app.activityName)
//            val userHandle = manager.getUserForSerialNumber(app.userSerial)
//
//            launcher.startMainActivity(componentName, userHandle, view.clipBounds, null)
//        } catch (e: Exception) {
//            // Do no shit yet
//        }
//    }

    override fun onBack(): Boolean {
        binding!!.root.transitionToStart()
        return true
    }

    override fun onHome() {
        binding!!.root.transitionToEnd()
    }

    inner class ClockReceiver : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            updateClock()
        }
    }

    private fun setViewVisibility(){
        setVisibility(binding!!.homeFragmentTime, R.string.prefs_settings_key_toggle_time)
        setVisibility(binding!!.homeFragmentDate, R.string.prefs_settings_key_toggle_date)
        setVisibility(binding!!.homeFragmentCall, R.string.prefs_settings_key_toggle_call)
        setVisibility(binding!!.homeFragmentCamera, R.string.prefs_settings_key_toggle_camera)
        setVisibility(binding!!.homeFragmentSearch, R.string.prefs_settings_key_search_button)
    }

    private fun setVisibility(view : View, settingRef : Int){
        val showView = settings.getBoolean(getString(settingRef), true)
        view.visibility = if (showView) View.VISIBLE else View.INVISIBLE
    }
}
