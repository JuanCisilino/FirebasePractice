package com.frost.firebasepractice

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(R.layout.activity_login) {

    private val GOOGLE_SIGN_IN = 100
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logEventAnalytics(getString(R.string.analytics_log_in), "InitLogin")
        setup()
        session()
        notification()
        remoteConfig()
    }

    private fun remoteConfig() {
        val configSettings: FirebaseRemoteConfigSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 60
        }
        val firebaseConfig = Firebase.remoteConfig
        firebaseConfig.setConfigSettingsAsync(configSettings)
        //Se setea por si no hay inet a la hora de iniciar
        firebaseConfig.setDefaultsAsync(mapOf("showLogOut" to false))
    }

    private fun notification() {
        FirebaseMessaging.getInstance().subscribeToTopic("Usuarios")
        intent.getStringExtra("url")?.let { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
    }

    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)
        if (email != null && provider != null){
            HomeActivity.start(this, email, ProviderType.valueOf(provider).name)
        }
    }

    private fun validate(task: Task<AuthResult>, provider: String){
        if (task.isSuccessful){
            HomeActivity.start(this, task.result?.user?.email?:"", provider)
        }else {
            showAlert()
        }
    }

    private fun setup() {
        title = getString(R.string.title_login)
        signUpButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passEditText.text.isNotEmpty()){
                createUser(emailEditText.text.toString(), passEditText.text.toString())
                    .addOnCompleteListener { validate(it, ProviderType.Basic.name) }
            }
        }
        logInButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passEditText.text.isNotEmpty()){
                signIn(emailEditText.text.toString(), passEditText.text.toString())
                    .addOnCompleteListener { validate(it, ProviderType.Basic.name) }
            }
        }
        googleButton.setOnClickListener {
            val googleConfig = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleClient = GoogleSignIn.getClient(this, googleConfig)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
        fbButton.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
            LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult?) {
                    result?.let {
                        signInWithCredential(FacebookAuthProvider.getCredential(it.accessToken.token))
                            .addOnCompleteListener {
                                if (it.isSuccessful){
                                    HomeActivity.start(this@LoginActivity, it.result.user?.email?:"", ProviderType.Facebook.name)
                                }else {
                                    showAlert()
                                }
                            }
                    }
                }
                override fun onCancel() {}
                override fun onError(error: FacebookException?) { showAlert() }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    signInWithCredential(GoogleAuthProvider.getCredential(it.idToken, null))
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                HomeActivity.start(this, account.email?:"", ProviderType.Google.name)
                            }else {
                                showAlert()
                            }
                        }
                }
            }catch (e: ApiException){
                showAlert()
            }
        }
    }


}
