package com.frost.firebasepractice

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.facebook.login.LoginManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.android.synthetic.main.activity_home.*

enum class ProviderType { Basic, Google, Facebook }

class HomeActivity : AppCompatActivity(R.layout.activity_home) {

    companion object {
        const val emailKey = "emailKey"
        const val providerKey = "providerKey"

        fun start(context: Context, email: String, providerType: String) {
            val intent = Intent(context, HomeActivity::class.java).apply {
                putExtra(emailKey, email)
                putExtra(providerKey, providerType)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logEventAnalytics(getString(R.string.analytics_log_home), "InitHome")
        val bundle = intent.extras
        val email = bundle?.getString(emailKey)?:""
        val provider = bundle?.getString(providerKey)?:""
        setup(email, provider)
        saveData(email, provider)
        remoteConfig()
    }

    private fun remoteConfig(){
        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful){
                val showLogOut = Firebase.remoteConfig.getBoolean("showLogOut")
                if (showLogOut) logOutButton.text = "Log Out"
            }
        }
    }

    private fun saveData(email: String, provider: String) {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()
    }

    private fun clearData(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()
    }

    private fun setup(email: String, provider: String) {
        title = getString(R.string.title_home)
        emailText.text = email
        passText.text = provider
        logOutButton.setOnClickListener {
            if (provider == ProviderType.Facebook.name) LoginManager.getInstance().logOut()
            logOut()
            clearData()
            logEventAnalytics(getString(R.string.analytics_log_out), "LogOut")
            onBackPressed()
        }
    }
}
