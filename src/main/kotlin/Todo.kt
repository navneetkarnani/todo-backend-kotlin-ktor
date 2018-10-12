package backend.todo.ktor

data class Todo(var id: Int = 0, var title: String = "", var completed: Boolean = false, var order: Int = -1)

val todos = arrayListOf<Todo>()
var counter: Int = 0;

class TodoService {
    fun list() = todos;

    fun add(todo: Todo): Todo {
        todo.id = counter ++;
        todos.add(todo)
        todos.sortBy { it.order }
        return todo;
    }

    fun get(id: Int) = todos.first { it.id == id }

    fun clear() {
        todos.clear()
    }

    fun delete(id: Int): Boolean {
        if (todos.any { it.id == id }) {
            todos.removeIf {it.id == id}
            return true
        }
        return false
    }

}