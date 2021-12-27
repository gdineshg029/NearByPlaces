package com.java.nearbyplaces.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils
import android.view.View
import com.google.android.material.snackbar.Snackbar


fun showSnackbar(
    view: View, message: String, buttonTitle: String,
    listner: View.OnClickListener
){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction(buttonTitle, listner).show()
    }

fun Context.showAppPermissionSettings(){
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri: Uri = Uri.fromParts("package", this.getPackageName(), null)
    intent.data = uri
    startActivity(intent)
}

fun Context.isLocationEnabled(): Boolean {
    var locationMode = 0
    val locationProviders: String
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        try {
            locationMode =
                Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE)
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }
        locationMode != Settings.Secure.LOCATION_MODE_OFF
    } else {
        locationProviders =
            Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_MODE)
        !TextUtils.isEmpty(locationProviders)
    }
}

fun View.updateVisiblity(visible: Boolean){
    this.visibility = if(visible)
                         View.VISIBLE
                      else
                          View.GONE
}