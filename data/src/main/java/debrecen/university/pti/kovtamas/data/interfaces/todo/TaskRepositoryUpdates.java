package debrecen.university.pti.kovtamas.data.interfaces.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.RowModificationException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRemovalException;
import java.util.Collection;
import java.util.List;

public interface TaskRepositoryUpdates {

    TaskEntity saveOrUpdate(TaskEntity entity) throws TaskPersistenceException;

    List<TaskEntity> saveOrUpdateAll(Collection<TaskEntity> entities) throws TaskPersistenceException;

    void remove(int id) throws TaskRemovalException;

    void removeAll(Collection<Integer> ids) throws TaskRemovalException;

    void setCategoryIdToNullWhere(int categoryId) throws RowModificationException;

    void clearTable();

}
