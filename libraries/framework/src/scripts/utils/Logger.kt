package scripts.utils

import org.tribot.script.sdk.Log
//import org.tribot.script.sdk.script.ScriptRuntimeInfo

class Logger(header: String) {
//    private val scriptName = ScriptRuntimeInfo.getScriptName()
//    private val format = "[$scriptName] - [$header]"
    private val format = "[$header]"

    private fun formatMessage(message: Any?) = "$format | $message"

    fun debug(message: Any?) {
        return Log.debug(this.formatMessage(message))
    }

    fun error(message: Any?) {
        return Log.error(this.formatMessage(message))
    }
}