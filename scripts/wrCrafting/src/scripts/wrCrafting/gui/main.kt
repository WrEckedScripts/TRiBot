package scripts.wrCrafting.gui

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() {
    val settings = ScriptSettings()

    application(true) {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Testing",
            resizable = false
        ) {
            val gui = GUI()
            gui.App(settings)
        }
    }
}