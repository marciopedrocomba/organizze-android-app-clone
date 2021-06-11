package com.example.organizze.model

import com.example.organizze.config.FirebaseConfiguration
import com.example.organizze.helper.Base64Custom
import com.example.organizze.helper.DateCustom

data class Movement(
    var key: String?,
    var date: String?,
    var category: String?,
    var description: String?,
    var type: String?,
    var value: Double?
) {

    constructor(): this(null,null, null, null, null, null)

    fun save() {

        val db = FirebaseConfiguration.db
        val auth = FirebaseConfiguration.auth

        val monthYear = DateCustom.monthYearChosen(this.date.toString())

        val id = Base64Custom.encodeBase64(auth.currentUser?.email.toString())

        db.child("movements")
            .child(id.trim())
            .child(monthYear)
            .push()
            .setValue(this)
    }

}