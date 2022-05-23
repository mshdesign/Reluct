package work.racka.reluct.android.compose.components.tab.screentime

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import work.racka.reluct.android.compose.destinations.ScreenTimeDestinations

@Composable
internal fun ScreenTimeTabIndicator(
    modifier: Modifier = Modifier,
    tabPositions: List<TabPosition>,
    tabPage: ScreenTimeDestinations,
) {
    val transition = updateTransition(
        targetState = tabPage,
        label = "Screen Time Tab Indicator"
    )
    val indicatorLeft by transition.animateDp(
        transitionSpec = {
            if (ScreenTimeDestinations.Statistics
                isTransitioningTo ScreenTimeDestinations.Limits
            ) {
                // Indicator moves to the right.
                // Low stiffness spring for the left edge so it moves slower than the right edge.
                spring(stiffness = Spring.StiffnessVeryLow)
            } else {
                // Indicator moves to the left.
                // Medium stiffness spring for the left edge so it moves faster than the right edge.
                spring(stiffness = Spring.StiffnessMedium)
            }
        },
        label = "Indicator Left"
    ) { page ->
        tabPositions[page.ordinal].left
    }

    val indicatorRight by transition.animateDp(
        transitionSpec = {
            if (ScreenTimeDestinations.Limits
                isTransitioningTo ScreenTimeDestinations.Statistics
            ) {
                // Indicator moves to the right
                // Medium stiffness spring for the right edge so it moves faster than the left edge.
                spring(stiffness = Spring.StiffnessMedium)
            } else {
                // Indicator moves to the left.
                // Low stiffness spring for the right edge so it moves slower than the left edge.
                spring(stiffness = Spring.StiffnessVeryLow)
            }
        },
        label = "Indicator right"
    ) { page ->
        tabPositions[page.ordinal].right
    }

    Box(
        modifier = modifier
            .zIndex(1f)
            .fillMaxSize()
            .wrapContentSize(align = Alignment.CenterStart)
            .offset(x = indicatorLeft)
            .width(indicatorRight - indicatorLeft)
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
    )
}