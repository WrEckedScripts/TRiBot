package scripts.wrMule

import org.tribot.script.sdk.Login
import org.tribot.script.sdk.ScriptListening
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.script.TribotScript
import org.tribot.script.sdk.script.TribotScriptManifest

@TribotScriptManifest(
    name = "WrMule 1.0.0",
    description = "Muling utility script",
    category = "Tools",
    author = "WrEcked"
)
class MuleScript : TribotScript {


    override fun execute(p0: String) {
        val client = ServerSocket()
        client.start()

        // Just to mimic some event that triggers a mule message
        ScriptListening.addMouseClickListener { point, i, b ->
            println("Clicked")
        }


        val tree = behaviorTree {
            repeatUntil(BehaviorTreeStatus.KILL) {
                selector {
                    condition { Login.isLoggedIn() }
                    perform {
                        Thread.sleep(10_000)

                    }
                }
            }
        }

        tree.tick()
    }
}