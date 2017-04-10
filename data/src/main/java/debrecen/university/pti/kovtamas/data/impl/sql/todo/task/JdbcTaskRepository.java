package debrecen.university.pti.kovtamas.data.impl.sql.todo.task;

import debrecen.university.pti.kovtamas.data.entity.todo.RefactoredTaskEntity;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRemovalException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepository;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public class JdbcTaskRepository implements TaskRepository {

    static private final JdbcTaskRepository INSTANCE;

    static {
        INSTANCE = new JdbcTaskRepository();
    }

    static public JdbcTaskRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public RefactoredTaskEntity findById(int id) throws TaskNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<RefactoredTaskEntity> findByIds(Collection<Integer> ids) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<RefactoredTaskEntity> findTodayAndUnfinishedPastTasks() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<RefactoredTaskEntity> findActiveByCategoryId(int categoryId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<RefactoredTaskEntity> findCompletedTasks() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<RefactoredTaskEntity> findActiveTasksBetween(LocalDate since, LocalDate until) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getRowCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RefactoredTaskEntity saveOrUpdate(RefactoredTaskEntity entity) throws TaskPersistenceException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<RefactoredTaskEntity> saveOrUpdateAll(Collection<RefactoredTaskEntity> entities) throws TaskPersistenceException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void remove(int id) throws TaskNotFoundException, TaskRemovalException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeAll(Collection<Integer> ids) throws TaskNotFoundException, TaskRemovalException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearTable() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
