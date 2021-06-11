package com.example.organizze.config

import com.example.organizze.config.FirebaseConfiguration.Companion.db
import com.example.organizze.helper.Base64Custom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class FirebaseConfiguration {

    companion object {
        val auth: FirebaseAuth = Firebase.auth
        val db: DatabaseReference = FirebaseDatabase.getInstance().reference
        fun authenticatedUserRef(): DatabaseReference {
            val userEmail = auth.currentUser?.email
            val userId = Base64Custom.encodeBase64(userEmail.toString())
            return db.child("users").child(userId)
        }
    }
}