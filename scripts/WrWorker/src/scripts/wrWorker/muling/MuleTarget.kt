package scripts.wrWorker.muling

import com.google.gson.Gson

object MuleTarget {
    var hasActiveTarget = false
    var attempts = 0

    // todo accept json message here
    var rawPayload: String = ""

    // return new to be created class for moving to the right place etc.
    // so this class returns the receivedPayload
    fun decode() {
        //TODO this now maps to the WRONG Payload, as we need the same from the Mule..
        val response = Gson().fromJson(this.rawPayload, Payload::class.java)
    }
}