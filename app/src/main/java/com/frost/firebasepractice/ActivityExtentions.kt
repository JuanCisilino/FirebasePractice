package com.frost.firebasepractice

import android.app.Activity
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

fun Activity.logEvent(message: String, name:String){
    val analytics = FirebaseAnalytics.getInstance(this)
    val bundle = Bundle()
    bundle.putString("message", message)
    analytics.logEvent(name, bundle)
}