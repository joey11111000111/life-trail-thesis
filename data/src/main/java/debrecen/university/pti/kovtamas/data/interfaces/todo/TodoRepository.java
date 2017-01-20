package debrecen.university.pti.kovtamas.data.interfaces.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRemovalException;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Set;

public interface TodoRepository {

    Set<TaskEntity> findAll();

    Set<TaskEntity> findByCategory(String category);

    Set<TaskEntity> findByNotCategory(String categoryToSkip);

    TaskEntity findById(int id) throws TaskNotFoundException;

    Set<TaskEntity> findByIds(Collection<Integer> ids) throws TaskNotFoundException;

    Set<TaskEntity> findTodayTasks();

    Set<TaskEntity> findTasksUntil(String lastDate, DateTimeFormatter format);

    void save(TaskEntity entity) throws TaskPersistenceException;

    void saveAll(Collection<TaskEntity> entities) throws TaskPersistenceException;

    void remove(int id) throws TaskNotFoundException, TaskRemovalException;

    void removeAll(Collection<Integer> ids) throws TaskNotFoundException, TaskRemovalException;

    void clean();

    int getRowCount();

}
