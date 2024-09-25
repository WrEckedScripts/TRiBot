package scripts.wrCrafting.models

/**
 * Class that should hold any values, re-usable by any other classes for quick lookups, see it like a model.
 * We can fetch data from here or persist it. But let's keep manipulations out of this class.
 */
class TaskConfiguration(
    val craftableName: String,
    val craftedName: String,
    var isProcessing: Boolean = false
) {

}