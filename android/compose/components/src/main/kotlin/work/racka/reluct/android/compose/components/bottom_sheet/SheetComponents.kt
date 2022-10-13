package work.racka.reluct.android.compose.components.bottom_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import work.racka.reluct.android.compose.components.R
import work.racka.reluct.android.compose.theme.Dimens
import work.racka.reluct.android.compose.theme.ReluctAppTheme

@Composable
fun TopSheetSection(
    modifier: Modifier = Modifier,
    contentColor: Color = LocalContentColor.current,
    sheetTitle: String,
    rightButtonIcon: ImageVector? = null,
    rightButtonContentDescription: String? = null,
    onCloseClicked: () -> Unit,
    onRightButtonClicked: () -> Unit = { },
    closeButtonVisible: Boolean = true,
    rightButtonVisible: Boolean = false,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Dimens.SmallPadding.size))
        Box(
            modifier = Modifier
                .size(height = 5.dp, width = 32.dp)
                .background(
                    color = MaterialTheme.colorScheme.onBackground
                        .copy(alpha = .1f),
                    shape = CircleShape
                )
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            IconButton(
                onClick = onCloseClicked,
                enabled = closeButtonVisible
            ) {
                if (closeButtonVisible) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = stringResource(id = R.string.close_icon),
                        tint = contentColor
                    )
                }
            }

            Text(
                text = sheetTitle,
                style = MaterialTheme.typography.headlineSmall,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )


            IconButton(
                onClick = onRightButtonClicked,
                enabled = rightButtonVisible
            ) {
                if (rightButtonVisible && rightButtonIcon != null) {
                    Icon(
                        imageVector = rightButtonIcon,
                        contentDescription = rightButtonContentDescription,
                        tint = contentColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SmallPadding.size))
    }
}


@Preview
@Composable
private fun TopSheetPreview() {
    ReluctAppTheme {
        TopSheetSection(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            sheetTitle = "Sheet Title",
            onCloseClicked = { })
    }
}