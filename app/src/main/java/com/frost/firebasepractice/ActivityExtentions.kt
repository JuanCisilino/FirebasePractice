package com.frost.firebasepractice

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics

fun Activity.logEventAnalytics(message: String, name:String){
    val analytics = FirebaseAnalytics.getInstance(this)
    val bundle = Bundle()
    bundle.putString("message", message)
    analytics.logEvent(name, bundle)
}

fun createUser(email: String, pass: String)=
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)

fun signIn(email: String, pass: String)=
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)

fun logOut()= FirebaseAuth.getInstance().signOut()

fun signInWithCredential(credential: AuthCredential) =
    FirebaseAuth.getInstance().signInWithCredential(credential)

fun Activity.showAlert(){
    FirebaseCrashlytics.getInstance().log("ShowAlert()")
    val builder = AlertDialog.Builder(this)
    builder.setTitle("Error")
    builder.setMessage("Se ha producido un error autenticando al usuario")
    builder.setPositiveButton("Aceptar", null)
    val dialog = builder.create()
    dialog.show()
}
