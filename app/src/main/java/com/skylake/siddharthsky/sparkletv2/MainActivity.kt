package com.skylake.siddharthsky.sparkletv2

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        if (areAppsSaved()) {
            openSavedApps()
        } else {
            openInstalledAppsFragment()
        }
    }

    private fun areAppsSaved(): Boolean {
        val firstApp = sharedPreferences!!.getString("firstApp", null)
        val secondApp = sharedPreferences!!.getString("secondApp", null)
        return firstApp != null && secondApp != null
    }

    private fun openSavedApps() {
        val firstApp = sharedPreferences!!.getString("firstApp", null)
        val secondApp = sharedPreferences!!.getString("secondApp", null)
        val delay = sharedPreferences!!.getInt("delay", 2000)

        if (firstApp != null && secondApp != null) {
            openApp(firstApp)
            Handler().postDelayed({ openApp(secondApp) }, delay.toLong())
        }
    }

    private fun openInstalledAppsFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_browse_fragment, InstalledAppsFragment())
            .commitNow()
    }

    private fun openApp(pkgName: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(pkgName)
        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            val playStoreIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$pkgName")
            )
            startActivity(playStoreIntent)
        }
    }
}