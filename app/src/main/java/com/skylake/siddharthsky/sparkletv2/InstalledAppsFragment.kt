package com.skylake.siddharthsky.sparkletv2

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class InstalledAppsFragment : Fragment() {

    private lateinit var appListView: ListView
    private lateinit var packageManager: PackageManager
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_installed_apps, container, false)
        appListView = view.findViewById(R.id.appListView)
        packageManager = requireContext().packageManager
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val installedApps = getInstalledApps()
        val adapter = AppAdapter(requireContext(), installedApps)
        appListView.adapter = adapter

        appListView.setOnItemClickListener { _, _, position, _ ->
            val selectedApp = installedApps[position]
            val pkgName = selectedApp.packageName
            val appName = selectedApp.appName

            // Save
            saveAppNameToSharedPreferences(pkgName)


            // Show a toast notification
            Toast.makeText(requireContext(), "App saved: $appName", Toast.LENGTH_SHORT).show()

            //Debug Open app
            //openApp(selectedApp.packageName)

            // Close the entire app
            requireActivity().finish()

        }
    }

    private fun saveAppNameToSharedPreferences(appName: String) {
        val editor = sharedPreferences.edit()
        editor.putString("savedAPP", appName)
        editor.apply()
    }

    private fun openApp(packageName: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?.let {
            startActivity(it)
        }
    }

    private fun getInstalledApps(): List<AppInfo> {
        val apps = mutableListOf<AppInfo>()
        val packageList = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
        for (packageInfo in packageList) {
            val appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString()
            val appIcon = packageManager.getApplicationIcon(packageInfo.applicationInfo)
            val packageName = packageInfo.packageName
            apps.add(AppInfo(appName, appIcon, packageName))
        }
        return apps
    }

    private class AppAdapter(context: Context, apps: List<AppInfo>) :
        ArrayAdapter<AppInfo>(context, R.layout.item_installed_app, apps) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: View = convertView
                ?: LayoutInflater.from(context).inflate(R.layout.item_installed_app, parent, false)

            val appInfo = getItem(position)
            val appNameTextView: TextView = view.findViewById(R.id.appNameTextView)
            val appIconImageView: ImageView = view.findViewById(R.id.appIconImageView)

            appNameTextView.text = appInfo?.appName
            appIconImageView.setImageDrawable(appInfo?.appIcon)

            return view
        }
    }

    private data class AppInfo(val appName: String, val appIcon: Drawable, val packageName: String)
}
