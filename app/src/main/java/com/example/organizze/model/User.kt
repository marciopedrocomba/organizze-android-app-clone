package com.example.organizze.model

import com.example.organizze.config.FirebaseConfiguration
import com.google.firebase.database.Exclude

data class User(

    @Exclude
    var id: String?,

    var name: String?,
    var email: String?,

    @Exclude
    var password: String?,

    var totalRecipe: Double,
    var totalExpense: Double

) {

    constructor():
            this(null, null, null, null, 0.0, 0.0)

    fun save() {

        val db = FirebaseConfiguration.db

        val user = User()
        user.name = this.name.toString()
        user.email = this.email.toString()

        db.child("users")
          .child(this.id.toString().trim()).setValue(user)
    }

}