package backend.todo.ktor

import io.ktor.application.Application
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.withTestApplication
import kotlinx.serialization.json.JSON
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TodoTest {
    @Test
    fun getAllTest() = withTestApplication(Application::main) {
        with( handleRequest { "/" }) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("{}", response.content)
        }
    }

    @Test
    fun getAllOneTest() = withTestApplication(Application::main) {
        with( handleRequest { "/1" }) {
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