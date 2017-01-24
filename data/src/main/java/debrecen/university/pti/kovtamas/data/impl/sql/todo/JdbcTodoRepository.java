package debrecen.university.pti.kovtamas.data.impl.sql.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.datasource.DataSourceManager;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRemovalException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.UnsuccessfulDatabaseOperation;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TodoRepository;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcTodoRepository implements TodoRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcTodoRepository.class);
    private final JdbcTaskUtils jdbcUtils;

    public JdbcTodoRepository(DateTimeFormatter requiredDateFormat) {
        jdbcUtils = new JdbcTaskUtils(requiredDateFormat);
    }

    @Override
    public Set<TaskEntity> findAll() {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(TodoQueries.FIND_ALL);

            return jdbcUtils.extractEntities(results);
        } catch (SQLException sqle) {
            LOG.warn("Exception while trying to read from database!", sqle);
            return new HashSet<>();
        }
    }

    @Override
    public Set<TaskEntity> findByCategory(String category) {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            PreparedStatement prStatement = conn.prepareStatement(TodoQueries.FIND_BY_CATEGORY);
            prStatement.setString(1, category);
            ResultSet results = prStatement.executeQuery();

            return jdbcUtils.extractEntities(results);
        } catch (SQLException sqle) {
            LOG.warn("Exception while trying to read from database!", sqle);
            return new HashSet<>();
        }
    }

    @Override
    public Set<TaskEntity> findByNotCategory(String categoryToSkip) {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            PreparedStatement prStatement = conn.prepareStatement(TodoQueries.FIND_BY_NOT_CATEGORY);
            prStatement.setString(1, categoryToSkip);
            ResultSet results = prStatement.executeQuery();

            return jdbcUtils.extractEntities(results);
        } catch (SQLException sqle) {
            LOG.warn("Exception while trying to read from database!", sqle);
            return new HashSet<>();
        }

    }

    @Override
    public TaskEntity findById(int id) throws TaskNotFoundException {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            PreparedStatement prStatement = conn.prepareStatement(TodoQueries.FIND_BY_ID);
            prStatement.setInt(1, id);
            ResultSet results = prStatement.executeQuery();

            if (results.next()) {
                return jdbcUtils.convertRecordToEntity(results);
            }

            throw new TaskNotFoundException("The id " + id + " doesn't belong to any object in the database!");
        } catch (SQLException sqle) {
            throw new TaskNotFoundException("Exception while trying to read from database!", sqle);
        }
    }

    @Override
    public Set<TaskEntity> findByIds(Collection<Integer> ids) throws TaskNotFoundException {
        // Search for duplication in ids
        Set<Integer> idSet = new HashSet<>(ids);
        if (idSet.size() < ids.size()) {
            throw new IllegalArgumentException("Duplicated id in collection!");
        }

        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            PreparedStatement prStatement = conn.prepareStatement(TodoQueries.buildIdCollectionQuery(ids.size()));

            Iterator<Integer> idIterator = ids.iterator();
            for (int i = 0; i < ids.size(); i++) {
                prStatement.setInt(i + 1, idIterator.next());       // PreparedStatement uses 1 - based indexing
            }

            ResultSet results = prStatement.executeQuery();
            Set<TaskEntity> entities = jdbcUtils.extractEntities(results);

            if (entities.size() < ids.size()) {
                throw new TaskNotFoundException("One or some of the given IDs don't belong to any objects in the database!");
            }

            return entities;
        } catch (SQLException sqle) {
            throw new TaskNotFoundException("Exception while trying to read from database!", sqle);
        }
    }

    @Override
    public Set<TaskEntity> findTodayTasks() {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            return findTodayTasks(conn);
        } catch (SQLException sqle) {
            LOG.warn("Failied attempt to find today tasks!", sqle);
            return new HashSet<>();
        }
    }

    @Override
    public Set<TaskEntity> findTasksUntil(String lastDate, DateTimeFormatter format) {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            Set<TaskEntity> tasksUntil = findTodayTasks(conn);
            Date now = Date.valueOf(LocalDate.now().plusDays(1));   // Today tasks are already in the set, so plusDays(1)
            Date until = Date.valueOf(LocalDate.parse(lastDate, format));
            PreparedStatement prStatement = conn.prepareStatement(TodoQueries.FIND_TASKS_BETWEEN_DATE);
            prStatement.setDate(1, now);
            prStatement.setDate(2, until);

            ResultSet results = prStatement.executeQuery();
            tasksUntil.addAll(jdbcUtils.extractEntities(results));
            return tasksUntil;
        } catch (SQLException sqle) {
            LOG.warn("Failed attempt to find tasks until date: " + lastDate, sqle);
            return new HashSet<>();
        }
    }

    @Override
    public void save(TaskEntity entity) throws TaskPersistenceException {
        saveAll(Arrays.asList(entity));
    }

    @Override
    public void saveAll(Collection<TaskEntity> entities) throws TaskPersistenceException {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            for (TaskEntity entity : entities) {
                saveOrUpdate(entity, conn);
            }
        } catch (SQLException sqle) {
            throw new TaskPersistenceException("Exception while trying to update the database!", sqle);
        }
    }

    @Override
    public void remove(int id) throws TaskNotFoundException, TaskRemovalException {
        removeAll(Arrays.asList(id));
    }

    @Override
    public void removeAll(Collection<Integer> ids) throws TaskNotFoundException, TaskRemovalException {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            for (int id : ids) {
                removeTask(id, conn);
            }
        } catch (SQLException sqle) {
            throw new TaskRemovalException("Exception while trying to remove from the database!", sqle);
        }
    }

    @Override
    public void clean() {
        String exceptionMessage = "Could not clean database table!";
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            Statement statement = conn.createStatement();
            statement.executeUpdate(TodoQueries.CLEAN_TABLE);
            ResultSet results = statement.executeQuery(TodoQueries.GET_ROW_COUNT);
            if (results.next()) {
                int rowCount = results.getInt("ROW_COUNT");
                if (rowCount != 0) {
                    throw new UnsuccessfulDatabaseOperation(exceptionMessage);
                }
            }
        } catch (SQLException sqle) {
            throw new UnsuccessfulDatabaseOperation(exceptionMessage);
        }
    }

    @Override
    public int getRowCount() {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(TodoQueries.GET_ROW_COUNT);
            if (results.next()) {
                return results.getInt("ROW_COUNT");
            }
            return -1;
        } catch (SQLException sqle) {
            LOG.warn("Could not get row count of database table!", sqle);
            return -1;
        }
    }

    private void saveOrUpdate(TaskEntity entity, Connection conn) throws SQLException, TaskPersistenceException {
        PreparedStatement prStatement = createSaveOrUpdateStatement(entity, conn);

        // Set statement variables
        prStatement.setString(1, entity.getTaskDef());
        prStatement.setInt(2, entity.getPriority());
        prStatement.setDate(3, jdbcUtils.parseToSqlDate(entity.getDeadline()));
        prStatement.setString(4, entity.getCategory());
        prStatement.setString(5, entity.getSubTaskIds());
        prStatement.setString(6, Boolean.toString(entity.isRepeating()));
        if (entity.hasId()) {
            prStatement.setInt(7, entity.getId());
        }

        int affectedRows = prStatement.executeUpdate();
        if (affectedRows != 1) {
            throw new TaskPersistenceException("Failed to update task with id: " + entity.getId());
        }

        if (entity.hasId()) {
            return;
        }

        entity.setId(jdbcUtils.extractGeneratedId(prStatement));
    }

    private void removeTask(int id, Connection conn) throws SQLException, TaskNotFoundException {
        PreparedStatement prStatement = conn.prepareStatement(TodoQueries.REMOVE_BY_ID);
        prStatement.setInt(1, id);
        int modifiedRowCount = prStatement.executeUpdate();
        if (modifiedRowCount == 0) {
            throw new TaskNotFoundException("Cannot delete a row that doesn't exist!"
                    + "The id " + id + " was not found in the database.");
        }
    }

    private PreparedStatement createSaveOrUpdateStatement(TaskEntity entity, Connection conn) throws SQLException {
        if (entity.hasId()) {
            return conn.prepareStatement(TodoQueries.UPDATE);
        }
        return conn.prepareStatement(TodoQueries.INSERT, Statement.RETURN_GENERATED_KEYS);
    }

    private Set<TaskEntity> findTodayTasks(Connection conn) throws SQLException {
        PreparedStatement prStatement = conn.prepareStatement(TodoQueries.FIND_TODAY_TASKS);
        LocalDate today = LocalDate.now();
        Date sqlToday = Date.valueOf(today);
        prStatement.setDate(1, sqlToday);

        ResultSet results = prStatement.executeQuery();
        return jdbcUtils.extractEntities(results);
    }

}
