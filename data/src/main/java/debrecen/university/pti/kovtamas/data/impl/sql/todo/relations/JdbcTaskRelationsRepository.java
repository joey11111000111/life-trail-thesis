package debrecen.university.pti.kovtamas.data.impl.sql.todo.relations;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskRelationEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.datasource.DataSourceManager;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRelationPersistenceException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRelationsRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JdbcTaskRelationsRepository implements TaskRelationsRepository {

    static private final JdbcTaskRelationsRepository INSTANCE;

    static {
        INSTANCE = new JdbcTaskRelationsRepository();
    }

    static public JdbcTaskRelationsRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public List<TaskRelationEntity> findAll() {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(TaskRelationStatements.FIND_ALL);
            return extractAllResults(results);
        } catch (SQLException sqle) {
            log.warn("Exception while trying to find all task relations", sqle);
            return Collections.EMPTY_LIST;
        }
    }

    private List<TaskRelationEntity> extractAllResults(ResultSet results) throws SQLException {
        List<TaskRelationEntity> allResults = new ArrayList<>();
        while (results.next()) {
            TaskRelationEntity loadedEntity = extractResultRow(results);
            allResults.add(loadedEntity);
        }

        return allResults;
    }

    private TaskRelationEntity extractResultRow(ResultSet resultRow) throws SQLException {
        return TaskRelationEntity.builder()
                .id(resultRow.getInt("ID"))
                .parentId(resultRow.getInt("PARENT_ID"))
                .childId(resultRow.getInt("CHILD_ID"))
                .build();
    }

    @Override
    public List<TaskRelationEntity> findAllWhereParentOrChildId(int id) {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            PreparedStatement statement = conn.prepareStatement(TaskRelationStatements.FIND_WHERE_PARENT_OR_CHILD);
            statement.setInt(1, id);
            statement.setInt(2, id);
            ResultSet results = statement.executeQuery();
            return extractAllResults(results);
        } catch (SQLException sqle) {
            log.warn("Exception while trying to find rows where parent or child id: " + id, sqle);
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public TaskRelationEntity save(TaskRelationEntity newRelation) throws TaskRelationPersistenceException {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            return saveNewRelation(conn, newRelation);
        } catch (SQLException sqle) {
            throw new TaskRelationPersistenceException("Exception while trying to save new task relation", sqle);
        }
    }

    private TaskRelationEntity saveNewRelation(Connection conn, TaskRelationEntity newRelation)
            throws SQLException, TaskRelationPersistenceException {
        abortIfEntityWouldMakeCircularDependency(conn, newRelation);

        PreparedStatement statement = conn.prepareStatement(TaskRelationStatements.INSERT, Statement.RETURN_GENERATED_KEYS);
        configureAndExecuteSaveStatement(statement, newRelation);

        Integer generatedId = extractGeneratedKey(statement);
        return TaskRelationEntity.builder()
                .id(generatedId)
                .parentId(newRelation.getParentId())
                .childId(newRelation.getChildId())
                .build();
    }

    private void abortIfEntityWouldMakeCircularDependency(Connection conn, TaskRelationEntity entity)
            throws SQLException, TaskRelationPersistenceException {
        PreparedStatement statement = conn.prepareStatement(TaskRelationStatements.FIND_BY_PARENT_AND_CHILD);
        statement.setInt(1, entity.getChildId());
        statement.setInt(2, entity.getParentId());
        ResultSet results = statement.executeQuery();
        if (results.next()) {
            throw new TaskRelationPersistenceException("Saving the following entity would result in "
                    + "circular task dependency: " + entity);
        }
    }

    private void configureAndExecuteSaveStatement(PreparedStatement statement, TaskRelationEntity entity) throws SQLException {
        statement.setInt(1, entity.getParentId());
        statement.setInt(2, entity.getChildId());
        statement.execute();
    }

    private Integer extractGeneratedKey(PreparedStatement statement) throws SQLException {
        ResultSet result = statement.getGeneratedKeys();
        if (result.next()) {
            return result.getInt(1);
        }

        throw new SQLException("Generated ID could not be retrived!");
    }

    @Override
    public List<TaskRelationEntity> saveAll(List<TaskRelationEntity> newRelations) throws TaskRelationPersistenceException {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            List<TaskRelationEntity> savedEntities = new ArrayList<>(newRelations.size());
            for (TaskRelationEntity entity : newRelations) {
                TaskRelationEntity savedEntity = saveNewRelation(conn, entity);
                savedEntities.add(savedEntity);
            }

            return savedEntities;
        } catch (SQLException sqle) {
            log.warn("Exception while trying to save all entities", sqle);
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public void removeRelation(int relationId) {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            PreparedStatement statement = conn.prepareStatement(TaskRelationStatements.REMOVE_BY_ID);
            statement.setInt(1, relationId);
            statement.executeUpdate();
        } catch (SQLException sqle) {
            log.warn("Exception while trying to delete task relation with id: " + relationId, sqle);
        }
    }

    @Override
    public void removeAllWhereParentOrChildIdIs(int id) {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            PreparedStatement statement = conn.prepareStatement(TaskRelationStatements.REMOVE_WHERE_PARENT_OR_CHILD);
            statement.setInt(1, id);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException sqle) {
            log.warn("Exception while trying to delete rows with parent or child id: " + id, sqle);
        }
    }

    @Override
    public void clearTable() {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            Statement statement = conn.createStatement();
            statement.executeUpdate(TaskRelationStatements.CLEAR);
        } catch (SQLException sqle) {
            log.warn("Failed to clear task relations table", sqle);
        }
    }

}
