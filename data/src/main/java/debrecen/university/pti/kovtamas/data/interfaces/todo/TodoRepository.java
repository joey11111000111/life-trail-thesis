package debrecen.university.pti.kovtamas.data.interfaces.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.TodoEntity;
import java.util.Collection;

public interface TodoRepository {

    Collection<TodoEntity> findAll();

    Collection<TodoEntity> findByCategory(String category);

    TodoEntity findById(int id);

}
