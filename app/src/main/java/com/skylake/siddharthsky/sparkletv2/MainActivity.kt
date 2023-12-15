package com.skylake.siddharthsky.sparkletv2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
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
                // If an app is saved, open it directly
                CheckSiteStatusTask().execute()
            } else {
                // If no app is saved, open the InstalledAppsFragment
                openInstalledAppsFragment()

            }
        }
    }


    private inner class CheckSiteStatusTask : AsyncTask<Void, Void, Boolean>() {

        // Wait for 1 seconds to check server status
        override fun doInBackground(vararg params: Void?): Boolean {
            return isSiteReachable("localhost", 5004, 100)
        }

        override fun onPostExecute(result: Boolean) {
            if (result) {
                showToast("Server is up ⬆️")
                showToast("Starting TV2")
                openSavedApp()
                // Finish the current activity (close the main app)
                finish()
            } else {
                showToast("Server is down ⬇️")

                openApp("com.termux")

                // Wait for 5.5 seconds before opening the second app
                Thread.sleep(5500)

                showToast("Starting TV2")

                openSavedApp()
                // Finish the current activity (close the main app)
                finish()
            }
        }
    }



    private fun isSiteReachable(host: String, port: Int, timeout: Int): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress(host, port), timeout)
            socket.close()
            true
        } catch (e: IOException) {
            false
        }
    }


    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }


    private fun isAppSaved(): Boolean {
        val savedAppName = sharedPreferences.getString("savedAPP", null)
        return !savedAppName.isNullOrEmpty()
    }

    private fun openSavedApp() {
        val savedApp= sharedPreferences.getString("savedAPP", null)
        savedApp?.let {
            openApp(it)
        }
    }

    private fun openInstalledAppsFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_browse_fragment, InstalledAppsFragment())
            .commitNow()
    }


    private fun openApp(pkgName: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(pkgName)
        println(pkgName)
        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            val playStoreIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$packageName")
            )
            startActivity(playStoreIntent)
        }
    }

}

