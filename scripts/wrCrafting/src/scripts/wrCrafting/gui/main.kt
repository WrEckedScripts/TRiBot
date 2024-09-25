package scripts.wrCrafting.gui

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.skia.impl.Log

fun main() {
    val settings = ScriptSettings()

    application(false) {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Testing",
            resizable = false
        ) {
            val gui = GUI(onStartScript = { Log.debug("Script Start Mimic") })
            gui.App(settings)
        }
    }
}