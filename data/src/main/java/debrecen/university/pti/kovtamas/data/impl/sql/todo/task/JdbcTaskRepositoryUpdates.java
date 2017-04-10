package debrecen.university.pti.kovtamas.data.impl.sql.todo.task;

import debrecen.university.pti.kovtamas.data.entity.todo.RefactoredTaskEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.datasource.DataSourceManager;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRemovalException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepositoryUpdates;
import java.sql.Connection;
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

    static {
        INSTANCE = new JdbcTaskRepositoryUpdates();
    }

    static public JdbcTaskRepositoryUpdates getInstance() {
        return INSTANCE;
    }

    @Override
    public RefactoredTaskEntity saveOrUpdate(@NonNull final RefactoredTaskEntity entity) throws TaskPersistenceException {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            return saveOrUpdate(conn, entity);
        } catch (SQLException sqle) {
            throw new TaskPersistenceException("Exception while trying to save or update task entity", sqle);
        }
    }

    private RefactoredTaskEntity saveOrUpdate(Connection conn, RefactoredTaskEntity entity) throws SQLException {
        if (entity.hasId()) {
            return update(conn, entity);
        } else {
            return save(conn, entity);
        }
    }

    private RefactoredTaskEntity update(Connection conn, RefactoredTaskEntity entity) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(TaskUpdateStatements.UPDATE);
        setColumnVariablesFrom(statement, entity);
        statement.setInt(6, entity.getId());
        statement.executeUpdate();

        return RefactoredTaskEntity.copy(entity);
    }

    private void setColumnVariablesFrom(PreparedStatement statement, RefactoredTaskEntity entity) throws SQLException {
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

    private RefactoredTaskEntity save(Connection conn, RefactoredTaskEntity entity) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(TaskUpdateStatements.INSERT, Statement.RETURN_GENERATED_KEYS);
        setColumnVariablesFrom(statement, entity);
        statement.executeUpdate();

        return getSavedEntity(statement, entity);
    }

    private RefactoredTaskEntity getSavedEntity(PreparedStatement statement, RefactoredTaskEntity entity) throws SQLException {
        Integer generatedId = extractGeneratedKey(statement);
        RefactoredTaskEntity savedEntity = RefactoredTaskEntity.copy(entity);
        savedEntity.setId(generatedId);
        return savedEntity;
    }

    private Integer extractGeneratedKey(PreparedStatement statement) throws SQLException {
        ResultSet result = statement.getGeneratedKeys();
        if (result.next()) {
            return result.getInt(1);
        }

        throw new SQLException("Generated ID could not be retrived");
    }

    @Override
    public List<RefactoredTaskEntity> saveOrUpdateAll(@NonNull final Collection<RefactoredTaskEntity> entities) throws TaskPersistenceException {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            return saveOrUpdateAll(conn, entities);
        } catch (SQLException sqle) {
            throw new TaskPersistenceException("Exception while trying to save or update all entities", sqle);
        }
    }

    private List<RefactoredTaskEntity> saveOrUpdateAll(Connection conn, Collection<RefactoredTaskEntity> entities) throws SQLException {
        List<RefactoredTaskEntity> savedEntities = new ArrayList<>(entities.size());
        for (RefactoredTaskEntity entity : entities) {
            RefactoredTaskEntity savedEntity = saveOrUpdate(conn, entity);
            savedEntities.add(savedEntity);
        }

        return savedEntities;
    }

    @Override
    public void remove(final int id) throws TaskRemovalException {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            removeUsingGivenConnection(conn, id);
        } catch (SQLException sqle) {
            throw new TaskRemovalException("Failed to establish database connection", sqle);
        }
    }

    @Override
    public void removeAll(@NonNull final Collection<Integer> ids) throws TaskRemovalException {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            for (Integer id : ids) {
                removeUsingGivenConnection(conn, id);
            }
        } catch (SQLException sqle) {
            throw new TaskRemovalException("Failed to establish database connection", sqle);
        }
    }

    private void removeUsingGivenConnection(Connection conn, int removeId) throws TaskRemovalException {
        try {
            PreparedStatement statement = conn.prepareStatement(TaskUpdateStatements.REMOVE_BY_ID);
            statement.setInt(1, removeId);
            statement.executeUpdate();
        } catch (SQLException sqle) {
            throw new TaskRemovalException("Exception while trying to remove task with id: " + removeId, sqle);
        }
    }

    @Override
    public void clearTable() {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            Statement statement = conn.createStatement();
            statement.executeUpdate(TaskUpdateStatements.CLEAR);
        } catch (SQLException sqle) {
            log.warn("Exception while trying to clear task table", sqle);
        }
    }

}
