package debrecen.university.pti.kovtamas.data.todo.interfaces;

import debrecen.university.pti.kovtamas.data.todo.entity.TodoEntity;
import java.util.Collection;

public interface TodoRepository {

    Collection<TodoEntity> findAll();

    Collection<TodoEntity> findByCategory(String category);

    TodoEntity findById(int id);

}
