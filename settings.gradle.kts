rootProject.name = "tribot-script-template"

// Own framework
include("libraries:framework")

// Dummy / Examples
include("scripts:wrCombat")

// Smithing & Mining
include("scripts:wrBlastFurnace")
include("scripts:wrMotherlode")
include("scripts:WrFoundry")

// Preparation
include("scripts:wrSkilling")

// Processing
include("scripts:WrCannonBalls")

// Utility & Testing
include("scripts:WrMule")
include("scripts:WrWorker")

// Boilerplate
include("scripts")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
