package debrecen.university.pti.kovtamas.data.interfaces.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.RefactoredTaskEntity;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRemovalException;
import java.util.Collection;
import java.util.List;

public interface TaskRepositoryUpdates {

    RefactoredTaskEntity saveOrUpdate(RefactoredTaskEntity entity) throws TaskPersistenceException;

    List<RefactoredTaskEntity> saveOrUpdateAll(Collection<RefactoredTaskEntity> entities) throws TaskPersistenceException;

    void remove(int id) throws TaskRemovalException;

    void removeAll(Collection<Integer> ids) throws TaskRemovalException;

    void clearTable();

}
