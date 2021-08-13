package com.frost.firebasepractice

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

fun Activity.logEventAnalytics(message: String, name:String){
    val analytics = FirebaseAnalytics.getInstance(this)
    val bundle = Bundle()
    bundle.putString("message", message)
    analytics.logEvent(name, bundle)
}

fun Activity.createUser(email: String, pass: String)=
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)

fun Activity.signIn(email: String, pass: String)=
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)

fun Activity.logOut()= FirebaseAuth.getInstance().signOut()

fun Activity.showAlert(title: String, message: String, positiveButton: String){
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton(positiveButton, null)
    val dialog = builder.create()
    dialog.show()
}
