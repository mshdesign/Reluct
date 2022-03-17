package work.racka.reluct.common.features.tasks.search.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import work.racka.reluct.common.database.dao.tasks.TasksDao
import work.racka.reluct.common.features.tasks.util.TasksHelper
import work.racka.reluct.common.model.domain.tasks.Task
import work.racka.reluct.common.model.util.time.TimeUtils

class SearchTasksRepositoryImpl(
    private val dao: TasksDao,
    private val backgroundDispatcher: CoroutineDispatcher
) : SearchTasksRepository {

    override suspend fun toggleDone(taskId: Long, isDone: Boolean) =
        withContext(backgroundDispatcher) {
            dao.toggleTaskDone(taskId, isDone)
        }

    override suspend fun search(query: String): Flow<List<Task>> =
        withContext(backgroundDispatcher) {
            dao.searchTasks(query)
                .map { list ->
                    val newList = mutableListOf<Task>()
                    list.forEach { taskDbObject ->
                        val task = Task(
                            id = taskDbObject.id,
                            title = taskDbObject.title,
                            description = taskDbObject.description ?: "No Description",
                            done = taskDbObject.done,
                            overdue = TimeUtils.isDateTimeOverdue(
                                taskDbObject.dueDateLocalDateTime,
                                taskDbObject.timeZoneId
                            ),
                            dueDate = TimeUtils.getFormattedDateString(
                                dateTime = taskDbObject.dueDateLocalDateTime,
                                originalTimeZoneId = taskDbObject.timeZoneId
                            ),
                            dueTime = TimeUtils.getTimeFromLocalDateTime(
                                dateTime = taskDbObject.dueDateLocalDateTime,
                                originalTimeZoneId = taskDbObject.timeZoneId
                            ),
                            timeLeftLabel = TimeUtils.getTimeLeftFromLocalDateTime(
                                dateTime = taskDbObject.dueDateLocalDateTime,
                                originalTimeZoneId = taskDbObject.timeZoneId
                            ),
                            reminder = TasksHelper.getReminderDateTime(
                                taskDbObject.reminderLocalDateTime,
                                taskDbObject.timeZoneId
                            )
                        )
                        newList.add(task)
                    }
                    newList.toList()
                }
        }
}