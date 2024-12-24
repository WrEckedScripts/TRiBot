package scripts.wrFoundry.tasks.prepare

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.Widget
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.wrFoundry.enums.Commission
import scripts.wrFoundry.tasks.ExecutableTask
import kotlin.jvm.optionals.getOrNull

class SetupMould(val commission: Commission) : ExecutableTask {
    override fun shouldExecute(): Boolean {
        return true
    }

    override fun execute(): Boolean {
        if (!this.shouldExecute()) {
            return false
        }
        
        Logger("[Commission]").warn("Commission: ${this.commission.commission}")
        Logger("[Commission]").warn("Forte: ${this.commission.combination.forte}")
        Logger("[Commission]").warn("Blade: ${this.commission.combination.blade}")
        Logger("[Commission]").warn("Tip: ${this.commission.combination.tip}")

        Logger("[SetupMould]").warn("Opening Mould Interface")
        openMouldInterface()

        Logger("[SetupMould]").warn("Pick Combination")
        pickCombination()

        Logger("[Commission]").warn("picked..")

        setMouldChoices()

        return true
    }

    // TODO, whenever WrFoundry is handling a new commission, we seem to open/close the mould interface
    // - Most likely due to a too quick checkup? But let's log some stuff and debug what's happening.
    private fun openMouldInterface(): Boolean {
        // Setup
        return Waiting.waitUntil(60_000) {
            Logger("[SetupMould]").warn("OpenMouldInterface:: Waiting loop inside")
            val mouldObject = Query.gameObjects()
                .nameContains("Mould jig")
                .findFirst()
                .getOrNull()

            Logger("[SetupMould]").warn("OpenMouldInterface:: MouldObj ${mouldObject}")

            Waiting.wait(FatigueResolver.getMilliseconds())

            if (null == mouldObject) {
                Logger("[SetupMould]").warn("Returning:: false, no mould object")
                return@waitUntil false
            }

            var interacted = mouldObject.interact("Setup")
            Logger("[SetupMould]").warn("OpenMouldInterface:: Interacted: ${interacted}")
            if (!interacted) {
                interacted = mouldObject.interact("Check")
            }

            Logger("[SetupMould]").warn("Did we interact? = ${interacted}")

            Waiting.wait(FatigueResolver.getMilliseconds())

            val mouldInterface = Query.widgets()
                .inIndexPath(718)
                .findFirst()
                .getOrNull()

            Logger("[SetupMould]").warn("OpenMouldInterface:: mouldInterface: ${mouldInterface}")

            interacted && mouldInterface?.isVisible() == true
        }
    }

    private fun pickCombination() {
        Logger("[SetupMould@pickCombination]").warn("Pick Combinations!")
        this.pickBestTip()
        Waiting.wait(FatigueResolver.getMilliseconds())
        Logger("[SetupMould]").warn("Best Tip selected")

        this.pickBestBlade()
        Waiting.wait(FatigueResolver.getMilliseconds())
        Logger("[SetupMould]").warn("Best Blade selected")

        this.pickBestForte()
        Logger("[SetupMould]").warn("Best Forte selected")
    }

    private fun setMouldChoices(): Boolean {
        return Waiting.waitUntil(20_000) {
            val setMould = Query.widgets().inRoots(718)
                .isDepth(2)
                .inIndexPath(718, 34)
                .findFirst()
                .getOrNull()

            Waiting.wait(FatigueResolver.getMilliseconds())

            if (null == setMould) {
                return@waitUntil false
            }

            return@waitUntil setMould.click()
        }
    }

    private fun pickBestForte(): Boolean? {
        this.findWidgetByName("Forte")?.click()

        Waiting.wait(FatigueResolver.getMilliseconds())

        this.findAndScroll(this.commission.combination.forte)
        Waiting.wait(FatigueResolver.getMilliseconds())

        this.findWidgetByName(this.commission.combination.forte)?.click()
        Waiting.wait(FatigueResolver.getMilliseconds())

        return this.findWidgetByName(this.commission.combination.forte)?.click()
    }

    private fun pickBestBlade(): Boolean {
        this.findWidgetByName("Blades")?.click()

        Waiting.wait(FatigueResolver.getMilliseconds())

        this.findAndScroll(this.commission.combination.blade)
        Waiting.wait(FatigueResolver.getMilliseconds())

        return this.findWidgetByName(this.commission.combination.blade)?.click() == true
    }

    private fun pickBestTip(): Boolean {
        this.findWidgetByName("Tips")?.click()

        Waiting.wait(FatigueResolver.getMilliseconds())
        this.findAndScroll(this.commission.combination.tip)

        return this.findWidgetByName(this.commission.combination.tip)?.click() == true
    }

    fun findAndScroll(findName: String): Boolean {
        return Waiting.waitUntil(30_000) {
            var result = false
            val scrollBar = Query.widgets()
                .inRoots(718)
                .inIndexPath(718, 11, 1)
                .isDepth(3)
                .findFirst()
                .getOrNull()

            if (null == scrollBar) {
                Waiting.wait(FatigueResolver.getMilliseconds())
                return@waitUntil false
            }

            val widget = this.findMouldByName(findName)

            if (widget != null) {
                Logger("[findAndScroll]").warn("dragging!")
                scrollBar.dragTo(widget)

                result = widget.isVisible
            }

            result
        }
    }

    fun findWidgetByName(findName: String): Widget? {
        // When logging this, it was very slow. Perhaps now without it isn't?
        // Other-wise we need to adjust.
        val result = Query.widgets()
            .inRoots(718)
            .inIndexPath(718, 12) // for moulds, we should dig into 718, 9..?
            .isDepth(3)
            .nameContains(findName)
            .findFirst()
            .getOrNull()

        return result
    }

    fun findMouldByName(findName: String): Widget? {
        // When logging this, it was very slow. Perhaps now without it isn't?
        // Other-wise we need to adjust.
        val result = Query.widgets()
            .inRoots(718)
            .inIndexPath(718, 9) // for moulds, we should dig into 718, 9..?
            .isDepth(3)
            .textContains(findName)
            .findFirst()
            .getOrNull()

        Logger("[FindByMouldName]").warn("Res: $result")
        return result
    }

    fun extractComponentName(input: String): String? {
        // Regex to match the text between <col=...> and </col>
        val regex = """<col=[^>]+>(.*?)</col>""".toRegex()
        val match = regex.find(input)
        return match?.groups?.get(1)?.value // Get the captured group
    }
}