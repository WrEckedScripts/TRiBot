package scripts.wrBlastFurnace.gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

class GUI(private val onStartScript: () -> Unit) {

    @Composable
    @Preview
    fun App() {
        MaterialTheme {
            Box {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        // State to track scroll position
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(scrollState)
        ) {
            // Using a Row to display two sections side by side
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("General", style = MaterialTheme.typography.h6.copy(fontSize = 16.sp))  // Smaller font size
                    Spacer(modifier = Modifier.height(4.dp))  // Reduced spacer height
                    GeneralSection()
                }

                Spacer(modifier = Modifier.width(8.dp))  // Space between columns

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Player Profile",
                        style = MaterialTheme.typography.h6.copy(fontSize = 16.sp)
                    )  // Smaller font size
                    Spacer(modifier = Modifier.height(4.dp))  // Reduced spacer height
                    PlayerProfileSection()
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Coffer Upkeep", style = MaterialTheme.typography.h6.copy(fontSize = 16.sp))
                    Spacer(modifier = Modifier.height(4.dp))
                    CofferUpkeepSection()
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("Progression", style = MaterialTheme.typography.h6.copy(fontSize = 16.sp))
                    Spacer(modifier = Modifier.height(4.dp))
                    ProgressionSection()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        // Alternatively, if running in a non-Android environment, use:
                        println("")
                        println("======START=======")
                        println("barType: ${Settings.barType}")
                        println("world: ${Settings.world}")
                        println("staminaChecked: ${Settings.staminaChecked}")
                        println("zoom: ${Settings.zoom}")
                        println("scroll: ${Settings.scroll}")
                        println("closingChecked: ${Settings.closingChecked}")
                        println("chatbox: ${Settings.chatbox}")
                        println("preWalkChecked: ${Settings.preWalkChecked}")
                        println("minAmount: ${Settings.minAmount}")
                        println("maxAmount: ${Settings.maxAmount}")
                        println("discordUrl: ${Settings.discordUrl}")
                        println("interval: ${Settings.interval}")
                        println("======END=======")
                    },
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("Verify", fontSize = 12.sp)
                }

                Button(onClick = { /* handle save */ }, modifier = Modifier.height(30.dp)) {
                    Text("Save", fontSize = 12.sp)
                }

                Button(onClick = { /* handle load */ }, modifier = Modifier.height(30.dp)) {
                    Text("Load", fontSize = 12.sp)
                }

                Button(onClick = { /* handle run */ }, modifier = Modifier.height(30.dp)) {
                    Text("Run", fontSize = 12.sp)
                }
            }
        }
    }

    /**
     * Sections
     */

    @Composable
    fun GeneralSection() {
        val barTypes = mapOf(
            "Steel bar" to "Steel bar",
            "Iron bar" to "Iron bar",
            "Bronze bar" to "Bronze bar"
        )

        var pickedBarType by remember { mutableStateOf(Settings.barType) }

        Column {

            DropdownMenu(
                label = "Bar type",
                options = barTypes,
                selectedKey = pickedBarType,
                onOptionSelected = { selectedBarType ->
                    pickedBarType = selectedBarType
                    Settings.barType = selectedBarType
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))


            var world by remember { mutableStateOf(Settings.world) }
            OutlinedTextField(
                value = world,
                onValueChange = { newWorld ->
                    world = newWorld
                    Settings.world = newWorld
                },
                label = { Text("World") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.body1.copy(fontSize = 12.sp)
            )

            Spacer(modifier = Modifier.height(8.dp))


            var staminaChecked by remember { mutableStateOf(Settings.staminaChecked) }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = staminaChecked,
                    onCheckedChange = { checked ->
                        staminaChecked = checked
                        Settings.staminaChecked = checked
                    }
                )
                Text("Use stamina potions", fontSize = 12.sp)
            }
        }
    }

    @Composable
    fun PlayerProfileSection() {
        val zoomOptions = mapOf("mouse" to "Mouse", "keyboard" to "Keyboard")
        val scrollOptions = mapOf("mouse" to "Mouse", "keyboard" to "Keyboard")
        val chatboxOptions = mapOf("hidden" to "Hidden", "visible" to "Visible")

        var selectedZoom by remember { mutableStateOf(Settings.zoom) }
        var selectedScroll by remember { mutableStateOf(Settings.scroll) }
        var selectedChatbox by remember { mutableStateOf(Settings.chatbox) }

        Column {

            DropdownMenu(
                label = "Zoom",
                options = zoomOptions,
                selectedKey = selectedZoom,
                onOptionSelected = { zoom ->
                    selectedZoom = zoom
                    Settings.zoom = zoom
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))


            DropdownMenu(
                label = "Scroll",
                options = scrollOptions,
                selectedKey = selectedScroll,
                onOptionSelected = { scroll ->
                    selectedScroll = scroll
                    Settings.scroll = scroll
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))


            var closingChecked by remember { mutableStateOf(Settings.closingChecked) }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = closingChecked,
                    onCheckedChange = { checked ->
                        closingChecked = checked
                        Settings.closingChecked = checked
                    }
                )
                Text("Use ESC to close bank windows", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))


            DropdownMenu(
                label = "Chatbox",
                options = chatboxOptions,
                selectedKey = selectedChatbox,
                onOptionSelected = { chatbox ->
                    selectedChatbox = chatbox
                    Settings.chatbox = chatbox
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))


            var preWalkChecked by remember { mutableStateOf(Settings.preWalkChecked) }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = preWalkChecked,
                    onCheckedChange = { checked ->
                        preWalkChecked = checked
                        Settings.preWalkChecked = checked
                    }
                )
                Text("Pre-walk to dispenser", fontSize = 12.sp)
            }
        }
    }

    @Composable
    fun CofferUpkeepSection() {
        var minAmount by remember { mutableStateOf(Settings.minAmount) }
        var maxAmount by remember { mutableStateOf(Settings.maxAmount) }

        Column {

            OutlinedTextField(
                value = minAmount,
                onValueChange = { newValue ->
                    minAmount = newValue
                    Settings.minAmount = newValue
                },
                label = { Text("Min amount") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.body1.copy(fontSize = 12.sp)
            )

            Spacer(modifier = Modifier.height(8.dp))


            OutlinedTextField(
                value = maxAmount,
                onValueChange = { newValue ->
                    maxAmount = newValue
                    Settings.maxAmount = newValue
                },
                label = { Text("Max amount") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.body1.copy(fontSize = 12.sp)
            )
        }
    }

    @Composable
    fun ProgressionSection() {
        var discordUrl by remember { mutableStateOf(Settings.discordUrl) }
        var interval by remember { mutableStateOf(Settings.interval) }

        Column {

            OutlinedTextField(
                value = discordUrl,
                onValueChange = { newValue ->
                    discordUrl = newValue
                    Settings.discordUrl = newValue
                },
                label = { Text("Discord URL") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.body1.copy(fontSize = 12.sp)
            )

            Spacer(modifier = Modifier.height(8.dp))


            OutlinedTextField(
                value = interval,
                onValueChange = { newValue ->
                    interval = newValue
                    Settings.interval = newValue
                },
                label = { Text("Screenshot Interval (in minutes)") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.body1.copy(fontSize = 12.sp)
            )
        }
    }

    /**
     * Re-usables
     */

    @Composable
    fun <K> DropdownMenu(
        label: String,
        options: Map<K, String>,
        selectedKey: K?,
        onOptionSelected: (K) -> Unit,
        modifier: Modifier = Modifier
    ) {
        var (expanded, setExpanded) = remember { mutableStateOf(false) }
        var selectedOption by remember { mutableStateOf(options[selectedKey]) }

        Box(modifier = modifier) {
            OutlinedTextField(
                value = selectedOption ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text(label, fontSize = 12.sp) },
                textStyle = TextStyle(fontSize = 12.sp),
                trailingIcon = {
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(1.dp)
                    .clickable { expanded = !expanded }
                    .zIndex(1f)
            )

            Box(
                modifier = Modifier.matchParentSize()
                    .zIndex(1.5f)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                        .align(Alignment.BottomCenter)
                        .clickable { setExpanded(!expanded) }
                        .zIndex(1.75f)
                        .alpha(0.3f)
                ) {
                    // Empty surface for click capturing
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { setExpanded(false) },
                modifier = Modifier.zIndex(2f)
            ) {
                options.forEach { (key, value) ->
                    DropdownMenuItem(
                        onClick = {
                            selectedOption = value
                            setExpanded(false)
                            onOptionSelected(key)
                            expanded = false
                        }
                    ) {
                        Text(text = value, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    @Composable
    fun TextFieldWithLabel(label: String, value: String, onValueChange: (String) -> Unit) {
        Column {
            Text(text = label, fontSize = 12.sp)
            TextField(
                value = value,
                onValueChange = { onValueChange(it) },
                textStyle = MaterialTheme.typography.body1.copy(fontSize = 12.sp)
            )
        }
    }

    @Composable
    fun CheckboxWithLabel(label: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
        var checkedState by remember { mutableStateOf(isChecked) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = checkedState,
                onCheckedChange = { checkedState = it; onCheckedChange(it) }
            )
            Text(text = label, fontSize = 12.sp)
        }
    }
}
