package com.example.organizze.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.organizze.R
import com.example.organizze.config.FirebaseConfiguration
import com.example.organizze.helper.Base64Custom
import com.example.organizze.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {

    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var buttonRegister: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        editName = findViewById(R.id.editName)
        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)

        auth = FirebaseConfiguration.auth

        buttonRegister = findViewById(R.id.buttonRegister)

        buttonRegister.setOnClickListener {

            // get user input data

            val name = editName.text.toString()
            val email = editEmail.text.toString()
            val password = editPassword.text.toString()

            // verify if the fields are all filled
            if(name.isEmpty()) {

                Toast.makeText(
                    RegisterActivity@this,
                    "Preencha o nome!",
                    Toast.LENGTH_SHORT
                ).show()

            }else if (email.isEmpty()){

                Toast.makeText(
                    RegisterActivity@this,
                    "Preencha o email!",
                    Toast.LENGTH_SHORT
                ).show()

            }else if (password.isEmpty()) {

                Toast.makeText(
                    RegisterActivity@this,
                    "Preencha a senha!",
                    Toast.LENGTH_SHORT
                ).show()

            }else {
                val user = User()
                user.name = name
                user.email = email
                user.password = password
                registerUser(user)
            }

        }

    }

    private fun registerUser(user: User) {

        auth.createUserWithEmailAndPassword(user.email.toString(), user.password.toString())
            .addOnCompleteListener {

            if(it.isSuccessful) {

                val userId = Base64Custom.encodeBase64(user.email.toString())
                user.id = userId
                Log.i("TEST_ID", user.toString())
                user.save()
                finish()

            }else {

                var error = ""

                try {

                    throw it.exception!!

                }catch (e: FirebaseAuthWeakPasswordException) {
                    error = "Digite uma senha mais forte"
                }catch (e: FirebaseAuthInvalidCredentialsException) {
                    error = "Digite um email valid"
                }catch (e: FirebaseAuthUserCollisionException) {
                    error = "Essa conta já foi cadastrada"
                }catch (e: Exception) {
                    error = "Erro ao cadastrar usuário: ${e.message}"
                    e.printStackTrace()
                }

                Toast.makeText(
                    RegisterActivity@this,
                    error,
                    Toast.LENGTH_SHORT
                ).show()

            }

        }

    }

}