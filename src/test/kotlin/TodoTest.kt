package backend.todo.ktor

import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlinx.serialization.json.JSON
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TodoTest {
    @Test
    fun getAllTest() = withTestApplication(Application::main) {
        with( handleRequest(HttpMethod.Get, "/")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertNotNull(response.content);
            val todoList = response.content?.let { JSON.parse<ArrayList<Todo>>(it) }
            assertNotNull(todoList);
        }
    }

    @Test
    fun getOneTest() = withTestApplication(Application::main) {
        with( handleRequest(HttpMethod.Get,"/1")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertNotNull(response.content);
            val todo = response.content?.let { JSON.parse<Todo>(it) }
            assertNotNull(todo)
            if (todo != null) {
                assertEquals(1, todo.id)
            }
        }
    }
}