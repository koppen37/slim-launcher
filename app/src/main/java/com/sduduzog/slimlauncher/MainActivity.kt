package com.sduduzog.slimlauncher

import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.preference.PreferenceManager
//import com.sduduzog.slimlauncher.di.MainFragmentFactoryEntryPoint
import com.sduduzog.slimlauncher.databinding.MainActivityBinding
import com.sduduzog.slimlauncher.utils.BaseFragment
import com.sduduzog.slimlauncher.utils.HomeWatcher
import com.sduduzog.slimlauncher.utils.IPublisher
import com.sduduzog.slimlauncher.utils.ISubscriber
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
        SharedPreferences.OnSharedPreferenceChangeListener,
        HomeWatcher.OnHomePressedListener, IPublisher {

    private lateinit var settings: SharedPreferences
    private lateinit var navigator: NavController
    private lateinit var homeWatcher: HomeWatcher
    private lateinit var binding: MainActivityBinding
    private val subscribers: MutableSet<BaseFragment> = mutableSetOf()

    override fun attachSubscriber(s: ISubscriber) {
        subscribers.add(s as BaseFragment)
    }

    override fun detachSubscriber(s: ISubscriber) {
        subscribers.remove(s as BaseFragment)
    }

    private fun dispatchHome() {
        for (s in subscribers) s.onHome()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settings = getSharedPreferences(getString(R.string.prefs_settings), MODE_PRIVATE)
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
        PreferenceManager.setDefaultValues(applicationContext, R.xml.options_fragment, true)
        PreferenceManager.setDefaultValues(applicationContext, R.xml.options_elements_fragment, true)


        navigator = findNavController(this, R.id.nav_host_fragment)
        homeWatcher = HomeWatcher(this)
        homeWatcher.setOnHomePressedListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, android.R.anim.fade_in, android.R.anim.fade_out)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        toggleStatusBar()
    }

    override fun onStart() {
        super.onStart()
        homeWatcher.startWatch()
    }

    override fun onStop() {
        super.onStop()
        homeWatcher.stopWatch()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) toggleStatusBar()
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, s: String?) {
        if (s.equals(getString(R.string.prefs_settings_key_theme), true)) {
            recreate()
        }
        if (s.equals(getString(R.string.prefs_settings_key_toggle_status_bar), true)) {
            toggleStatusBar()
        }
    }

    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()
        settings = PreferenceManager.getDefaultSharedPreferences(this)
        val active = settings.getString(getString(R.string.prefs_settings_key_theme), "0")!!

        theme.applyStyle(resolveTheme(Integer.parseInt(active)), true)
        return theme
    }

    override fun onHomePressed() {
        dispatchHome()
        navigator.popBackStack(R.id.homeFragment, false)
    }

    private fun toggleStatusBar() {
        val showBar = settings.getBoolean(getString(R.string.prefs_settings_key_toggle_status_bar), true)
        if(!showBar) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                //TODO: statusbar blijft zichtbaar...
                window.statusBarColor = Color.TRANSPARENT
                window.insetsController?.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                window.insetsController?.hide(WindowInsets.Type.statusBars())
//                WindowCompat.setDecorFitsSystemWindows(window, false)
            } else {
                @Suppress("DEPRECATION")
                val uiOptions = (View. SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View. SYSTEM_UI_FLAG_LAYOUT_STABLE)
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = uiOptions
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.show(WindowInsets.Type.statusBars())
                window.insetsController?.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_DEFAULT)
            } else {
                @Suppress("DEPRECATION")
                val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = uiOptions
            }
        }
    }

    companion object {

        fun resolveTheme(i: Int): Int {
            return when (i) {
                1 -> R.style.AppDarkTheme
                2 -> R.style.AppGreyTheme
                3 -> R.style.AppTealTheme
                4 -> R.style.AppCandyTheme
                5 -> R.style.AppPinkTheme
                else -> R.style.AppTheme
            }
        }
    }
}
