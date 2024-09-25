package scripts.wrCrafting.gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import scripts.wrCrafting.gui.methods.Gemstones

class GUI {

    @Composable
    @Preview
    fun App(settings: ScriptSettings) {
        MaterialTheme {
            Column(
                modifier = Modifier.padding(16.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MethodDropdown(settings) // this sets the settings.method
                Spacer(modifier = Modifier.padding(16.dp))

                if (settings.getMethods()[settings.method]?.hasMaterialOptions == true) {
                    MaterialDropdown(settings)
                    Spacer(modifier = Modifier.padding(16.dp))
                }

                BatchBuy(settings)

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text("method: ${settings.method}")
                    Text("gem: ${settings.gemMaterial}")
                    Text("batchbuy: ${settings.batchBuy}")
                }

            }
        }
    }

    @Composable
    fun BatchBuy(settings: ScriptSettings) {
        val (selectedNumber, setSelectedNumber) = remember { mutableStateOf(settings.batchBuy) }

        Box {
            OutlinedTextField(
                value = selectedNumber.toString(),
                onValueChange = { newValue ->
                    val newNumber = newValue.toIntOrNull() ?: 0

                    if (newValue.isNotEmpty() || newValue.all { it.isDigit() }) {
                        setSelectedNumber(newNumber)
                        settings.batchBuy = newNumber
                    }
                },
                label = { Text("Batch buying (leave 0 to opt-out)") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier
                    .padding(1.dp)
                    .zIndex(1f) // Ensure OutlinedTextField is on top
            )
        }
    }

    @Composable
    fun MethodDropdown(settings: ScriptSettings) {
        val options = settings.getMethods()
        print(options)

        val (expanded, setExpanded) = remember { mutableStateOf(false) }
        val (selectedKey, setSelectedKey) = remember { mutableStateOf(options.keys.firstOrNull() ?: "") }
        val selectedOption = options[selectedKey]?.name

        Box {
            OutlinedTextField(
                value = selectedOption ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Training method") },
                trailingIcon = {
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .padding(1.dp)
                    .zIndex(1f) // Ensure OutlinedTextField is on top
            )

            // Box limited to parent box size to enclose the clickable surface and adjust its size and alignment relative to the parent box size
            Box(
                modifier = Modifier.matchParentSize()
                    .border(1.dp, Color.Red) // just for debug display purposes
                    .zIndex(1.5f) // above the text field but below the dropdown menu
            ) {
                // Surface to capture clicks
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                        .align(Alignment.BottomCenter)
                        .clickable { setExpanded(!expanded) }
                        .zIndex(1.75f) // slightly above but less than the dropdown menu
                        .alpha(0.5f) // just for debug display purposes
                        .border(1.dp, Color.Green) // just for debug display purposes
                ) {
                    // Empty surface to capture clicks and dismiss the dropdown
                }
            }

            // DropdownMenu with higher zIndex to ensure it appears above other components
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { setExpanded(false) },
                modifier = Modifier.zIndex(2f) // Ensure DropdownMenu is on top
            ) {
                options.forEach { (key, value) ->
                    DropdownMenuItem(onClick = {
                        setSelectedKey(key)
                        setExpanded(false)
                        settings.method = key
                    }) {
                        Text(value.name)
                    }
                }
            }
        }
    }

    @Composable
    fun MaterialDropdown(settings: ScriptSettings) {
        val options = Gemstones().getMaterials()

        val (expanded, setExpanded) = remember { mutableStateOf(false) }
        val (selectedKey, setSelectedKey) = remember { mutableStateOf(options.keys.firstOrNull() ?: "") }
        val selectedOption = options[selectedKey]?.uncutStoneName

        Box {
            OutlinedTextField(
                value = selectedOption ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Material to cut") },
                trailingIcon = {
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .padding(1.dp)
                    .zIndex(1f) // Ensure OutlinedTextField is on top
            )

            // Box limited to parent box size to enclose the clickable surface and adjust its size and alignment relative to the parent box size
            Box(
                modifier = Modifier.matchParentSize()
                    .border(1.dp, Color.Red) // just for debug display purposes
                    .zIndex(1.5f) // above the text field but below the dropdown menu
            ) {
                // Surface to capture clicks
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                        .align(Alignment.BottomCenter)
                        .clickable { setExpanded(!expanded) }
                        .zIndex(1.75f) // slightly above but less than the dropdown menu
                        .alpha(0.5f) // just for debug display purposes
                        .border(1.dp, Color.Green) // just for debug display purposes
                ) {
                    // Empty surface to capture clicks and dismiss the dropdown
                }
            }

            // DropdownMenu with higher zIndex to ensure it appears above other components
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { setExpanded(false) },
                modifier = Modifier.zIndex(2f) // Ensure DropdownMenu is on top
            ) {
                options.forEach { (key, value) ->
                    DropdownMenuItem(onClick = {
                        setSelectedKey(key)
                        setExpanded(false)
                        settings.gemMaterial = key
                    }) {
                        Text("${value.uncutStoneName} (${value.minimumLevel}+)")
                    }
                }
            }
        }

    }
}
