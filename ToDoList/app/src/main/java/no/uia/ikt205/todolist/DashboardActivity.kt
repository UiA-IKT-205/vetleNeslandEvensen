package no.uia.ikt205.todolist

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_dashboard.*
import no.uia.ikt205.todolist.DTO.ToDo

class DashboardActivity : AppCompatActivity() {

    lateinit var dbHandler: DBHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(dashboard_toolbar)
        title = "Todo list"
        dbHandler = DBHandler(this)
        rv_dashboard.layoutManager = LinearLayoutManager(this)

        fab_dashboard.setOnClickListener{
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Add Todo")
            val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
            val toDoName = view.findViewById<EditText>(R.id.et_todo)
            dialog.setView(view)
            dialog.setPositiveButton("Add") { _: DialogInterface, _: Int ->
                if (toDoName.text.isNotEmpty()) {
                    val toDo = ToDo()
                    toDo.name = toDoName.text.toString()
                    dbHandler.addToDo(toDo)
                    refreshList()
                }
            }
            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

            }
            dialog.show()
        }

    }

    fun updateTodo(toDo: ToDo) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Update Todo")
        val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
        val toDoName = view.findViewById<EditText>(R.id.et_todo)
        toDoName.setText(toDo.name)
        dialog.setView(view)
        dialog.setPositiveButton("Update") { _: DialogInterface, _: Int ->
            if (toDoName.text.isNotEmpty()) {
                toDo.name = toDoName.text.toString()
                dbHandler.updateToDo(toDo)
                refreshList()
            }
        }
        dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

        }
        dialog.show()
    }

    override fun onResume() {
        refreshList()
        super.onResume()
}

    private fun refreshList(){
        rv_dashboard.adapter = DashBoardAdapter(this, dbHandler.getToDos())
    }

    class DashBoardAdapter(val activity: DashboardActivity, val list: MutableList<ToDo>) : RecyclerView.Adapter<DashBoardAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.rv_child_dashboard, parent, false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.toDoName.text = list[position].name

           holder.toDoName.setOnClickListener{
               val intent = Intent(activity,ItemActivity::class.java)
               intent.putExtra(INTENT_TODO_ID, list[position].id)
               intent.putExtra(INTENT_TODO_NAME, list[position].name)
               activity.startActivity(intent)
           }

            holder.menu.setOnClickListener{
                val popup = PopupMenu(activity, holder.menu)
                popup.inflate(R.menu.dashboard_child)
                popup.setOnMenuItemClickListener {

                    when(it.itemId) {
                        R.id.menu_edit->{
                            activity.updateTodo(list[position])

                        }
                        R.id.menu_delete->{
                            val dialog:AlertDialog.Builder = AlertDialog.Builder(activity)
                            dialog.setTitle("Deleting item")
                            dialog.setMessage("Are you sure you want to delete this task?")
                            dialog.setPositiveButton("Delete") { _: DialogInterface, _: Int ->
                                activity.dbHandler.deleteToDo(list[position].id)
                                activity.refreshList()
                            }
                            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
                            }
                            dialog.show()
                        }
                        R.id.menu_mark_as_completed->{
                            activity.dbHandler.updateToDoItemCompletedStatus(list[position].id, true)
                        }
                        R.id.menu_reset->{
                            activity.dbHandler.updateToDoItemCompletedStatus(list[position].id, false)
                        }
                    }

                        true
                }
                popup.show()
            }
        }

        class ViewHolder(v : View) : RecyclerView.ViewHolder(v){
            val toDoName : TextView = v.findViewById(R.id.tv_todo_name)
            val menu : ImageView = v.findViewById(R.id.iv_menu)
        }
    }
}