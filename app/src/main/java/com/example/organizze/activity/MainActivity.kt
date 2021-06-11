package com.example.organizze.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.organizze.R
import com.example.organizze.config.FirebaseConfiguration
import com.google.firebase.auth.FirebaseAuth
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide

class MainActivity : IntroActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        isButtonBackVisible = false
        isButtonNextVisible = false

        addSlide(FragmentSlide.Builder()
            .background(android.R.color.white)
            .fragment(R.layout.intro_1)
            .build()
        )

        addSlide(FragmentSlide.Builder()
            .background(android.R.color.white)
            .fragment(R.layout.intro_2)
            .build()
        )

        addSlide(FragmentSlide.Builder()
            .background(android.R.color.white)
            .fragment(R.layout.intro_3)
            .build()
        )

        addSlide(FragmentSlide.Builder()
            .background(android.R.color.white)
            .fragment(R.layout.intro_4)
            .build()
        )

        addSlide(FragmentSlide.Builder()
            .background(android.R.color.white)
            .fragment(R.layout.intro_register)
            .canGoForward(false)
            .build()
        )


    }

    override fun onStart() {
        super.onStart()
        verifyAuthenticatedUser()
    }

    fun btnEnter(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun btnRegister(view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    private fun verifyAuthenticatedUser() {
        auth = FirebaseConfiguration.auth
        if(auth.currentUser != null) {
            openMainAppActivity()
        }
    }

    private fun openMainAppActivity() {
        startActivity(Intent(this, MainAppActivity::class.java))
    }

}