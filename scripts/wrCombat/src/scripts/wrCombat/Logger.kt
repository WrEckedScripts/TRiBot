package scripts.wrCombat

import org.tribot.script.sdk.Log
import org.tribot.script.sdk.script.ScriptRuntimeInfo

class Logger(header: String) {
    private val scriptName = ScriptRuntimeInfo.getScriptName()
    private val format = "[$scriptName] [$header]"

    private fun formatMessage(message: Any?) = "$format $message"

    fun debug(message: Any?) = Log.debug(formatMessage(message))

    fun error(message: Any?) = Log.error(formatMessage(message))
}