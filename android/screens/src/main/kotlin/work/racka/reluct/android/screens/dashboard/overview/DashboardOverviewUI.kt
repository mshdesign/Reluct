package work.racka.reluct.android.screens.dashboard.overview

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import work.racka.reluct.android.compose.components.R
import work.racka.reluct.android.compose.components.buttons.ReluctButton
import work.racka.reluct.android.compose.components.cards.headers.ListGroupHeadingHeader
import work.racka.reluct.android.compose.components.cards.permissions.PermissionsCard
import work.racka.reluct.android.compose.components.cards.statistics.StatisticsChartState
import work.racka.reluct.android.compose.components.cards.statistics.piechart.DailyScreenTimePieChart
import work.racka.reluct.android.compose.components.cards.task_entry.EntryType
import work.racka.reluct.android.compose.components.cards.task_entry.TaskEntry
import work.racka.reluct.android.compose.components.images.LottieAnimationWithDescription
import work.racka.reluct.android.compose.components.util.BarsVisibility
import work.racka.reluct.android.compose.components.util.rememberScrollContext
import work.racka.reluct.android.compose.theme.Dimens
import work.racka.reluct.android.compose.theme.Shapes
import work.racka.reluct.android.screens.util.PermissionCheckHandler
import work.racka.reluct.android.screens.util.checkUsageAccessPermissions
import work.racka.reluct.android.screens.util.requestUsageAccessPermission
import work.racka.reluct.common.features.dashboard.overview.states.DashboardOverviewState
import work.racka.reluct.common.features.dashboard.overview.states.TodayScreenTimeState
import work.racka.reluct.common.features.dashboard.overview.states.TodayTasksState
import work.racka.reluct.common.model.domain.tasks.Task

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun DashboardOverviewUI(
    modifier: Modifier = Modifier,
    mainScaffoldPadding: PaddingValues,
    barsVisibility: BarsVisibility,
    scaffoldState: ScaffoldState,
    uiState: DashboardOverviewState,
    getUsageData: (isGranted: Boolean) -> Unit,
    openScreenTimeStats: () -> Unit,
    openPendingTask: (Task) -> Unit,
    onToggleTaskDone: (isDone: Boolean, task: Task) -> Unit
) {
    val listState = rememberLazyListState()
    val scrollContext = rememberScrollContext(listState = listState)

    //TODO: Need to evaluate recomposition overhead when user it at the
    // top of the list
    if (scrollContext.isTop) {
        barsVisibility.bottomBar.show()
    } else {
        barsVisibility.bottomBar.hide()
    }

    val pieChartState by remember(uiState.todayScreenTimeState) {
        derivedStateOf {
            val result = when (val screenTimeState = uiState.todayScreenTimeState) {
                is TodayScreenTimeState.Data -> {
                    StatisticsChartState.Success(screenTimeState.usageStats)
                }
                is TodayScreenTimeState.Loading -> {
                    StatisticsChartState.Loading(screenTimeState.usageStats)
                }
                is TodayScreenTimeState.Nothing -> {
                    StatisticsChartState.Empty(screenTimeState.usageStats)
                }
                else -> StatisticsChartState.Empty(screenTimeState.usageStats)
            }
            result
        }
    }

    var usagePermissionGranted by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }

    val context = LocalContext.current

    PermissionCheckHandler {
        if (!usagePermissionGranted) {
            usagePermissionGranted = checkUsageAccessPermissions(context)
            getUsageData(usagePermissionGranted)
        }
    }

    val snackbarModifier = if (scrollContext.isTop) {
        Modifier.padding(bottom = mainScaffoldPadding.calculateBottomPadding())
    } else Modifier.navigationBarsPadding()

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(hostState = it) { data ->
                Snackbar(
                    modifier = snackbarModifier,
                    shape = RoundedCornerShape(10.dp),
                    snackbarData = data,
                    backgroundColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    actionColor = MaterialTheme.colorScheme.primary,
                )
            }
        },
        backgroundColor = MaterialTheme.colorScheme.background,
    ) { padding ->

        Box(
            modifier = Modifier
                .animateContentSize()
                .padding(padding)
                .padding(horizontal = Dimens.MediumPadding.size)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                state = listState,
                verticalArrangement = Arrangement
                    .spacedBy(Dimens.SmallPadding.size)
            ) {
                // Top Space
                item {
                    Spacer(modifier = Modifier)
                }

                // Permission Card
                if (!usagePermissionGranted) {
                    item {
                        PermissionsCard(
                            imageSlot = {
                                LottieAnimationWithDescription(
                                    iterations = Int.MAX_VALUE,
                                    lottieResId = R.raw.no_permission,
                                    imageSize = 200.dp,
                                    description = null
                                )
                            },
                            permissionDetails = stringResource(R.string.usage_permissions_details),
                            onPermissionRequest = { openDialog.value = true }
                        )
                    }
                }

                // Pie Chart
                item {
                    DailyScreenTimePieChart(
                        pieChartState = pieChartState,
                        onClick = openScreenTimeStats
                    )
                }

                // Tasks
                stickyHeader {
                    ListGroupHeadingHeader(text = stringResource(R.string.upcoming_tasks_text))
                }

                if (uiState.todayTasksState is TodayTasksState.Loading) {
                    item { LinearProgressIndicator() }
                }

                // No Tasks Animation
                if (uiState.todayTasksState.pending.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            LottieAnimationWithDescription(
                                lottieResId = R.raw.no_task_animation,
                                imageSize = 200.dp,
                                description = stringResource(R.string.no_tasks_text)
                            )
                        }
                    }
                }

                // Upcoming Tasks
                items(items = uiState.todayTasksState.pending, key = { it.id }) { item ->
                    TaskEntry(
                        modifier = Modifier.animateItemPlacement(),
                        playAnimation = true,
                        task = item,
                        entryType = EntryType.TasksWithOverdue,
                        onEntryClick = { openPendingTask(item) },
                        onCheckedChange = { onToggleTaskDone(it, item) }
                    )
                }

                // Bottom Space for spaceBy
                item {
                    Spacer(
                        modifier = Modifier
                            .padding(bottom = Dimens.ExtraLargePadding.size)
                            .navigationBarsPadding()
                    )
                }
            }
        }
    }

    // Permission Dialog
    // Go To Usage Access Dialog
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = {
                Text(text = stringResource(R.string.open_settings_dialog_title))
            },
            text = {
                Text(text = stringResource(R.string.usage_permissions_rationale_dialog_text))
            },
            confirmButton = {
                ReluctButton(
                    buttonText = stringResource(R.string.ok),
                    icon = null,
                    shape = Shapes.large,
                    buttonColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    onButtonClicked = {
                        openDialog.value = false
                        requestUsageAccessPermission(context)
                    }
                )
            },
            dismissButton = {
                ReluctButton(
                    buttonText = stringResource(R.string.cancel),
                    icon = null,
                    shape = Shapes.large,
                    buttonColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    onButtonClicked = { openDialog.value = false }
                )
            }
        )
    }
}