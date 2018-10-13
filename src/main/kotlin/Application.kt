package backend.todo.ktor

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

@Location("/") class TodoItems {
    @Location("/{id}")
    class Single(val id: Int)
}

/**
 * Entry Point of the application. This function is referenced in the
 * resources/application.conf file inside the ktor.application.modules.
 *
 * Notice that the fqname of this function is io.ktor.samples.chat.ChatApplicationKt.main
 * For top level functions, the class name containing the method in the JVM is FileNameKt.
 *
 * The `Application.main` part is Kotlin idiomatic that specifies that the main method is
 * an extension of the [Application] class, and thus can be accessed like a normal member `myapplication.main()`.
 */
fun Application.main() {
    TodoApplication().apply { main() }
}

class TodoApplication {
    fun Application.main() {
        /**
         * First we install the features we need. They are bound to the whole application.
         * Since this method has an implicit [Application] receiver that supports the [install] method.
         */
        // This adds automatically Date and Server headers to each response, and would allow you to configure
        // additional headers served to each response.
        install(DefaultHeaders)
        // This uses use the logger to log every call (request/response)
        install(CallLogging)

        install(ContentNegotiation) { gson {} }
        install(Locations)
        install(StatusPages)

        val service = TodoService()

//        if (isTest || isDev) {
            println("In non-prod environment. Adding test data.")
            service.add(Todo(1, "Todo 1", false))
            service.add(Todo(2, "Todo 2", false))
//        }

        routing {
            get<TodoItems.Single> { item ->
                val todo = service.get(item.id)
                call.respond(HttpStatusCode.OK, todo)
            }

            post<TodoItems> {
                val post = call.receive<Todo>()
                service.add(post)
                call.respond(HttpStatusCode.Created)
            }

            delete<TodoItems.Single> { item ->
                service.delete(item.id)
                call.respond(HttpStatusCode.OK)
            }

            delete<TodoItems> { item ->
                service.clear()
                call.respond(HttpStatusCode.OK)
            }

            get<TodoItems> {
                val list = service.list()
                call.respond(HttpStatusCode.OK, list)
            }
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

val Application.envKind get() = environment.config.property("ktor.environment").getString()
val Application.isTest get() = envKind == "test"
val Application.isDev get() = envKind == "dev"
val Application.isProd get() = envKind == "prod"
