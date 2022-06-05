package work.racka.reluct.android.compose.components.cards.statistics.tasks

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import work.racka.reluct.android.compose.components.R
import work.racka.reluct.android.compose.components.cards.statistics.StatisticsBarChartCard
import work.racka.reluct.android.compose.components.cards.statistics.StatisticsChartState
import work.racka.reluct.barChart.BarChartData
import work.racka.reluct.common.model.domain.tasks.DailyTasksStats
import work.racka.reluct.common.model.util.time.Week

@Composable
fun TasksStatisticsCard(
    modifier: Modifier = Modifier,
    barChartState: StatisticsChartState<Map<Week, DailyTasksStats>>,
    barColor: Color = MaterialTheme.colorScheme.secondary
        .copy(alpha = .7f),
    selectedDayText: String,
    selectedDayTasksDone: Int,
    selectedDayTasksPending: Int,
    totalWeekTaskCount: Int,
    selectedDayIsoNumber: Int,
    onBarClicked: (Int) -> Unit,
    weekUpdateButton: @Composable () -> Unit,
) {

    val bars = remember(barChartState) {
        derivedStateOf {
            val tempList = mutableListOf<BarChartData.Bar>()
            barChartState.data.forEach { entry ->
                tempList.add(
                    BarChartData.Bar(
                        value = entry.value.completedTasksCount.toFloat(),
                        color = barColor,
                        label = entry.key.dayAcronym,
                        uniqueId = entry.key.isoDayNumber
                    )
                )
            }
            tempList.toList()
        }
    }

    StatisticsBarChartCard(
        modifier = modifier,
        bars = bars.value,
        selectedBarColor = MaterialTheme.colorScheme.primary,
        dataLoading = barChartState is StatisticsChartState.Loading,
        noDataText = stringResource(id = R.string.no_completed_tasks_text),
        selectedDayIsoNumber = selectedDayIsoNumber,
        onBarClicked = { onBarClicked(it) },
        topLeftText = {
            Text(
                text = selectedDayText,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = LocalContentColor.current
            )
        },
        topRightText = {
            Text(
                text = stringResource(
                    R.string.tasks_tally_text_arg,
                    selectedDayTasksDone,
                    selectedDayTasksPending + selectedDayTasksDone
                ),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        belowChartText = {
            Spacer(modifier = Modifier)
            Text(
                text = stringResource(R.string.weekly_task_count_arg, totalWeekTaskCount),
                style = MaterialTheme.typography.titleLarge
                    .copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = LocalContentColor.current
            )
        },
        bodyContent = weekUpdateButton
    )
}