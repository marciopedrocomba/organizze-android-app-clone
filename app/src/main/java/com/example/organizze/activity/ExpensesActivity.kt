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
import com.google.firebase.database.*

class ExpensesActivity : AppCompatActivity() {

    private lateinit var editDate: TextInputEditText
    private lateinit var editCategory: TextInputEditText
    private lateinit var editDescription: TextInputEditText
    private lateinit var editValue: EditText

    private var totalExpense = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)

        editDate = findViewById(R.id.editExpensesDate)
        editCategory = findViewById(R.id.editExpensesCategory)
        editDescription = findViewById(R.id.editExpensesDescription)
        editValue = findViewById(R.id.editExpenseValue)

        // fill the date field with the actual date
        editDate.setText(DateCustom.actualDate())

        getTotalExpense()

        val buttonSave: FloatingActionButton = findViewById(R.id.fabExpensesSave)

        buttonSave.setOnClickListener {
            saveExpense()
        }

    }

    fun saveExpense() {

        if(validateFields()) {

            val value = editValue.text.toString().toDouble()

            val movement = Movement()

            movement.value = value
            movement.category = editCategory.text.toString()
            movement.description = editDescription.text.toString()
            movement.date = editDate.text.toString()
            movement.type = "expense"
            movement.save()

            val updatedExpense = totalExpense + value

            updateExpense(updatedExpense)

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
                ExpensesActivity@this,
                "Preencha o valor!",
                Toast.LENGTH_SHORT
            ).show()

            return false

        }else if (date.isEmpty()){

            Toast.makeText(
                ExpensesActivity@this,
                "Preencha a Data!",
                Toast.LENGTH_SHORT
            ).show()

            return false

        }else if (category.isEmpty()) {

            Toast.makeText(
                ExpensesActivity@this,
                "Preencha a Categoria!",
                Toast.LENGTH_SHORT
            ).show()

            return false

        }else if (description.isEmpty()) {

            Toast.makeText(
                ExpensesActivity@this,
                "Preencha a Descrição!",
                Toast.LENGTH_SHORT
            ).show()

            return false

        }else {
            return true
        }

    }

    fun getTotalExpense() {
        val userRef = FirebaseConfiguration.authenticatedUserRef()

        userRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: User? = snapshot.getValue(User::class.java)
                if(user != null) {
                    totalExpense = user.totalExpense
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })

    }

    fun updateExpense(updatedExpense: Double) {
        val userRef = FirebaseConfiguration.authenticatedUserRef()
        userRef.child("totalExpense").setValue(updatedExpense)
    }

}