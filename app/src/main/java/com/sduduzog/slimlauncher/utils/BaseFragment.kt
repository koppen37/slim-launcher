package com.sduduzog.slimlauncher.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.os.Build
import android.os.UserManager
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.sduduzog.slimlauncher.R
import com.sduduzog.slimlauncher.data.model.App
import com.sduduzog.slimlauncher.models.HomeApp

abstract class BaseFragment : Fragment(), ISubscriber {

    abstract fun getFragmentView(): ViewGroup


    override fun onResume() {
        super.onResume()
        val settings = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val active = Integer.parseInt(settings.getString(getString(R.string.prefs_settings_key_theme), "0")!!)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (active) {
                0, 3, 5 -> {
                    val flags = requireActivity().window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    getFragmentView().systemUiVisibility = flags
                }

            }
            val value = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.colorPrimary, value, true)
            requireActivity().window.statusBarColor = value.data
        }

    }

    override fun onStart() {
        super.onStart()
        with(activity as IPublisher) {
            this.attachSubscriber(this@BaseFragment)
        }
    }

    override fun onStop() {
        super.onStop()
        with(activity as IPublisher) {
            this.detachSubscriber(this@BaseFragment)
        }
    }

    fun onLaunch(app: App, view: View){
        try {
            val manager = requireContext().getSystemService(Context.USER_SERVICE) as UserManager
            val launcher = requireContext().getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

            val componentName = ComponentName(app.packageName, app.activityName)
            val userHandle = manager.getUserForSerialNumber(app.userSerial)

            launcher.startMainActivity(componentName, userHandle, view.clipBounds, null)
        } catch (e: Exception) {
            // Do no shit yet
        }
    }

    fun onLaunch(app: HomeApp, view: View) {
        onLaunch(App.from(app), view)
    }

    protected fun launchActivity(view: View, intent: Intent) {
        val left = 0
        val top = 0
        val width = view.measuredWidth
        val height = view.measuredHeight
        val opts = ActivityOptionsCompat.makeClipRevealAnimation(view, left, top, width, height)
        startActivity(intent, opts.toBundle())
    }

    open fun onBack(): Boolean = false

    open fun onHome() {}
}
