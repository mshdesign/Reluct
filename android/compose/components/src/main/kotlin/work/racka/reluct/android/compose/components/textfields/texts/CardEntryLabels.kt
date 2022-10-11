package work.racka.reluct.android.compose.components.textfields.texts

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun EntryHeading(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = LocalContentColor.current
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.titleLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = color
    )
}

@Composable
fun EntryDescription(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = LocalContentColor.current
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        color = color
    )
}