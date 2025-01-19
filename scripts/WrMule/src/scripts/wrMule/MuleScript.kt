package scripts.wrMule

import org.tribot.script.sdk.Log
import org.tribot.script.sdk.Login
import org.tribot.script.sdk.MessageListening
import org.tribot.script.sdk.frameworks.behaviortree.*
import org.tribot.script.sdk.query.Query
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

        MessageListening.addTradeRequestListener { name: String ->
            if (name == WorkerTarget.targetName) {
                Query.players().nameEquals(WorkerTarget.targetName)
                    .findBestInteractable()
                    .get()
                    .interact("Trade")
            }
        }

        val tree = behaviorTree {
            repeatUntil(BehaviorTreeStatus.KILL) {
                sequence {
                    //TODO only if a mule request comes in
                    selector {
                        condition { Login.isLoggedIn() }
                        condition { Login.login() }
                    }


                    selector {
                        condition { WorkerTarget.hasActiveTarget == false }
                        perform {
                            Log.debug("Looks like we got a target?")
                        }
                    }
                }
            }
        }

        tree.tick()
    }
}