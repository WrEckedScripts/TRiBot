package scripts.wrSulphurNagua.behaviours.contracts.tasks

interface TaskContract {
    fun satisfied(): Boolean

    fun execute(): Boolean
}