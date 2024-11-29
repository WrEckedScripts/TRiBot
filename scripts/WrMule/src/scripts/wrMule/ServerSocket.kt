package scripts.wrMule

import com.google.gson.Gson
import kotlinx.coroutines.*
import java.net.ServerSocket
import java.net.Socket

class ServerSocket {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var server: ServerSocket
    private val clientJobs = mutableListOf<Job>()

    fun start() {
        scope.launch {
            try {
                server = ServerSocket(22345)
                println("Server started on port 12345")

                while (isActive) {
                    val client = server.accept()
                    println("New client connected: ${client.inetAddress.hostAddress}")
                    clientJobs.add(handleClient(client))
                }
            } catch (e: Exception) {
                println("Server error: ${e.message}")
            }
        }
    }

    private fun handleClient(client: Socket): Job = scope.launch {
        val clientOut = client.getOutputStream().bufferedWriter()
        val clientIn = client.getInputStream().bufferedReader()
        val gson = Gson()

        try {
            // Coroutine for reading from the client
            val readJob = launch {
                try {
                    while (isActive) {
                        val clientMessage = clientIn.readLine() ?: break
                        println("Client (${client.inetAddress.hostAddress}): $clientMessage")
                        val test = gson.fromJson(clientMessage, TestAction::class.java)

                        println(test.items.toString())
                        println(test.action)
                        println(test.subject)
                        println(test.coordinates.toString())

                        // Send acknowledgment
                        clientOut.write(gson.toJson("received"))
                        clientOut.newLine()
                        clientOut.flush()
                    }
                } catch (e: Exception) {
                    println("Error reading from client: ${e.message}")
                }
            }

            // Coroutine for writing to the client (if needed)
            val writeJob = launch {
                try {
                    while (isActive) {
                        val serverInput = readlnOrNull() ?: break

                        val response = gson.toJson(serverInput) // Serialize if structured
                        clientOut.write(response)
                        clientOut.newLine()
                        clientOut.flush()
                    }
                } catch (e: Exception) {
                    println("Error sending to client: ${e.message}")
                }
            }

            // Wait for both jobs to finish
            readJob.join()
            writeJob.cancelAndJoin() // Cancel the write job after reading finishes
        } catch (e: Exception) {
            println("Client handler error: ${e.message}")
        } finally {
            client.close()
            println("Client disconnected: ${client.inetAddress.hostAddress}")
        }
    }

    fun stop() {
        scope.launch {
            try {
                scope.cancel() // Cancel all coroutines
                clientJobs.forEach { it.cancelAndJoin() } // Ensure all client jobs are terminated
                server.close()
                println("Server stopped.")
            } catch (e: Exception) {
                println("Error stopping server: ${e.message}")
            }
        }
    }
}
