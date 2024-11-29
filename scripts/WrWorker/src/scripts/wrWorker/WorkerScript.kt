package scripts.wrMule

import org.tribot.script.sdk.Login
import org.tribot.script.sdk.ScriptListening
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.script.TribotScript
import org.tribot.script.sdk.script.TribotScriptManifest
import scripts.wrWorker.muling.ClientSocket
import scripts.wrWorker.muling.TestAction

@TribotScriptManifest(
    name = "WrWorker 1.0.0",
    description = "Muling test worker",
    category = "Tools",
    author = "WrEcked"
)
class WorkerScript : TribotScript {


    override fun execute(p0: String) {

        val client = ClientSocket()
        client.start()

        // Just to mimic some event that triggers a mule message
        ScriptListening.addMouseClickListener { point, i, b ->
            println("Clicked")
            client.sendMessage(TestAction())
            println("After click")
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