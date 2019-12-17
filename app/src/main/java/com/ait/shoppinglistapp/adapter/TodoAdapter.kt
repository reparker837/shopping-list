package com.ait.shoppinglistapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ait.shoppinglistapp.R
import com.ait.shoppinglistapp.ScrollingActivity
import com.ait.shoppinglistapp.data.AppDatabase
import com.ait.shoppinglistapp.data.Todo
import com.ait.shoppinglistapp.touch.TodoTouchHelperCallback
import kotlinx.android.synthetic.main.todo_row.view.*
import java.util.*

class TodoAdapter : RecyclerView.Adapter<TodoAdapter.ViewHolder>, TodoTouchHelperCallback {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val todoRow = LayoutInflater.from(context).inflate(
            R.layout.todo_row, parent, false
        )
        return ViewHolder(todoRow)
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var todo = todoList.get(holder.adapterPosition)

        holder.tvName.text = todo.itemName
        holder.cbPurchased.isChecked = todo.done
        holder.tvPrice.text = todo.itemPrice
        if (todo.itemCategory == context.getString(R.string.food)){
            holder.ivType.setImageResource(R.drawable.food_icon)
        }
        else if (todo.itemCategory == context.getString(R.string.clothes)){
            holder.ivType.setImageResource(R.drawable.clothes_icon)
        }
        else{
            holder.ivType.setImageResource(R.drawable.car_icon)
        }

        holder.btnDelete.setOnClickListener{
            deleteTodo(holder.adapterPosition)
        }

        holder.cbPurchased.setOnClickListener{
            todo.done = holder.cbPurchased.isChecked
            updateTodo(todo)
        }

        holder.btnEdit.setOnClickListener{
            (context as ScrollingActivity).showEditTodoDialog(
                todo, holder.adapterPosition
            )
        }
    }

    var todoList = mutableListOf<Todo>()

    val context: Context
    constructor(context: Context, listTodos: List<Todo>){
        this.context = context

        todoList.addAll(listTodos)

        //for(i: Int in 0..20){
        //    todoList.add(Todo(2019,"$ $i", "Todo $i", false))
        //}
    }

    fun updateTodo(todo: Todo){
        Thread{
            AppDatabase.getInstance(context).todoDao().updateTodo(todo)
        }.start()
    }

    fun updateTodoOnPosition(todo: Todo, index: Int){
        todoList.set(index, todo)
        notifyItemChanged(index)
    }

    fun deleteTodo(index: Int){
        Thread{
            AppDatabase.getInstance(context).todoDao().deleteTodo(todoList[index])
            (context as ScrollingActivity).runOnUiThread {
                todoList.removeAt(index)
                notifyItemRemoved(index)
            }
        }.start()
    }

    fun deleteAllTodos() {
        Thread {
            AppDatabase.getInstance(context).todoDao().deleteAllTodo()

            (context as ScrollingActivity).runOnUiThread {
                todoList.clear()
                notifyDataSetChanged()
            }
        }.start()
    }

    fun addTodo(todo: Todo){
        todoList.add(todo)
        notifyItemInserted(todoList.lastIndex)
    }

    override fun onDismissed(position: Int) {
        deleteTodo(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(todoList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvName = itemView.tvName
        val cbPurchased = itemView.cbPurchased
        val tvPrice = itemView.tvPrice
        val btnDelete = itemView.btnDelete
        val btnEdit = itemView.btnEdit
        val ivType = itemView.ivType

    }

}