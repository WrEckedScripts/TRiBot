package scripts.wrCrafting.gui

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() {
    application(true) {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Testing",
            resizable = false
        ) {
            App()
        }
    }
}