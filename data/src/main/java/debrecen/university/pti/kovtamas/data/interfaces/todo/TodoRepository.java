package debrecen.university.pti.kovtamas.data.interfaces.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskSaveFailureException;
import java.util.Collection;
import java.util.Set;

public interface TodoRepository {

    Set<TaskEntity> findAll();

    Set<TaskEntity> findByCategory(String category);

    Set<TaskEntity> findByNotCategory(String categoryToSkip);

    TaskEntity findById(int id) throws TaskNotFoundException;

    Set<TaskEntity> findByIds(Collection<Integer> ids) throws TaskNotFoundException;

    void save(TaskEntity entity) throws TaskSaveFailureException;

    void saveAll(Collection<TaskEntity> entities) throws TaskSaveFailureException;

    void remove(int id) throws TaskNotFoundException;

    void clean();

    int getRowCount();

}
