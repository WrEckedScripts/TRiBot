package scripts.wrWorker.muling

import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.BufferedWriter
import java.net.Socket

class ClientSocket(val port: Int) {
    private lateinit var out: BufferedWriter
    private var isRunning = true // Keeps the client alive
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob()) // Custom CoroutineScope

    fun start() {
        scope.launch {
            val client = try {
                Socket("127.0.0.1", port)
            } catch (e: Exception) {
                println("Could not connect to the server: ${e.message}")
                return@launch
            }

            out = client.getOutputStream().bufferedWriter()
            val clientIn = client.getInputStream().bufferedReader()

            // Coroutine for reading from the server
            val readJob = launch {
                try {
                    while (isRunning) {
                        val serverResponse = clientIn.readLine() ?: break
                        println("Server: $serverResponse")
                    }
                } catch (e: Exception) {
                    println("Error reading from server: ${e.message}")
                }
            }

            // Keep running until explicitly stopped
            while (isRunning) {
                delay(100) // Non-blocking delay to let other coroutines run
            }

            // Clean up resources when done
            try {
                readJob.cancelAndJoin() // Stop reading coroutine
                out.close()
                clientIn.close()
                client.close()
                println("Client connection closed.")
            } catch (e: Exception) {
                println("Error closing client resources: ${e.message}")
            }
        }
    }

    fun sendMessage(action: Payload) {
        scope.launch {
            if (::out.isInitialized) {
                val json = Gson().toJson(action)
                try {
                    out.write(json)
                    out.newLine()
                    out.flush()
                } catch (e: Exception) {
                    println("Error sending message: ${e.message}")
                }
            } else {
                println("Cannot send message; client is not connected.")
            }
        }
    }

    fun stop() {
        isRunning = false
        scope.cancel() // Cancel the CoroutineScope to clean up all child coroutines
    }
}
