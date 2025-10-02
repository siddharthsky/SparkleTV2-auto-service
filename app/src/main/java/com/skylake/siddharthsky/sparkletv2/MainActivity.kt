package com.skylake.siddharthsky.sparkletv2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class MainActivity : FragmentActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        if (savedInstanceState == null) {
            if (isAppSaved()) {
                lifecycleScope.launch {
                    checkServerAndLaunchApps()
                }
            } else {
                openInstalledAppsFragment()
            }
        }
    }

    private suspend fun checkServerAndLaunchApps() = withContext(Dispatchers.IO) {
        val serverUp = isSiteReachable("localhost", 5001, 100)
        withContext(Dispatchers.Main) {
            if (serverUp) {
                showToast("Server is up ⬆️")
                showToast("Starting TV2")
                openSavedApp()
                finish()
            } else {
                showToast("Server is down ⬇️")
                openApp("com.termux")
                delay(5500)
                showToast("Starting TV2")
                openSavedApp()
                finish()
            }
        }
    }

    private fun isSiteReachable(host: String, port: Int, timeout: Int): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), timeout)
            }
            true
        } catch (e: IOException) {
            false
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun isAppSaved(): Boolean {
        return !sharedPreferences.getString("savedAPP", null).isNullOrEmpty()
    }

    private fun openSavedApp() {
        sharedPreferences.getString("savedAPP", null)?.let { openApp(it) }
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
