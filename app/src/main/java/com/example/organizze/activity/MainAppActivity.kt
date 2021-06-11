package com.example.organizze.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.organizze.R
import com.example.organizze.adapter.RecyclerViewAdapter
import com.example.organizze.config.FirebaseConfiguration
import com.example.organizze.helper.Base64Custom
import com.example.organizze.model.Movement
import com.example.organizze.model.User
import com.github.clans.fab.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.prolificinteractive.materialcalendarview.MaterialCalendarView


class MainAppActivity : AppCompatActivity() {

    private lateinit var textViewGreet: TextView
    private lateinit var textViewBalance: TextView
    private lateinit var calendarView: MaterialCalendarView
    private lateinit var recyclerView: RecyclerView

    private val movementsList: ArrayList<Movement> = ArrayList()

    private val auth: FirebaseAuth = FirebaseConfiguration.auth
    private val db: DatabaseReference = FirebaseConfiguration.db
    private val userRef = FirebaseConfiguration.authenticatedUserRef()
    private var movementsRef: DatabaseReference? = null

    private var valueEventListenerUser: ValueEventListener? = null
    private var valueEventListenerMovements: ValueEventListener? = null

    private var totalExpense = 0.0
    private var totalRecipe = 0.0
    private var total = 0.0
    private var selectedMonthYear: String = ""

    private lateinit var movimentsAdapter: RecyclerViewAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_app)

        //setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = ""
        supportActionBar?.elevation = 0f

        val fabExpense: FloatingActionButton = findViewById(R.id.fab_menu_expense)
        fabExpense.setImageDrawable(
            ContextCompat.getDrawable(
                applicationContext,
                R.drawable.ic_remove_24
            )
        )

        val fabRecipe: FloatingActionButton = findViewById(R.id.fab_menu_recipe)
        fabRecipe.setImageDrawable(
            ContextCompat.getDrawable(
                applicationContext,
                R.drawable.ic_add_24
            )
        )

        textViewGreet = findViewById(R.id.textViewGreet)
        textViewBalance = findViewById(R.id.textViewBalance)
        calendarView = findViewById(R.id.calendarView)

        recyclerView = findViewById(R.id.RecyclerViewMovements)
        configRecyclerView()

        configCalendarView()
        swipe()

    }

    private fun swipe() {


        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(
                    0,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                ) {

//                override fun getMovementFlags(
//                    recyclerView: RecyclerView,
//                    viewHolder: ViewHolder
//                ): Int {
//                    val dragFlags = ItemTouchHelper.ACTION_STATE_IDLE
//                    val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
//                    return makeMovementFlags(dragFlags, swipeFlags)
//                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    removeMovement(viewHolder)

                }

            }

        ItemTouchHelper(itemTouchHelperCallback)
            .attachToRecyclerView(recyclerView)

    }

    override fun onStart() {
        super.onStart()
        getUserDataStat()
        getUserMovements()
    }

    private fun removeMovement(viewHolder: RecyclerView.ViewHolder) {

        val builder: AlertDialog.Builder = AlertDialog.Builder(MainAppActivity@this)
        builder.setTitle("Excluir Movimentação da Conta")
        builder.setMessage("Você tem certeza que deseja realmente excluir essa movimentação da sua conta?")
        builder.setCancelable(false)

        builder.setPositiveButton("Confirmar", object: DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {

                val position = viewHolder.adapterPosition
                val movement: Movement = movementsList[position]

                val userId = Base64Custom.encodeBase64(auth.currentUser?.email.toString())
                movementsRef = db.child("movements")
                    .child(userId)
                    .child(selectedMonthYear)

                movementsRef!!.child(movement.key.toString()).removeValue()

                movimentsAdapter.notifyItemRemoved(position)
                updateBalance(movement)


            }
        })

        builder.setNegativeButton("Cancelar"
        ) { _, _ ->

            Toast.makeText(
                MainAppActivity@this,
                "Cancelado",
                Toast.LENGTH_SHORT
            ).show()

            movimentsAdapter.notifyDataSetChanged()
        }

        builder.create()
        builder.show()

    }

    private fun configRecyclerView() {

        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        movimentsAdapter = RecyclerViewAdapter(applicationContext, movementsList)
        recyclerView.adapter = movimentsAdapter

    }

    private fun configCalendarView() {
        val months = arrayOf("Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro")
        calendarView.setTitleMonths(months)

        val actualDate = calendarView.currentDate
        selectedMonthYear = "${String.format("%02d", (actualDate.month + 1))}${actualDate.year}"

        calendarView.setOnMonthChangedListener { _, date ->
            selectedMonthYear = "${String.format("%02d", (date.month + 1))}${date.year}"
            valueEventListenerMovements?.let { movementsRef?.removeEventListener(it) }
            getUserMovements()
        }

    }

    private fun updateBalance(movement: Movement) {

        if(movement.type.toString() == "recipe") {
            totalRecipe -= movement.value!!
            userRef.child("totalRecipe").setValue(totalRecipe)
        }

        if(movement.type.toString() == "expense") {
            totalExpense -= movement.value!!
            userRef.child("totalExpense").setValue(totalExpense)
        }

    }

    private fun getUserMovements() {

        val userId = Base64Custom.encodeBase64(auth.currentUser?.email.toString())
        movementsRef = db.child("movements")
                         .child(userId)
                         .child(selectedMonthYear)

        valueEventListenerMovements =
            movementsRef!!
            .addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                movementsList.clear()

                for (data in snapshot.children) {
                    val movement = data.getValue(Movement::class.java)
                    if (movement != null) {
                        movement.key = data.key
                        movementsList.add(movement)
                    }
                }

                movimentsAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {}

        })

    }

    private fun getUserDataStat() {

        valueEventListenerUser = userRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if(user != null) {
                    totalExpense = user.totalExpense
                    totalRecipe = user.totalRecipe
                    total = totalRecipe - totalExpense

                    textViewGreet.text = "Olá, ${user.name}"
                    textViewBalance.text = String.format("$%.2f", total)

                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })

    }

    fun addRecipe(view: View) {
        startActivity(Intent(this, RecipesActivity::class.java))
    }

    fun addExpense(view: View) {
        startActivity(Intent(this, ExpensesActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.exitMenu -> {
                auth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        if(valueEventListenerUser != null)
            userRef.removeEventListener(valueEventListenerUser!!)
        if (valueEventListenerMovements != null)
            movementsRef!!.removeEventListener(valueEventListenerMovements!!)
    }

}