package debrecen.university.pti.kovtamas.data.interfaces.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.RefactoredTaskEntity;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface TaskRepositoryQueries {

    List<RefactoredTaskEntity> findAll();

    RefactoredTaskEntity findById(int id) throws TaskNotFoundException;

    List<RefactoredTaskEntity> findByIds(Collection<Integer> ids);

    List<RefactoredTaskEntity> findTodayAndUnfinishedPastTasks();

    List<RefactoredTaskEntity> findActiveByCategoryId(int categoryId);

    List<RefactoredTaskEntity> findCompletedTasks();

    List<RefactoredTaskEntity> findActiveTasksBetween(LocalDate since, LocalDate until);

    int getRowCount();

}
