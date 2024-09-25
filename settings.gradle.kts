rootProject.name = "tribot-script-template"

// Own code
include("libraries:framework")

include("scripts:wrCombat")
include("scripts:wrBlastFurnace")
include("scripts:wrCrafting")

// Boilerplate
include("scripts")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}