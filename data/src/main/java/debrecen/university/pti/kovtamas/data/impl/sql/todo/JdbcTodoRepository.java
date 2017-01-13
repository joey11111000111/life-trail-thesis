package debrecen.university.pti.kovtamas.data.impl.sql.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.datasource.DataSourceManager;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskSaveFailureException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.UnsuccessfulDatabaseOperation;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TodoRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcTodoRepository implements TodoRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcTodoRepository.class);

    @Override
    public Set<TaskEntity> findAll() {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(TodoQueries.FIND_ALL);

            Set<TaskEntity> entities = new HashSet<>();
            while (results.next()) {
                entities.add(convertRecordToEntity(results));
            }

            return entities;
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

            Set<TaskEntity> entities = new HashSet<>();
            while (results.next()) {
                entities.add(convertRecordToEntity(results));
            }

            return entities;
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

            Set<TaskEntity> entities = new HashSet<>();
            while (results.next()) {
                entities.add(convertRecordToEntity(results));
            }

            return entities;
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
                return convertRecordToEntity(results);
            }

            String exceptionMessage = "The id " + id + " doesn't belong to any object in the database!";
            LOG.warn(exceptionMessage);
            throw new TaskNotFoundException(exceptionMessage);
        } catch (SQLException sqle) {
            String message = "Exception while trying to read from database!";
            LOG.warn(message, sqle);
            throw new TaskNotFoundException(message, sqle);
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
            Set<TaskEntity> entities = new HashSet<>();
            while (results.next()) {
                entities.add(convertRecordToEntity(results));
            }

            if (entities.size() < ids.size()) {
                String exceptionMessage = "One or some of the given IDs don't belong to any objects in the database!";
                LOG.warn(exceptionMessage);
                throw new TaskNotFoundException(exceptionMessage);
            }

            return entities;
        } catch (SQLException sqle) {
            String message = "Exception while trying to read from database!";
            LOG.warn(message, sqle);
            throw new TaskNotFoundException(message, sqle);
        }
    }

    @Override
    public void save(TaskEntity entity) throws TaskSaveFailureException {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            saveOrUpdate(entity, conn);
        } catch (SQLException sqle) {
            String message = "Exception while trying to update the database!";
            LOG.warn(message, sqle);
            throw new TaskSaveFailureException(message, sqle);
        }
    }

    @Override
    public void saveAll(Collection<TaskEntity> entities) throws TaskSaveFailureException {
        for (TaskEntity entity : entities) {
            save(entity);
        }
    }

    private void saveOrUpdate(TaskEntity entity, Connection conn) throws SQLException, TaskSaveFailureException {
        PreparedStatement prStatement = createSaveOrUpdateStatement(entity, conn);

        // Set statement variables
        prStatement.setString(1, entity.getTaskDef());
        prStatement.setInt(2, entity.getPriority());
        prStatement.setString(3, entity.getDeadline());
        prStatement.setString(4, entity.getCategory());
        prStatement.setString(5, entity.getSubTaskIds());
        prStatement.setString(6, Boolean.toString(entity.isRepeating()));
        if (entity.hasId()) {
            prStatement.setInt(7, entity.getId());
        }

        int affectedRows = prStatement.executeUpdate();
        if (affectedRows != 1) {
            throw new TaskSaveFailureException("Failed to update task with id: " + entity.getId());
        }

        if (entity.hasId()) {
            return;
        }

        entity.setId(extractGeneratedId(prStatement));
    }

    @Override
    public void remove(int id) throws TaskNotFoundException {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            PreparedStatement prStatement = conn.prepareStatement(TodoQueries.REMOVE_BY_ID);
            prStatement.setInt(1, id);
            int modifiedRowCount = prStatement.executeUpdate();
            if (modifiedRowCount == 0) {
                throw new TaskNotFoundException("Cannot delete a row that doesn't exist!"
                        + "The id " + id + " was not found in the database.");
            }
        } catch (SQLException sqle) {
            LOG.warn("Exception while trying to remove from the database!", sqle);
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
            LOG.warn("Exception while trying to clean the database table!", sqle);
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

    private PreparedStatement createSaveOrUpdateStatement(TaskEntity entity, Connection conn) throws SQLException {
        if (entity.hasId()) {
            return conn.prepareStatement(TodoQueries.UPDATE);
        }
        return conn.prepareStatement(TodoQueries.INSERT, Statement.RETURN_GENERATED_KEYS);
    }

    private Integer extractGeneratedId(PreparedStatement prStatement) throws SQLException, TaskSaveFailureException {
        ResultSet result = prStatement.getGeneratedKeys();
        if (result.next()) {
            return result.getInt(1);
        }
        String message = "Possible save error, generated ID could not be retrived!";
        LOG.warn(message);
        throw new TaskSaveFailureException(message);
    }

    private TaskEntity convertRecordToEntity(ResultSet record) throws SQLException {
        return TaskEntity.builder()
                .id(record.getInt("ID"))
                .taskDef(record.getString("TASK_DEF"))
                .priority(record.getInt("PRIORITY"))
                .deadline(record.getString("DEADLINE"))
                .category(record.getString("CATEGORY"))
                .subTaskIds(record.getString("SUB_TASK_IDS"))
                .repeating(Boolean.parseBoolean(record.getString("REPEATING")))
                .build();
    }

}
