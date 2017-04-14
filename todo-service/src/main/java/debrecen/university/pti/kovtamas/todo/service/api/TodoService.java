package debrecen.university.pti.kovtamas.todo.service.api;

import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.CategorySaveFailureException;
import debrecen.university.pti.kovtamas.todo.service.vo.CategoryVo;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface TodoService {

    // Category and logical category related methods
    List<CategoryVo> getAllCategoriesInDisplayOrder();

    List<TaskVo> getActiveByCategory(CategoryVo category);

    List<TaskVo> getTodayTasks();

    List<TaskVo> getTomorrowTasks();

    List<TaskVo> getOneWeekTasks();

    List<TaskVo> getCompletedTasks();

    List<TaskVo> getUncategorizedTasks();

    List<TaskVo> getTasksOfFollowingDays(LocalDate since, LocalDate until);

    CategoryVo saveOrUpdateCategory(CategoryVo categoryVo) throws CategorySaveFailureException;

    void deleteCategory(CategoryVo category);
    // /Category and logical category related methods

    TaskVo saveTask(TaskVo task) throws TaskSaveFailureException;

    List<TaskVo> saveAllTasks(Collection<TaskVo> tasks) throws TaskSaveFailureException;

    void deleteTask(TaskVo task) throws TaskDeletionException;

    void deleteAllTasks(Collection<TaskVo> tasks) throws TaskDeletionException;

    void deleteTaskFromTaskTree(TaskVo taskToDelete, TaskVo taskTree) throws TaskDeletionException;

    void addSubTaskToMainTask(TaskVo subTask, TaskVo mainTask);

    TaskVo newMinimalTaskVo();

    void addNewMinimalSubTaskTo(TaskVo parent);
}
