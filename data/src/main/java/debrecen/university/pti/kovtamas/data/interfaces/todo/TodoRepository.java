package debrecen.university.pti.kovtamas.data.interfaces.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.TodoEntity;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskSaveFailureException;
import java.util.Collection;
import java.util.Set;

public interface TodoRepository {

    Set<TodoEntity> findAll();

    Set<TodoEntity> findByCategory(String category);

    Set<TodoEntity> findByNotCategory(String categoryToSkip);

    TodoEntity findById(int id) throws TaskNotFoundException;

    Set<TodoEntity> findByIds(Collection<Integer> ids) throws TaskNotFoundException;

    void save(TodoEntity entity) throws TaskSaveFailureException;

    void saveAll(Collection<TodoEntity> entities) throws TaskSaveFailureException;

    void remove(int id) throws TaskNotFoundException;

    void removeAll(Collection<Integer> ids) throws TaskNotFoundException;

    void clean();

    int getRowCount();

}
