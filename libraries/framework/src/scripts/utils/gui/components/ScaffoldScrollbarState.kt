package scripts.utils.gui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/* Written by IvanEOD 9/28/2022, at 9:52 AM */

class ScriptScaffoldScrollbarState(
    val lazyListState: LazyListState,
    val scrollState: ScrollState
)

@Composable
fun rememberScriptScaffoldScrollbarState(
    lazyListState: LazyListState = rememberLazyListState(),
    scrollState: ScrollState = rememberScrollState()
): ScriptScaffoldScrollbarState {
    return remember {
        ScriptScaffoldScrollbarState(
            lazyListState,
            scrollState
        )
    }
}
