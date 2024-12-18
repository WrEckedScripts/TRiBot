package scripts.wrFoundry.tasks.prepare

class ReceiveCommisionTask {

    private fun shouldExecute(): Boolean {
        return true//todo
    }

    fun execute(): Boolean {
        if (!this.shouldExecute()) {
            return false
        }

        // NPC = Kovac

        // If option "Hand-in" exists && Wearing a preform
        // We need to hand-in and collect a new task (option "yes")

        // Then we need to Setup the mould based on the received task
        // - needs to extract from the string
        // - map to the correct combinations
        // - handle the UI

        // Then we need to fill the crucible
        // - 14 adamantite bars
        // - 14 mithril bars

        // If crucible is full, pour and collect the sword from mould

        return true
    }
}