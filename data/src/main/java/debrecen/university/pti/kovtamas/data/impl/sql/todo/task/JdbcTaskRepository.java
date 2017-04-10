package debrecen.university.pti.kovtamas.data.impl.sql.todo.task;

import debrecen.university.pti.kovtamas.data.entity.todo.RefactoredTaskEntity;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRemovalException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepository;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepositoryQueries;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepositoryUpdates;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public class JdbcTaskRepository implements TaskRepository {

    static private final JdbcTaskRepository INSTANCE;

    private final TaskRepositoryQueries taskQueries;
    private final TaskRepositoryUpdates taskUpdates;

    static {
        INSTANCE = new JdbcTaskRepository();
    }

    static public JdbcTaskRepository getInstance() {
        return INSTANCE;
    }

    public JdbcTaskRepository() {
        taskQueries = JdbcTaskRepositoryQueries.getInstance();
        taskUpdates = JdbcTaskRepositoryUpdates.getInstance();
    }

    @Override
    public List<RefactoredTaskEntity> findAll() {
        return taskQueries.findAll();
    }

    @Override
    public RefactoredTaskEntity findById(int id) throws TaskNotFoundException {
        return taskQueries.findById(id);
    }

    @Override
    public List<RefactoredTaskEntity> findByIds(Collection<Integer> ids) {
        return taskQueries.findByIds(ids);
    }

    @Override
    public List<RefactoredTaskEntity> findTodayAndUnfinishedPastTasks() {
        return taskQueries.findTodayAndUnfinishedPastTasks();
    }

    @Override
    public List<RefactoredTaskEntity> findActiveByCategoryId(int categoryId) {
        return taskQueries.findActiveByCategoryId(categoryId);
    }

    @Override
    public List<RefactoredTaskEntity> findCompletedTasks() {
        return taskQueries.findCompletedTasks();
    }

    @Override
    public List<RefactoredTaskEntity> findActiveTasksBetween(LocalDate since, LocalDate until) {
        return taskQueries.findActiveTasksBetween(since, until);
    }

    @Override
    public int getRowCount() {
        return taskQueries.getRowCount();
    }

    @Override
    public RefactoredTaskEntity saveOrUpdate(RefactoredTaskEntity entity) throws TaskPersistenceException {
        return taskUpdates.saveOrUpdate(entity);
    }

    @Override
    public List<RefactoredTaskEntity> saveOrUpdateAll(Collection<RefactoredTaskEntity> entities) throws TaskPersistenceException {
        return taskUpdates.saveOrUpdateAll(entities);
    }

    @Override
    public void remove(int id) throws TaskRemovalException {
        taskUpdates.remove(id);
    }

    @Override
    public void removeAll(Collection<Integer> ids) throws TaskRemovalException {
        taskUpdates.removeAll(ids);
    }

    @Override
    public void clearTable() {
        taskUpdates.clearTable();
    }

}
