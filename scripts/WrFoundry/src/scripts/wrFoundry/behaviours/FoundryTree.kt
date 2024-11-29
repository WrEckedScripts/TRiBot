package scripts.wrFoundry.behaviours

import org.tribot.script.sdk.Login
import org.tribot.script.sdk.frameworks.behaviortree.*

fun getFoundryTree() = behaviorTree {
    repeatUntil(BehaviorTreeStatus.KILL) {
        /**
         * Ensures that we're logged in, after we get disconnected for example.
         */
        selector {
            condition { Login.isLoggedIn() }
            condition {
                Login.login()
            }
        }
    }
}