package scripts.wrBarrows.behaviors.rooms.tasks

import org.tribot.script.sdk.ChatScreen
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.GameObject
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.wrBarrows.behaviors.combat.tasks.DisablePrayer
import scripts.wrBarrows.behaviors.combat.tasks.EnablePrayer
import scripts.wrBarrows.managers.Container
import scripts.wrBarrows.managers.State
import scripts.wrBarrows.player.BarrowsArea
import scripts.wrBarrows.rooms.RoomState
import kotlin.jvm.optionals.getOrNull

class InteractSarcophagus(val managers: Container) {
    private val action: String = "Search"
    private val objectName: String = "Sarcophagus"

    private var currentRoomState: RoomState? = this.managers.roomManager.getCurrentCrypt()?.value

    fun satisfied(): Boolean {
        Logger("InteractSarcophagus").debug("- - Completed: ${this.currentRoomState?.isCompleted}")
        Logger("InteractSarcophagus").debug("- - Tunnel: ${this.currentRoomState?.isTunnel}")

        if (this.currentRoomState?.isTunnel ?: false) {
            return false
        }

        return this.currentRoomState?.isCompleted ?: false
    }

    fun execute(): Boolean {
        if (this.currentRoomState == null) {
            this.currentRoomState = this.managers.roomManager.getCurrentCrypt()!!.value
        }

        val prayer = this.currentRoomState!!.room.brother.prayer
        Logger("InteractSarcophagus").debug("execute() - Prayer: $prayer")
        if (prayer != null) {
            Waiting.waitUntil(20_000) {
                EnablePrayer(prayer.protectionPrayer).execute()
            }
        }

        Logger("InteractSarcophagus").debug("execute() - Prayer: $prayer enabled?")

        // Interact sarcophagus
        Waiting.waitUntil(25_000) {
            this.findSarcophagus()?.interact(action) ?: false
        }

        Logger("InteractSarcophagus").debug("execute() - Interacted with the sarcophagus?")

        // Slight delay to see if a chatScreen pops-up
        Waiting.waitUntil(4_000) {
            ChatScreen.isOpen()
        }

        Logger("InteractSarcophagus").debug("${ChatScreen.isOpen()}")
        Logger("InteractSarcophagus").debug("${ChatScreen.containsText("hidden")}")

        if (
            ChatScreen.isClickContinueOpen()
            && ChatScreen.containsText("hidden")
        ) {
            this.managers.roomManager.markAsTunnel(this.currentRoomState!!.room)
            Logger("InteractSarcophagus").debug("execute() - Marking room as tunnel")

            if (this.managers.roomManager.shouldEnterTunnel()) {
                ChatScreen.clickContinue()

                Logger("InteractSarcophagus").debug("execute() - Entering tunnel")
                Waiting.waitUntil {
                    ChatScreen.isSelectOptionOpen()
                }

                ChatScreen.selectOption("Yeah I'm fearless!")

                val enteredTheCrypt = Waiting.waitUntil(15_000) {
                    BarrowsArea.BARROWS.crypt.containsMyPlayer()
                }

                if (enteredTheCrypt) {
                    managers.stateManager.set(State.TUNNEL.name)
                }

                DisablePrayer().execute()

                return true
            } else {
                return LeaveRoom(managers).execute()
            }
        }

        Waiting.wait(FatigueResolver.getMilliseconds())
        // Return inverse to prevent roomSequence from being re-called.
        return !this.managers.stateManager.set(State.FIGHT.name)
    }

    fun findSarcophagus(): GameObject? {
        return Query.gameObjects()
            .nameEquals(objectName)
            .findBestInteractable()
            .getOrNull()
    }
}