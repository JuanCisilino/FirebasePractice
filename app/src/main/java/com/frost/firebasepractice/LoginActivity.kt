package com.frost.firebasepractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(R.layout.activity_login) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logEventAnalytics("Inicio pantalla Login", "InitLogin")
        setup()
    }

    private fun setup() {
        title = "Authentication"
        signUpButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passEditText.text.isNotEmpty()){
                createUser(emailEditText.text.toString(), passEditText.text.toString())
                    .addOnCompleteListener {
                    if (it.isSuccessful){
                        HomeActivity.start(this, it.result?.user?.email?:"", ProviderType.Basic.name)
                    }else {
                        showAlert("Error", "Se ha producido un error autenticando al usuario", "Aceptar")
                    }
                }
            }
        }
        logInButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passEditText.text.isNotEmpty()){
                signIn(emailEditText.text.toString(), passEditText.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            HomeActivity.start(this, it.result?.user?.email?:"", ProviderType.Basic.name)
                        }else {
                            showAlert("Error", "Se ha producido un error autenticando al usuario", "Aceptar")
                        }
                    }
            }
        }
    }



}
