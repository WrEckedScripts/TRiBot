package scripts.wrCrafting.gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MethodDropdown()
            Spacer(modifier = Modifier.padding(16.dp))
            val (showContent, setShowContent) = remember { mutableStateOf(false) }

            Button(onClick = { setShowContent(!showContent) }) {
                Text("Click me!")
            }
            if (showContent) Text("Content is now visible!")
        }
    }
}

@Composable
fun MethodDropdown() {
    val options = listOf(
        "Gemstones (cutting)",
        "Molton glass (furnace)"
    )

    val (expanded, setExpanded) = remember { mutableStateOf(false) }
    val (selected, setSelected) = remember { mutableStateOf(options[0]) }

    Box {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Training method") },
            trailingIcon = {
                Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
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
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    setSelected(option)
                    setExpanded(false)
                }) {
                    Text(option)
                }
            }
        }
    }

}
