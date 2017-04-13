package debrecen.university.pti.kovtamas.data.interfaces.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface TaskRepositoryQueries {

    List<TaskEntity> findAll();

    TaskEntity findById(int id) throws TaskNotFoundException;

    List<TaskEntity> findByIds(Collection<Integer> ids);

    List<TaskEntity> findTodayAndUnfinishedPastTasks();

    List<TaskEntity> findActiveByCategoryId(int categoryId);

    List<TaskEntity> findUncategorizedTasks();

    List<TaskEntity> findCompletedTasks();

    List<TaskEntity> findActiveTasksBetween(LocalDate since, LocalDate until);

    int getRowCount();

}
