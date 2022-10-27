package work.racka.reluct.android.compose.components.bottom_sheet.add_edit_task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import work.racka.reluct.android.compose.components.R
import work.racka.reluct.android.compose.components.bottom_sheet.task_labels.TaskLabelsSelectCard
import work.racka.reluct.android.compose.components.buttons.ReluctButton
import work.racka.reluct.android.compose.components.cards.settings.EntryWithCheckbox
import work.racka.reluct.android.compose.components.textfields.ReluctTextField
import work.racka.reluct.android.compose.theme.Dimens
import work.racka.reluct.android.compose.theme.Shapes
import work.racka.reluct.common.model.domain.tasks.EditTask
import work.racka.reluct.common.model.util.time.TimeUtils.getLocalDateTimeWithCorrectTimeZone
import work.racka.reluct.common.model.util.time.TimeUtils.plus

// This provided here so that it doesn't leak DateTime dependencies to the
// screens modules.
@Composable
fun LazyColumnAddEditTaskFields(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    task: EditTask,
    saveButtonText: String,
    discardButtonText: String,
    onReminderSet: (reminderLocalDateTime: String) -> Unit = { },
    onUpdateTask: (EditTask) -> Unit,
    onSave: (EditTask) -> Unit,
    onDiscard: () -> Unit,
    onEditLabels: () -> Unit
) {
    val focusRequest = LocalFocusManager.current

    var setReminder by remember(task) {
        val reminderPresent = !task.reminderLocalDateTime.isNullOrBlank()
        mutableStateOf(reminderPresent)
    }

    var taskTitleError by remember { mutableStateOf(false) }

    var reminderTimePillError by remember { mutableStateOf(false) }

    val taskDueTime by remember(task.dueDateLocalDateTime) {
        derivedStateOf {
            if (task.dueDateLocalDateTime.isNotBlank()) {
                getLocalDateTimeWithCorrectTimeZone(
                    dateTime = task.dueDateLocalDateTime,
                    originalTimeZoneId = task.timeZoneId
                )
            } else Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .plus(hours = 1)
        }
    }

    val reminderTime by remember(task.reminderLocalDateTime) {
        derivedStateOf {
            task.reminderLocalDateTime?.let { timeString ->
                if (timeString.isNotBlank()) {
                    getLocalDateTimeWithCorrectTimeZone(
                        dateTime = timeString,
                        originalTimeZoneId = task.timeZoneId
                    )
                } else taskDueTime.plus(hours = 1)
            } ?: taskDueTime.plus(hours = 1)
        }
    }

    LazyColumn(
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement
            .spacedBy(Dimens.MediumPadding.size),
        modifier = modifier
            .animateContentSize()
            .fillMaxWidth()
    ) {
        item {
            ReluctTextField(
                value = task.title,
                hint = stringResource(R.string.task_title_hint),
                isError = taskTitleError,
                errorText = stringResource(R.string.task_title_error_text),
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusRequest.moveFocus(FocusDirection.Next) }
                ),
                onTextChange = { text ->
                    taskTitleError = false
                    onUpdateTask(task.copy(title = text))
                }
            )
        }

        item {
            ReluctTextField(
                modifier = Modifier
                    .height(200.dp),
                value = task.description ?: "",
                hint = stringResource(R.string.description_hint),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                onTextChange = { text ->
                    onUpdateTask(task.copy(description = text))
                }
            )
        }

        // Task Due Time
        item {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(R.string.task_labels_text),
                style = MaterialTheme.typography.titleMedium,
                color = LocalContentColor.current,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(Dimens.SmallPadding.size))
            TaskLabelsSelectCard(labels = task.taskLabels, onEditLabels = onEditLabels)
        }

        // Task Due Time
        item {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(R.string.task_to_be_done_at_text),
                style = MaterialTheme.typography.titleMedium,
                color = LocalContentColor.current,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(Dimens.SmallPadding.size))
            DateTimePills(
                currentLocalDateTime = taskDueTime,
                onLocalDateTimeChange = { dateTime ->
                    onUpdateTask(task.copy(dueDateLocalDateTime = dateTime.toString()))
                }
            )
        }

        // Task Reminder
        item {
            EntryWithCheckbox(
                isChecked = setReminder,
                title = stringResource(R.string.set_reminder),
                description = stringResource(R.string.set_reminder_desc),
                onCheckedChanged = { checked ->
                    setReminder = checked
                    val newTask = task.copy(
                        reminderLocalDateTime =
                        if (setReminder) taskDueTime.plus(hours = 1).toString()
                        else null
                    )
                    onUpdateTask(newTask)
                }
            )
        }

        item {
            AnimatedVisibility(
                visible = setReminder,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = stringResource(R.string.reminder_at),
                        style = MaterialTheme.typography.titleMedium,
                        color = LocalContentColor.current,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(Dimens.SmallPadding.size))
                    DateTimePills(
                        hasError = reminderTimePillError,
                        errorText = stringResource(id = R.string.reminder_time_error_text),
                        currentLocalDateTime = reminderTime,
                        onLocalDateTimeChange = { dateTime ->
                            reminderTimePillError = dateTime <= taskDueTime
                            val newTime = if (dateTime > taskDueTime) dateTime
                            else taskDueTime.plus(hours = 1)
                            onUpdateTask(task.copy(reminderLocalDateTime = newTime.toString()))
                        }
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ReluctButton(
                    buttonText = discardButtonText,
                    icon = Icons.Rounded.DeleteSweep,
                    onButtonClicked = onDiscard,
                    shape = Shapes.large,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    buttonColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.width(Dimens.MediumPadding.size))
                ReluctButton(
                    buttonText = saveButtonText,
                    icon = Icons.Rounded.Save,
                    shape = Shapes.large,
                    buttonColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    onButtonClicked = {
                        val isTitleBlank = task.title.isBlank()
                        taskTitleError = isTitleBlank
                        if (!isTitleBlank) onSave(task)

                        // Trigger Setting Reminder - Not used for now as it's set in Use Cases
                        task.reminderLocalDateTime?.let { onReminderSet(it) }
                    }
                )
            }
        }
    }
}
