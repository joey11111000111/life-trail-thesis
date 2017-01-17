package debrecen.university.pti.kovtamas.todo.service.api;

import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.util.List;

public interface TodoService {

    List<TaskVo> getByCategory(String category);

    List<TaskVo> getTodayTasks();

    List<TaskVo> getTomorrowTasks();

    List<TaskVo> getTasksOfFollowingDays(int days);

    void save(TaskVo task) throws TaskSaveFailureException;

    void saveAll(List<TaskVo> tasks) throws TaskSaveFailureException;

    void delete(TaskVo task);

    void deleteAll(List<TaskVo> tasks);

}
