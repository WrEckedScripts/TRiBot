package scripts.wrFoundry.tasks

interface ExecutableTask {

    fun shouldExecute(): Boolean

    fun execute(): Boolean
}