package debrecen.university.pti.kovtamas.todo.service.api;

import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.util.Collection;
import java.util.List;

public interface TodoService {

    List<TaskVo> getByCategory(String category);

    List<TaskVo> getTodayTasks();

    List<TaskVo> getTomorrowTasks();

    List<TaskVo> getTasksOfFollowingDays(int days);

    void save(TaskVo task) throws TaskSaveFailureException;

    void saveAll(Collection<TaskVo> tasks) throws TaskSaveFailureException;

    void delete(TaskVo task) throws TaskDeletionException;

    void deleteAll(Collection<TaskVo> tasks) throws TaskDeletionException;

}
