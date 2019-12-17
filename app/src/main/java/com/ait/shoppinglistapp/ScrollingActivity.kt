package com.ait.shoppinglistapp

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.GridLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.ait.shoppinglistapp.adapter.TodoAdapter
import com.ait.shoppinglistapp.data.AppDatabase
import com.ait.shoppinglistapp.data.Todo
import com.ait.shoppinglistapp.touch.TodoReyclerTouchCallback
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.activity_scrolling.view.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import java.util.*
import java.util.prefs.PreferenceChangeEvent

class ScrollingActivity : AppCompatActivity(), TodoDialog.TodoHandler {

    companion object {
        const val KEY_TODO = "KEY_TODO"
    }

    lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)

        setSupportActionBar(toolbar)

        initRecyclerView()


        fab.setOnClickListener {
            showAddTodoDialog()
        }

        fabDeleteAll.setOnClickListener {
            todoAdapter.deleteAllTodos()
        }

        if (!wasStartedBefore()) {
            MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.fab)
                .setPrimaryText(getString(R.string.newItem))
                .setSecondaryText(getString(R.string.newItemInstruct))
                .show()
            saveWasStarted()
        }
    }

    fun saveWasStarted() {
        var sharedPref =
            PreferenceManager.getDefaultSharedPreferences(this)
        var editor = sharedPref.edit()
        editor.putBoolean(getString(R.string.key_started), true)
        editor.apply()
    }

    fun wasStartedBefore(): Boolean {
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        return sharedPref.getBoolean(getString(R.string.key_started), false)
    }

    private fun initRecyclerView() {
        Thread {
            var todoList =
                AppDatabase.getInstance(this@ScrollingActivity).todoDao().getAllTodo()

            runOnUiThread {
                todoAdapter = TodoAdapter(this, todoList)
                recyclerTodo.adapter = todoAdapter

                var itemDecoration = DividerItemDecoration(
                    this,
                    DividerItemDecoration.VERTICAL
                )
                recyclerTodo.addItemDecoration(itemDecoration)

                //recyclerTodo.layoutManager =
                //    GridLayoutManager(this, 2)

                val callback = TodoReyclerTouchCallback(todoAdapter)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(recyclerTodo)
            }
        }.start()
    }

    fun showAddTodoDialog() {
        TodoDialog().show(supportFragmentManager, getString(R.string.tag_todo_dialog))
    }

    var editIndex: Int = -1

    fun showEditTodoDialog(todoToEdit: Todo, idx: Int) {
        editIndex = idx

        val editDialog = TodoDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_TODO, todoToEdit)
        editDialog.arguments = bundle

        editDialog.show(supportFragmentManager, getString(R.string.tag_todo_edit))

    }

    fun saveTodo(todo: Todo) {
        Thread {
            var newId: Long =
                AppDatabase.getInstance(this).todoDao().insertTodo(
                    todo
                )

            todo.todoId = newId

            runOnUiThread {
                todoAdapter.addTodo(todo)
            }
        }.start()
    }

    override fun todoCreated(item: Todo) {
        saveTodo(item)
    }

    override fun todoUpdated(item: Todo) {
        Thread {
            AppDatabase.getInstance(
                this@ScrollingActivity
            ).todoDao().updateTodo(item)

            runOnUiThread {
                todoAdapter.updateTodoOnPosition(item, editIndex)
            }
        }.start()
    }
}
