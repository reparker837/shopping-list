package com.ait.shoppinglistapp

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.ait.shoppinglistapp.data.Todo
import kotlinx.android.synthetic.main.new_todo_dialog.view.*
import java.io.Serializable
import java.util.*

class TodoDialog : DialogFragment() {

    interface TodoHandler {
        fun todoCreated(item: Todo)
        fun todoUpdated(item: Todo)
    }

    private lateinit var todoHandler: TodoHandler

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is TodoHandler) {
            todoHandler = context
        } else {
            throw RuntimeException(
                getString(R.string.doesNotImplement)
            )
        }
    }

    private lateinit var etItemPrice: EditText
    //private lateinit var etTodoText: EditText
    private lateinit var etItemName: EditText
    private lateinit var spItemCategory: Spinner

    var isEditMode = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(getString(R.string.newItem))

        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.new_todo_dialog, null
        )

        etItemPrice = rootView.etPrice
        //etTodoText = rootView.etTodo
        etItemName = rootView.etName
        spItemCategory = rootView.spCategory
        builder.setView(rootView)

        isEditMode = ((arguments != null) && arguments!!.containsKey(ScrollingActivity.KEY_TODO))

        if (isEditMode) {
            var todo: Todo = (arguments?.getSerializable(ScrollingActivity.KEY_TODO) as Todo)

            etItemPrice.setText(todo.itemPrice)
            //etTodoText.setText(todo.todoText)
            etItemName.setText(todo.itemName)
            //spItemCategory.isSelected(todo.itemCategory)
        }

        builder.setPositiveButton(getString(R.string.ok)) { dialog, witch ->
            // empty
        }

        return builder.create()
    }


    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etItemName.text.isNotEmpty() && etItemPrice.text.isNotEmpty()) {
                if (isEditMode) {
                    handleTodoEdit()
                } else {
                    handleTodoCreate()
                }
                dialog.dismiss()
            } else {
                if (etItemName.text.isEmpty()){
                    etItemName.error = getString(R.string.emptyField)
                }
                if (etItemPrice.text.isEmpty()){
                    etItemPrice.error = getString(R.string.emptyField)
                }

            }
        }
    }

    private fun handleTodoCreate() {
        todoHandler.todoCreated(
            Todo(
                null,
                etItemPrice.text.toString(),
                etItemName.text.toString(),
                spItemCategory.selectedItem.toString(),
                false
            )
        )
    }

    private fun handleTodoEdit() {
        val todoToEdit = arguments?.getSerializable(
            ScrollingActivity.KEY_TODO
        ) as Todo
        todoToEdit.itemPrice = etItemPrice.text.toString()
        todoToEdit.itemName = etItemName.text.toString()
        todoToEdit.itemCategory = spItemCategory.selectedItem.toString()

        todoHandler.todoUpdated(todoToEdit)
    }
}
