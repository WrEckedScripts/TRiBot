package scripts.wrBarrows.behaviors.rooms.tasks

import org.tribot.script.sdk.ChatScreen
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.GameObject
import scripts.wrBarrows.behaviors.combat.tasks.AttackBrother
import scripts.wrBarrows.behaviors.combat.tasks.EnablePrayer
import scripts.wrBarrows.managers.Container
import scripts.wrBarrows.rooms.RoomState

class InteractSarcophagus(val managers: Container) {
    private val action: String = "Search"
    private val objectName: String = "Sarcophagus"

    private val currentRoomState: RoomState = this.managers.roomManager.getCurrentCrypt()!!.value

    fun satisfied(): Boolean {
        return this.currentRoomState.isCompleted
    }

    fun execute(): Boolean {
        val prayer = this.currentRoomState.room.brother.prayer
        if (prayer != null) {
            Waiting.waitUntil {
                EnablePrayer(prayer.protectionPrayer).execute()
            }
        }

        // Interact sarcophagus
        Waiting.waitUntil {
            this.findSarcophagus().interact(action)
        }

        Waiting.wait(600)

        // mark as tunnel or let script handle combat
        if (
            ChatScreen.isClickContinueOpen()
            && ChatScreen.containsText("hidden tunnel")
        ) {
            this.managers.roomManager.markAsTunnel(this.currentRoomState.room)

            ChatScreen.clickContinue()

            if (this.managers.roomManager.shouldEnterTunnel()) {
                Waiting.waitUntil {
                    ChatScreen.isSelectOptionOpen()
                }

                ChatScreen.selectOption("Yes, I'm fearless")
            }
        } else {
            AttackBrother(this.managers)
        }

        return true
    }

    private fun findSarcophagus(): GameObject {
        return Query.gameObjects()
            .nameEquals(objectName)
            .findBestInteractable()
            .get()
    }
}