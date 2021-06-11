package com.example.organizze.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.example.organizze.R
import com.example.organizze.config.FirebaseConfiguration
import com.example.organizze.helper.Base64Custom
import com.example.organizze.helper.DateCustom
import com.example.organizze.model.Movement
import com.example.organizze.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class RecipesActivity : AppCompatActivity() {

    private lateinit var editDate: TextInputEditText
    private lateinit var editCategory: TextInputEditText
    private lateinit var editDescription: TextInputEditText
    private lateinit var editValue: EditText

    private var totalRecipe = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes)

        editDate = findViewById(R.id.editRecipesDate)
        editCategory = findViewById(R.id.editRecipesCategory)
        editDescription = findViewById(R.id.editRecipesDescription)
        editValue = findViewById(R.id.editRecipesValue)

        // fill the date field with the actual date
        editDate.setText(DateCustom.actualDate())

        getTotalRecipe()

        val buttonSave: FloatingActionButton = findViewById(R.id.fabRecipesSave)

        buttonSave.setOnClickListener {
            saveRecipe()
        }

    }

    fun saveRecipe() {

        if(validateFields()) {

            val value = editValue.text.toString().toDouble()

            val movement = Movement()

            movement.value = value
            movement.category = editCategory.text.toString()
            movement.description = editDescription.text.toString()
            movement.date = editDate.text.toString()
            movement.type = "recipe"
            movement.save()

            val updatedRecipe = totalRecipe + value

            updateRecipe(updatedRecipe)

            finish()

        }

    }

    fun validateFields(): Boolean {

        val value = editValue.text.toString()
        val date = editDate.text.toString()
        val category = editCategory.text.toString()
        val description = editDescription.text.toString()

        // verify if the fields are all filled
        if(value.isEmpty()) {

            Toast.makeText(
                RecipesActivity@this,
                "Preencha o valor!",
                Toast.LENGTH_SHORT
            ).show()

            return false

        }else if (date.isEmpty()){

            Toast.makeText(
                RecipesActivity@this,
                "Preencha a Data!",
                Toast.LENGTH_SHORT
            ).show()

            return false

        }else if (category.isEmpty()) {

            Toast.makeText(
                RecipesActivity@this,
                "Preencha a Categoria!",
                Toast.LENGTH_SHORT
            ).show()

            return false

        }else if (description.isEmpty()) {

            Toast.makeText(
                RegisterActivity@this,
                "Preencha a Descrição!",
                Toast.LENGTH_SHORT
            ).show()

            return false

        }else {
            return true
        }

    }

    fun getTotalRecipe() {
        val userRef = FirebaseConfiguration.authenticatedUserRef()

        userRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: User? = snapshot.getValue(User::class.java)
                if(user != null) {
                    totalRecipe = user.totalRecipe
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })

    }

    fun updateRecipe(updateRecipe: Double) {
        val userRef = FirebaseConfiguration.authenticatedUserRef()
        userRef.child("totalRecipe").setValue(updateRecipe)
    }

}