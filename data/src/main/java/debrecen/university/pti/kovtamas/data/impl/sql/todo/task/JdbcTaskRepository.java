package debrecen.university.pti.kovtamas.data.impl.sql.todo.task;
//

public class JdbcTaskRepository {

}
//import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
//import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
//import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
//import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRemovalException;
//import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepository;
//import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepositoryQueries;
//import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepositoryUpdates;
//import java.time.LocalDate;
//import java.util.Collection;
//import java.util.List;
//
//public class JdbcTaskRepository implements TaskRepository {
//
//    static private final JdbcTaskRepository INSTANCE;
//
//    private final TaskRepositoryQueries taskQueries;
//    private final TaskRepositoryUpdates taskUpdates;
//
//    static {
//        INSTANCE = new JdbcTaskRepository();
//    }
//
//    static public JdbcTaskRepository getInstance() {
//        return INSTANCE;
//    }
//
//    public JdbcTaskRepository() {
//        taskQueries = JdbcTaskRepositoryQueries.getInstance();
//        taskUpdates = JdbcTaskRepositoryUpdates.getInstance();
//    }
//
//    @Override
//    public List<TaskEntity> findAll() {
//        return taskQueries.findAll();
//    }
//
//    @Override
//    public TaskEntity findById(int id) throws TaskNotFoundException {
//        return taskQueries.findById(id);
//    }
//
//    @Override
//    public List<TaskEntity> findByIds(Collection<Integer> ids) {
//        return taskQueries.findByIds(ids);
//    }
//
//    @Override
//    public List<TaskEntity> findTodayAndUnfinishedPastTasks() {
//        return taskQueries.findTodayAndUnfinishedPastTasks();
//    }
//
//    @Override
//    public List<TaskEntity> findActiveByCategoryId(int categoryId) {
//        return taskQueries.findActiveByCategoryId(categoryId);
//    }
//
//    @Override
//    public List<TaskEntity> findCompletedTasks() {
//        return taskQueries.findCompletedTasks();
//    }
//
//    @Override
//    public List<TaskEntity> findActiveTasksBetween(LocalDate since, LocalDate until) {
//        return taskQueries.findActiveTasksBetween(since, until);
//    }
//
//    @Override
//    public int getRowCount() {
//        return taskQueries.getRowCount();
//    }
//
//    @Override
//    public TaskEntity saveOrUpdate(TaskEntity entity) throws TaskPersistenceException {
//        return taskUpdates.saveOrUpdate(entity);
//    }
//
//    @Override
//    public List<TaskEntity> saveOrUpdateAll(Collection<TaskEntity> entities) throws TaskPersistenceException {
//        return taskUpdates.saveOrUpdateAll(entities);
//    }
//
//    @Override
//    public void remove(int id) throws TaskRemovalException {
//        taskUpdates.remove(id);
//    }
//
//    @Override
//    public void removeAll(Collection<Integer> ids) throws TaskRemovalException {
//        taskUpdates.removeAll(ids);
//    }
//
//    @Override
//    public void clearTable() {
//        taskUpdates.clearTable();
//    }
//
//}
