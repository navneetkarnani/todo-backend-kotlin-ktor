package backend.todo.ktor

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

@Location("/{id}") class TodoAllItems
@Location("/{id}") class TodoItem(val id: Int)

fun Application.mymodule() {
    install(Locations)
    install(StatusPages)

    val service = TodoService()
    routing {
        get<TodoAllItems> {
            call.respond(HttpStatusCode.OK, service.list())
        }

        get<TodoItem> {item ->
            val todo = service.get(item.id)
            call.respond(HttpStatusCode.OK, todo)
        }

        post<TodoItem> {
            val post = call.receive<Todo>()
            service.add(post)
            call.respond(HttpStatusCode.Created)
        }

        delete<TodoItem> {item ->
            service.delete(item.id)
            call.respond(HttpStatusCode.OK)
        }

        delete<TodoAllItems> {item ->
            service.clear()
            call.respond(HttpStatusCode.OK)
        }
    }
}

/**
 * Entry point of the application: main method that starts an embedded server using Jetty,
 * processes the application.conf file, interprets the command line args if available
 * and loads the application modules.
 */
fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}