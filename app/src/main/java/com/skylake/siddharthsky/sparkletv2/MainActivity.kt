package com.skylake.siddharthsky.sparkletv2

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the site is up or down
        CheckSiteStatusTask().execute()
    }

    private inner class CheckSiteStatusTask : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            return isSiteReachable("localhost", 5004, 2000)
        }

        override fun onPostExecute(result: Boolean) {
            if (result) {
                showToast("Server is up ⬆️")
                showToast("Starting Sparkle-TV2")
                openApp("se.hedekonsult.sparkle")
                // Finish the current activity (close the main app)
                finish()
            } else {
                showToast("Server is down ⬇️")

                openApp("com.termux")

                // Wait for another 5.5 seconds before opening the second app
                Thread.sleep(5000)

                showToast("Starting Sparkle-TV2")

                openApp("se.hedekonsult.sparkle")
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

    private fun openApp(packageName: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            // If the app is not installed, open its page on the Google Play Store
            val playStoreIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$packageName")
            )
            startActivity(playStoreIntent)
            showToast("App not installed: $packageName")
        }
    }



    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }
}
