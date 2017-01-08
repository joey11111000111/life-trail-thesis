package debrecen.university.pti.kovtamas.data.interfaces.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.TodoEntity;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import java.util.Set;

public interface TodoRepository {

    Set<TodoEntity> findAll();

    Set<TodoEntity> findByCategory(String category);

    TodoEntity findById(int id) throws TaskNotFoundException;

    Set<TodoEntity> findByIds(Set<Integer> ids) throws TaskNotFoundException;

}
