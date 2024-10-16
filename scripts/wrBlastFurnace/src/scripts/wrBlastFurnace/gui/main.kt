package scripts.wrBlastFurnace.gui

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.jetbrains.skia.impl.Log

fun main() {
    application(false) {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Testing > WrBlastFurnace Lite GUI",
            resizable = true,
            state = WindowState(width = 520.dp, height = 550.dp)
        ) {
            val gui = GUI(onStartScript = { Log.debug("Script Start Mimic") })
            gui.App()
        }
    }
}