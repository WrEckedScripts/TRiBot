package scripts.wrFoundry.tasks.prepare

import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.Widget
import scripts.utils.Logger
import scripts.utils.antiban.FatigueResolver
import scripts.wrFoundry.enums.Commission
import kotlin.jvm.optionals.getOrNull

class SetupMould(val commission: Commission) {

    fun execute(): Boolean {
        Logger("[Commission]").warn("Commission: ${this.commission.commission}")
        Logger("[Commission]").warn("Forte: ${this.commission.combination.forte}")
        Logger("[Commission]").warn("Blade: ${this.commission.combination.blade}")
        Logger("[Commission]").warn("Tip: ${this.commission.combination.tip}")

        openMouldInterface()
        Logger("[Commission]").warn("opened...")

        pickCombination()
        Logger("[Commission]").warn("picked..")

        return true
    }

    private fun openMouldInterface(): Boolean {
        // Setup
        return Waiting.waitUntil(60_000) {
            val mouldObject = Query.gameObjects()
                .nameContains("Mould jig")
                .findFirst()
                .getOrNull()

            Waiting.wait(FatigueResolver.getMilliseconds())

            if (null == mouldObject) {
                return@waitUntil false
            }

            val interacted = mouldObject.interact("Setup")
            if (!interacted) {
                mouldObject.interact("Check")
            }

            Waiting.wait(FatigueResolver.getMilliseconds())

            val mouldInterface = Query.widgets()
                .inIndexPath(718)
                .findFirst()
                .getOrNull()

            mouldInterface?.isVisible() == true
        }
    }

    fun pickCombination(): Boolean {
        Logger("[PickCombo's]").warn("Let's pick some!")
        this.pickBestTip()

        Waiting.wait(FatigueResolver.getMilliseconds())

        this.pickBestBlade()
        Waiting.wait(FatigueResolver.getMilliseconds())

        this.pickBestForte()

        val setMould = Query.widgets().inRoots(718)
            .isDepth(2)
            .inIndexPath(718, 34)
            .findFirst()
            .get()
            .click()

        Waiting.wait(FatigueResolver.getMilliseconds())

        return setMould
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
        return Waiting.waitUntil {
            var result = false
            val scrollBar = Query.widgets()
                .inRoots(718)
                .inIndexPath(718, 11, 1)
                .isDepth(3)
                .findFirst()
                .get()

            val widget = this.findMouldByName(findName)

            if (widget != null) {
                Logger("[findAndScroll]").warn("dragging!")
                scrollBar.dragTo(widget)

                //TODO this doesn't do anything anymore, afaik..
                Waiting.waitUntil(5_000) {
                    widget.scrollTo()
                    widget.isVisible
                }

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