import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.slf4j.LoggerFactory
import java.io.File

object Main {
    val LOGGER = LoggerFactory.getLogger("Diplomacy")

    val WORLD: World = loadWorld()
}

fun main(args: Array<String>) {
    embeddedServer(Netty, 443) {
        install(ContentNegotiation) {
            json()
        }

        routing {
            get("/world") {
                call.respond(Json.encodeToJsonElement(Main.WORLD))
            }

            get("/nations") {
                call.respond(Json.encodeToJsonElement(Main.WORLD.getNations()))
            }

            get("/provinces") {
                call.respond(Json.encodeToJsonElement(Main.WORLD.getProvinces()))
            }

            get("/groups") {
                call.respond(Json.encodeToJsonElement(Main.WORLD.getGroups()))
            }

            post("/nations") {
               call.receive<Nation>().also { nation ->
                   getWorld().addNation(nation).fold({
                       call.respond(HttpStatusCode.Accepted)
                   }, { warn ->
                       call.respond(HttpStatusCode.Conflict, warn)
                   })
               }
            }

            post("/provinces") {
                call.receive<Province>().also { province ->
                    getWorld().addProvince(province).fold({
                        call.respond(HttpStatusCode.Accepted)
                    }, { warn ->
                        call.respond(HttpStatusCode.Conflict, warn)
                    })
                }
            }

            post("/groups") {
                call.receive<Group>().also { group ->
                    getWorld().addGroup(group).fold({
                        call.respond(HttpStatusCode.Accepted)
                    }, { warn ->
                        call.respond(HttpStatusCode.Conflict, warn)
                    })
                }
            }

            post("/orders") {
                call.receive<Order>().also { order ->
                    getWorld().addOrder(order).fold({
                        call.respond(HttpStatusCode.Accepted)
                    }, { warn ->
                        call.respond(HttpStatusCode.Conflict, warn)
                    })
                }
            }
        }
    }.start()

    saveWorld()
}

fun loadWorld() = File("world.json").inputStream().bufferedReader().use {
    World() //Json.decodeFromString<World>(it.readText())
}

fun saveWorld() = File("world.json").outputStream().bufferedWriter().use {
    it.write(Json.encodeToString(Main.WORLD))
}

fun getWorld() = Main.WORLD