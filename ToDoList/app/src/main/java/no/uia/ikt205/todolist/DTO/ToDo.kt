package no.uia.ikt205.todolist.DTO

class ToDo{

    var id : Long = -1
    var name = ""
    var createdAt = ""
    var items : MutableList<ToDo> = ArrayList()

}