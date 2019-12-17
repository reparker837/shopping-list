package com.ait.shoppinglistapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "todo")
data class Todo(
    @PrimaryKey(autoGenerate = true) var todoId: Long?,
    @ColumnInfo(name = "itemPrice") var itemPrice: String,
    //@ColumnInfo(name = "todoText") var todoText: String,
    @ColumnInfo(name = "itemName") var itemName: String,
    @ColumnInfo(name = "itemCategory") var itemCategory: String,
    @ColumnInfo(name = "done") var done: Boolean
) : Serializable