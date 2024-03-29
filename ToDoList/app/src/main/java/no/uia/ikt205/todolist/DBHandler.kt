package no.uia.ikt205.todolist

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import no.uia.ikt205.todolist.DTO.ToDo
import no.uia.ikt205.todolist.DTO.ToDoItem

class DBHandler(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val createToDoTable = "CREATE TABLE $TABLE_TODO (" +
                "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                "$COL_CREATED_AT datetime DEFAULT CURRENT_TIMESTAMP," +
                "$COL_NAME varchar);"
        val createToDoItemTable:String =
                "CREATE TABLE $TABLE_TODO_ITEM (" +
                "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                "$COL_CREATED_AT datetime DEFAULT CURRENT_TIMESTAMP," +
                "$COL_TODO_ID integer," +
                "$COL_ITEM_NAME varchar," +
                "$COL_IS_COMPLETED integer);"

        db.execSQL(createToDoTable)
        db.execSQL(createToDoItemTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }


    // Add task
    fun addToDo(toDo: ToDo): Boolean {
        val db:SQLiteDatabase = writableDatabase
        val cv = ContentValues()
        cv.put(COL_NAME, toDo.name)
        val result : Long = db.insert(TABLE_TODO, null, cv)
        return result!=(-1).toLong()
    }

    // Update task
    fun updateToDo(toDo: ToDo) {
        val db:SQLiteDatabase = writableDatabase
        val cv = ContentValues()
        cv.put(COL_NAME, toDo.name)
        db.update(TABLE_TODO,cv,"$COL_ID=?", arrayOf(toDo.id.toString()))
    }

    // Delete task
    fun deleteToDo(todoId: Long) {
        val db:SQLiteDatabase = writableDatabase
        db.delete(TABLE_TODO_ITEM, "$COL_TODO_ID=?", arrayOf(todoId.toString()))
        db.delete(TABLE_TODO, "$COL_ID=?", arrayOf(todoId.toString()))
    }

    fun updateToDoItemCompletedStatus(todoId: Long,isCompleted: Boolean) {
        val db = writableDatabase
        val queryResult = db.rawQuery("SELECT * FROM $TABLE_TODO_ITEM WHERE $COL_TODO_ID=$todoId", null)

        if(queryResult.moveToFirst()) {
            do{
                val item = ToDoItem()
                item.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                item.toDoId = queryResult.getLong(queryResult.getColumnIndex(COL_TODO_ID))
                item.itemName = queryResult.getString(queryResult.getColumnIndex(COL_ITEM_NAME))
                item.isCompleted = isCompleted
                updateToDoItem(item)
            }while ( queryResult.moveToNext())
        }

        queryResult.close()
    }

    fun getToDos() : MutableList<ToDo>{
        val result : MutableList<ToDo> = ArrayList()
        val db:SQLiteDatabase = readableDatabase
        val queryResult: Cursor = db.rawQuery("SELECT * from $TABLE_TODO", null)
        if(queryResult.moveToFirst()){
            do{
                val todo = ToDo()
                todo.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                todo.name = queryResult.getString(queryResult.getColumnIndex(COL_NAME))
                result.add(todo)
            } while (queryResult.moveToNext())
        }
        queryResult.close()
        return result
    }

    // Add subtask
    fun addToDoItem(item : ToDoItem) : Boolean {
        val db: SQLiteDatabase = writableDatabase
        val cv = ContentValues()
        cv.put(COL_ITEM_NAME, item.itemName)
        cv.put(COL_TODO_ID, item.toDoId)
        if(item.isCompleted)
            cv.put(COL_IS_COMPLETED, true)
        else
            cv.put(COL_IS_COMPLETED, false)

        val result:Long = db.insert(TABLE_TODO_ITEM, null, cv)
        return result != (-1).toLong()
    }

    // Update subtask
    fun updateToDoItem(item : ToDoItem){
        val db: SQLiteDatabase = writableDatabase
        val cv = ContentValues()
        cv.put(COL_ITEM_NAME, item.itemName)
        cv.put(COL_TODO_ID, item.toDoId)
        cv.put(COL_IS_COMPLETED, item.isCompleted)

        db.update(TABLE_TODO_ITEM,cv,"$COL_ID=?", arrayOf(item.id.toString()))
    }

    // Delete subtask
    fun deleteToDoItem(itemId: Long) {
        val db: SQLiteDatabase = writableDatabase
        db.delete(TABLE_TODO_ITEM, "$COL_ID=?", arrayOf(itemId.toString()))
    }

    fun getToDoItems(todoId : Long) : MutableList<ToDoItem>{
        val result : MutableList<ToDoItem> = ArrayList()

        val db:SQLiteDatabase = readableDatabase
        val queryResult = db.rawQuery("SELECT * FROM $TABLE_TODO_ITEM WHERE $COL_TODO_ID=$todoId", null)

        if(queryResult.moveToFirst()) {
            do{
                val item = ToDoItem()
                item.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                item.toDoId = queryResult.getLong(queryResult.getColumnIndex(COL_TODO_ID))
                item.itemName = queryResult.getString(queryResult.getColumnIndex(COL_ITEM_NAME))
                item.isCompleted = queryResult.getInt(queryResult.getColumnIndex(COL_IS_COMPLETED)) == 1
                result.add(item)
            }while ( queryResult.moveToNext())
        }

        queryResult.close()
        return result
    }

}