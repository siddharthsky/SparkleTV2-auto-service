package com.skylake.siddharthsky.sparkletv2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class InstalledAppsFragment : Fragment() {
    private var appListView: ListView? = null
    private var packageManager: PackageManager? = null
    private var sharedPreferences: SharedPreferences? = null
    private var firstSelectedApp: String? = null
    private var delayInput: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_installed_apps, container, false)
        appListView = view.findViewById(R.id.appListView)
        delayInput = view.findViewById<EditText>(R.id.delayInput)
        packageManager = requireContext().packageManager
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val installedApps =
            installedApps
        val adapter = AppAdapter(requireContext(), installedApps)
        appListView!!.adapter = adapter

        appListView!!.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>?, view1: View?, position: Int, id: Long ->
                val selectedApp =
                    installedApps[position]
                val pkgName = selectedApp.packageName
                val appName = selectedApp.appName
                if (firstSelectedApp == null) {
                    firstSelectedApp = pkgName
                    Toast.makeText(
                        requireContext(),
                        "First app selected: $appName",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    var delay = try {
                        delayInput!!.text.toString().toInt() * 1000
                    } catch (e: NumberFormatException) {
                        2000 // Default to 2 seconds
                    }
                    saveAppsToSharedPreferences(firstSelectedApp!!, pkgName, delay)
                    Toast.makeText(
                        requireContext(),
                        "Second app selected: $appName",
                        Toast.LENGTH_SHORT
                    ).show()
                    launchMainActivity()
                    requireActivity().finish()
                }
            }
    }

    private fun saveAppsToSharedPreferences(firstApp: String, secondApp: String, delay: Int) {
        val editor = sharedPreferences!!.edit()
        editor.putString("firstApp", firstApp)
        editor.putString("secondApp", secondApp)
        editor.putInt("delay", delay)
        editor.apply()
    }

    private val installedApps: List<AppInfo>
        get() {
            val apps: MutableList<AppInfo> = ArrayList()
            val packageList = packageManager!!.getInstalledPackages(PackageManager.GET_META_DATA)
            for (packageInfo in packageList) {
                val appName =
                    packageManager!!.getApplicationLabel(packageInfo.applicationInfo).toString()
                val appIcon = packageManager!!.getApplicationIcon(packageInfo.applicationInfo)
                val packageName = packageInfo.packageName
                apps.add(AppInfo(appName, appIcon, packageName))
            }

            apps.sortWith(java.util.Comparator { app1: AppInfo, app2: AppInfo ->
                app1.appName.compareTo(
                    app2.appName
                )
            })
            return apps
        }

    private fun launchMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }

    private class AppAdapter(context: Context?, apps: List<AppInfo>?) :
        ArrayAdapter<AppInfo?>(
            context!!, R.layout.item_installed_app,
            apps!!
        ) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView
                ?: LayoutInflater.from(
                    context
                ).inflate(R.layout.item_installed_app, parent, false)

            val appInfo = getItem(position)
            val appNameTextView = view.findViewById<TextView>(R.id.appNameTextView)
            val appIconImageView = view.findViewById<ImageView>(R.id.appIconImageView)

            appNameTextView.text = appInfo?.appName ?: ""
            appIconImageView.setImageDrawable(appInfo?.appIcon)

            return view
        }
    }

    private class AppInfo(val appName: String, val appIcon: Drawable, val packageName: String)
}