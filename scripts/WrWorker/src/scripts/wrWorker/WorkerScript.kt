package scripts.wrMule

import org.tribot.script.sdk.Log
import org.tribot.script.sdk.Waiting
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.script.TribotScript
import org.tribot.script.sdk.script.TribotScriptManifest
import scripts.wrWorker.muling.ClientSocket
import scripts.wrWorker.muling.MuleTarget
import scripts.wrWorker.muling.Payload

@TribotScriptManifest(
    name = "WrWorker 1.0.0",
    description = "Muling test worker",
    category = "Tools",
    author = "WrEcked"
)
class WorkerScript : TribotScript {

    override fun execute(port: String) {

        val client = ClientSocket(port.toInt())
        client.start()

        val tree = behaviorTree {
            repeatUntil(BehaviorTreeStatus.KILL) {
                sequence {
                    selector {
                        condition { MuleTarget.hasActiveTarget }
                        condition {
                            client.sendMessage(Payload())
                            MuleTarget.hasActiveTarget = true
                            MuleTarget.attempts++
                            MuleTarget.hasActiveTarget
                        }
                    }

                    // dedicated mule-out sequence
                    // - deposit all items in bank
                    // - withdraw (noted) mule-out stock
                    // - move to MuleTarget

                    selector {
                        condition { !MuleTarget.hasActiveTarget }
                        condition {
                            Log.warn("Not muling object: ${MuleTarget}")
                            MuleTarget.hasActiveTarget = false
                            MuleTarget.attempts = 0
                            Waiting.wait(10_000)
                            true
                        }
                    }
                }
            }
        }

        tree.tick()
    }
}