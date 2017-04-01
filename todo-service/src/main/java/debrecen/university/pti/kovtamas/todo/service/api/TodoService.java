package debrecen.university.pti.kovtamas.todo.service.api;

import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface TodoService {

    List<TaskVo> getAll();

    Set<String> getCustomCategories();

    List<TaskVo> getActiveByCategory(String category);

    // Methods for fixed categories ---------------------------------------
    List<TaskVo> getTodayTasks();

    List<TaskVo> getTomorrowTasks();

    List<TaskVo> getOneWeekTasks();

    List<TaskVo> getTasksOfFollowingDays(int days);

    List<TaskVo> getCompletedTasks();

    List<TaskVo> getUncategorizedTasks();
    // /Methods for fixed categories --------------------------------------

    void save(TaskVo task) throws TaskSaveFailureException;

    void saveAll(Collection<TaskVo> tasks) throws TaskSaveFailureException;

    void delete(TaskVo task) throws TaskDeletionException;

    void deleteAll(Collection<TaskVo> tasks) throws TaskDeletionException;

    void deleteTaskFromTaskTree(TaskVo taskToDelete, TaskVo taskTree) throws TaskDeletionException;

    void addSubTask(TaskVo mainTask, TaskVo subTask);

    TaskVo newMinimalTaskVo();

    void addNewMinimalSubTaskTo(TaskVo parent);
}
