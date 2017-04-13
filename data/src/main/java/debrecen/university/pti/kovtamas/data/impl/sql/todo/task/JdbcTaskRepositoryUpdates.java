package debrecen.university.pti.kovtamas.data.impl.sql.todo.task;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.datasource.DatabaseConnector;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRemovalException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepositoryUpdates;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JdbcTaskRepositoryUpdates implements TaskRepositoryUpdates {

    static private final JdbcTaskRepositoryUpdates INSTANCE;

    private final DatabaseConnector connector;

    static {
        INSTANCE = new JdbcTaskRepositoryUpdates();
    }

    static public JdbcTaskRepositoryUpdates getInstance() {
        return INSTANCE;
    }

    private JdbcTaskRepositoryUpdates() {
        connector = DatabaseConnector.getInstance();
    }

    @Override
    public TaskEntity saveOrUpdate(@NonNull final TaskEntity entity) throws TaskPersistenceException {
        try {
            return saveOrUpdateBasedOnId(entity);
        } catch (SQLException sqle) {
            throw new TaskPersistenceException("Exception while trying to save or update task entity", sqle);
        } finally {
            connector.finishedOperations();
        }
    }

    private TaskEntity saveOrUpdateBasedOnId(TaskEntity entity) throws SQLException {
        if (entity.hasId()) {
            return update(entity);
        } else {
            return save(entity);
        }
    }

    private TaskEntity update(TaskEntity entity) throws SQLException {
        PreparedStatement statement = connector.prepareStatement(TaskUpdateStatements.UPDATE);
        setColumnVariablesFrom(statement, entity);
        statement.setInt(6, entity.getId());
        statement.executeUpdate();

        return TaskEntity.copy(entity);
    }

    private void setColumnVariablesFrom(PreparedStatement statement, TaskEntity entity) throws SQLException {
        int index = 1;
        if (entity.hasCategoryId()) {
            statement.setInt(index++, entity.getCategoryId());
        } else {
            statement.setNull(index++, Types.BIGINT);
        }
        statement.setString(index++, entity.getTaskDef());
        statement.setInt(index++, entity.getPriority());
        Date deadline = Date.valueOf(entity.getDeadline());
        statement.setDate(index++, deadline);
        String completed = Boolean.toString(entity.isCompleted()).toUpperCase();
        statement.setString(index++, completed);
    }

    private TaskEntity save(TaskEntity entity) throws SQLException {
        PreparedStatement statement = connector.prepareStatement(TaskUpdateStatements.INSERT, DatabaseConnector.RETURN_GENERATED_KEYS);
        setColumnVariablesFrom(statement, entity);
        statement.executeUpdate();

        return getSavedEntity(statement, entity);
    }

    private TaskEntity getSavedEntity(PreparedStatement statement, TaskEntity entity) throws SQLException {
        Integer generatedId = extractGeneratedKey(statement);
        TaskEntity savedEntity = TaskEntity.copy(entity);
        savedEntity.setId(generatedId);
        return savedEntity;
    }

    private Integer extractGeneratedKey(PreparedStatement statement) throws SQLException {
        ResultSet result = connector.getGeneratedKeys(statement);
        if (result.next()) {
            return result.getInt(1);
        }

        throw new SQLException("Generated ID could not be retrived");
    }

    @Override
    public List<TaskEntity> saveOrUpdateAll(@NonNull final Collection<TaskEntity> entities) throws TaskPersistenceException {
        try {
            return saveOrUpdateAllBasedOnId(entities);
        } catch (SQLException sqle) {
            throw new TaskPersistenceException("Exception while trying to save or update all entities", sqle);
        } finally {
            connector.finishedOperations();
        }
    }

    private List<TaskEntity> saveOrUpdateAllBasedOnId(Collection<TaskEntity> entities) throws SQLException {
        List<TaskEntity> savedEntities = new ArrayList<>(entities.size());
        for (TaskEntity entity : entities) {
            TaskEntity savedEntity = saveOrUpdateBasedOnId(entity);
            savedEntities.add(savedEntity);
        }

        return savedEntities;
    }

    @Override
    public void remove(final int id) throws TaskRemovalException {
        try {
            PreparedStatement statement = connector.prepareStatement(TaskUpdateStatements.REMOVE_BY_ID);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException sqle) {
            throw new TaskRemovalException("Exception while trying to remove task with id: " + id, sqle);
        } finally {
            connector.finishedOperations();
        }
    }

    @Override
    public void removeAll(@NonNull final Collection<Integer> ids) throws TaskRemovalException {
        for (Integer id : ids) {
            remove(id);
        }
    }

    @Override
    public void clearTable() {
        try {
            Statement statement = connector.createStatement();
            statement.executeUpdate(TaskUpdateStatements.CLEAR);
        } catch (SQLException sqle) {
            log.warn("Exception while trying to clear task table", sqle);
        } finally {
            connector.finishedOperations();
        }
    }

}
