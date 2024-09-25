package scripts.utils

import org.tribot.script.sdk.Log

class Logger(section: String) {
    private val format = section

    private fun formatMessage(message: Any?) = "[$format] | $message"

    fun debug(message: Any?) {
        return Log.debug(this.formatMessage(message))
    }

    fun error(message: Any?) {
        return Log.error(this.formatMessage(message))
    }

    fun warn(message: Any?) {
        return Log.warn(this.formatMessage(message))
    }

    fun info(message: Any?) {
        return Log.info(this.formatMessage(message))
    }

    fun trace(message: Any?) {
        return Log.trace(this.formatMessage(message))
    }


}