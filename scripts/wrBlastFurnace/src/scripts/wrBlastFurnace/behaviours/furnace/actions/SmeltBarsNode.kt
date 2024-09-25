package scripts.wrBlastFurnace.behaviours.furnace.actions

import org.tribot.script.sdk.frameworks.behaviortree.*
import scripts.utils.Logger
import scripts.wrBlastFurnace.behaviours.setup.validation.MoveToFurnaceValidation

fun IParentNode.smeltBarsNode(logger: Logger) = sequence {
    condition { MoveToFurnaceValidation(logger).isWithinBlastFurnaceArea() }
    selector {
        perform {
            logger.info("Were at the spot...")
        }
    }

    // Pay the foreman every 10mins if below 60 smithing

    // Ensure enough gold coins stays within the coffer

    selector {

    }
    //TODO, let's start off by steel
    // - 27 coals
    // - 27 irons
    // - collect bars
    // source: https://oldschool.runescape.wiki/w/Blast_Furnace/Strategies#Single-bar_method

    // add iron ores on second load

    //
}