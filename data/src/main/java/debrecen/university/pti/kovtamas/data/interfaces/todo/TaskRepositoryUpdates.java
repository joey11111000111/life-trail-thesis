package debrecen.university.pti.kovtamas.data.interfaces.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.RefactoredTaskEntity;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRemovalException;
import java.util.Collection;

public interface TaskRepositoryUpdates {

    void save(RefactoredTaskEntity entity) throws TaskPersistenceException;

    void saveAll(Collection<RefactoredTaskEntity> entities) throws TaskPersistenceException;

    void remove(int id) throws TaskNotFoundException, TaskRemovalException;

    void removeAll(Collection<Integer> ids) throws TaskNotFoundException, TaskRemovalException;

    void clearTable();

}
