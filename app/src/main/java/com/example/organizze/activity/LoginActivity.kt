package com.example.organizze.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.organizze.R
import com.example.organizze.config.FirebaseConfiguration
import com.example.organizze.model.User
import com.google.firebase.auth.*
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editEmail = findViewById(R.id.editLoginName)
        editPassword = findViewById(R.id.editLoginPassword)
        buttonLogin = findViewById(R.id.buttonLogin)

        auth = FirebaseConfiguration.auth

        buttonLogin.setOnClickListener {
            // get user input data

            val email = editEmail.text.toString()
            val password = editPassword.text.toString()

            // verify if the fields are all filled
            if (email.isEmpty()){

                Toast.makeText(
                    LoginActivity@this,
                    "Preencha o email!",
                    Toast.LENGTH_SHORT
                ).show()

            }else if (password.isEmpty()) {

                Toast.makeText(
                    LoginActivity@this,
                    "Preencha a senha!",
                    Toast.LENGTH_SHORT
                ).show()

            }else {
                val user = User()
                user.email = email
                user.password = password
                validateLogin(user)
            }
        }

    }

    private fun validateLogin(user: User) {

         auth.signInWithEmailAndPassword(
             user.email.toString(),
             user.password.toString()
         ).addOnCompleteListener {

             if (it.isSuccessful) {

                 openMainAppActivity()

             }else {

                 var error = ""

                 try {
                     throw it.exception!!
                 }catch (e: FirebaseAuthInvalidCredentialsException) {
                     error = "E-mail ou senha errada!"
                 }catch (e: FirebaseAuthInvalidUserException) {
                     error = "E-mail ou senha errada!"
                 }catch (e: Exception) {
                     error = "Erro ao cadastrar usuário: ${e.message}"
                     e.printStackTrace()
                 }

                 Toast.makeText(
                     LoginActivity@this,
                     error,
                     Toast.LENGTH_SHORT
                 ).show()
             }

         }

    }

    private fun openMainAppActivity() {
        startActivity(Intent(this, MainAppActivity::class.java))
        finish()
    }

}