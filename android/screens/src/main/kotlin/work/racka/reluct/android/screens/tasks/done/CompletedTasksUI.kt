package work.racka.reluct.android.screens.tasks.done

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import work.racka.reluct.android.compose.components.buttons.ReluctFloatingActionButton
import work.racka.reluct.android.compose.components.cards.headers.TaskGroupHeadingHeader
import work.racka.reluct.android.compose.components.cards.task_entry.EntryType
import work.racka.reluct.android.compose.components.cards.task_entry.TaskEntry
import work.racka.reluct.android.compose.components.images.LottieAnimationWithDescription
import work.racka.reluct.android.compose.components.util.BarsVisibility
import work.racka.reluct.android.compose.components.util.rememberScrollContext
import work.racka.reluct.android.compose.components.util.slideInVerticallyFadeReversed
import work.racka.reluct.android.compose.components.util.slideOutVerticallyFadeReversed
import work.racka.reluct.android.compose.theme.Dimens
import work.racka.reluct.android.screens.R
import work.racka.reluct.common.model.domain.tasks.Task
import work.racka.reluct.common.model.states.tasks.CompletedTasksState

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CompletedTasksUI(
    modifier: Modifier = Modifier,
    mainScaffoldPadding: PaddingValues,
    barsVisibility: BarsVisibility,
    scaffoldState: ScaffoldState,
    uiState: CompletedTasksState,
    onTaskClicked: (task: Task) -> Unit,
    onAddTaskClicked: (task: Task?) -> Unit,
    onToggleTaskDone: (isDone: Boolean, task: Task) -> Unit,
    fetchMoreData: () -> Unit,
) {
    val listState = rememberLazyListState()
    val scrollContext = rememberScrollContext(listState = listState)

    if (scrollContext.isBottom && uiState.shouldUpdateData
        && uiState !is CompletedTasksState.Loading
    ) {
        fetchMoreData()
    }

    // Need to evaluate recomposition overhead when user it at the
    // top of the list
    if (scrollContext.isTop) {
        barsVisibility.bottomBar.show()
    } else {
        barsVisibility.bottomBar.hide()
    }

    val mainScaffoldBottomPadding by remember(mainScaffoldPadding) {
        derivedStateOf {
            mainScaffoldPadding.calculateBottomPadding()
        }
    }

    val snackbarModifier = if (scrollContext.isTop) Modifier
    else Modifier.navigationBarsPadding()

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
        floatingActionButton = {
            AnimatedVisibility(
                visible = scrollContext.isTop,
                enter = slideInVerticallyFadeReversed(),
                exit = slideOutVerticallyFadeReversed()
            ) {
                ReluctFloatingActionButton(
                    modifier = Modifier
                        .padding(bottom = mainScaffoldBottomPadding),
                    buttonText = stringResource(R.string.new_task_button_text),
                    contentDescription = stringResource(R.string.add_icon),
                    icon = Icons.Rounded.Add,
                    onButtonClicked = {
                        onAddTaskClicked(null)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = Dimens.MediumPadding.size),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = uiState is CompletedTasksState.Loading &&
                        uiState.tasksData.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = mainScaffoldBottomPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            // Show Empty Graphic
            if (uiState.tasksData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = mainScaffoldBottomPadding),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimationWithDescription(
                        lottieResId = R.raw.no_task_animation,
                        imageSize = 200.dp,
                        description = stringResource(R.string.no_tasks_text)
                    )
                }
            } else { // Show Pending Tasks
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = listState,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement
                        .spacedBy(Dimens.SmallPadding.size)
                ) {

                    uiState.tasksData.forEach { taskGroup ->
                        stickyHeader {
                            TaskGroupHeadingHeader(text = taskGroup.key)
                        }
                        items(
                            items = taskGroup.value,
                            key = { it.id }
                        ) { item ->
                            TaskEntry(
                                task = item,
                                entryType = EntryType.CompletedTask,
                                onEntryClick = { onTaskClicked(item) },
                                onCheckedChange = { onToggleTaskDone(it, item) }
                            )
                        }
                    }

                    // Loading when fetching more data
                    item {
                        if (uiState is CompletedTasksState.Loading &&
                            uiState.tasksData.isNotEmpty()
                        ) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .padding(Dimens.MediumPadding.size)
                            )
                        }
                    }

                    // Bottom Space for spaceBy
                    // Needed so that the load more indicator is shown
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
    }
}