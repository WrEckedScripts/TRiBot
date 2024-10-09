package scripts.wrBlastFurnace.managers

/**
 * Data class that contains all the active managers
 */
data class Container(
    val upkeepManager: UpkeepManager,
    val dispenserManager: DispenserManager,
    val meltingPotManager: MeltingPotManager,
    val tripStateManager: TripStateManager,
    val playerRunManager: PlayerRunManager,
    val staminaManager: StaminaManager,
    val cameraManager: CameraManager,
    val progressionManager: ProgressionManager
)

