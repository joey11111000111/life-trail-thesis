package debrecen.university.pti.kovtamas.data.impl.sql.todo.relations;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskRelationEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.datasource.DatabaseConnector;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRelationPersistenceException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRelationsRepository;
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

    private final DatabaseConnector connector;

    static {
        INSTANCE = new JdbcTaskRelationsRepository();
    }

    static public JdbcTaskRelationsRepository getInstance() {
        return INSTANCE;
    }

    private JdbcTaskRelationsRepository() {
        connector = DatabaseConnector.getInstance();
    }

    @Override
    public List<TaskRelationEntity> findAll() {
        try {
            Statement statement = connector.createStatement();
            ResultSet results = connector.executeQuery(statement, TaskRelationStatements.FIND_ALL);
            return extractAllResults(results);
        } catch (SQLException sqle) {
            log.warn("Exception while trying to find all task relations", sqle);
            return Collections.EMPTY_LIST;
        } finally {
            connector.finishedOperations();
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
        try {
            PreparedStatement statement = connector.prepareStatement(TaskRelationStatements.FIND_WHERE_PARENT_OR_CHILD);
            statement.setInt(1, id);
            statement.setInt(2, id);
            ResultSet results = connector.executeQuery(statement);
            return extractAllResults(results);
        } catch (SQLException sqle) {
            log.warn("Exception while trying to find rows where parent or child id: " + id, sqle);
            return Collections.EMPTY_LIST;
        } finally {
            connector.finishedOperations();
        }
    }

    @Override
    public TaskRelationEntity save(TaskRelationEntity newRelation) throws TaskRelationPersistenceException {
        try {
            return saveNewRelation(newRelation);
        } catch (SQLException sqle) {
            throw new TaskRelationPersistenceException("Exception while trying to save new task relation", sqle);
        } finally {
            connector.finishedOperations();
        }
    }

    private TaskRelationEntity saveNewRelation(TaskRelationEntity newRelation)
            throws SQLException, TaskRelationPersistenceException {
        abortIfEntityWouldMakeCircularDependency(newRelation);

        PreparedStatement statement = connector.prepareStatement(TaskRelationStatements.INSERT, DatabaseConnector.RETURN_GENERATED_KEYS);
        configureAndExecuteSaveStatement(statement, newRelation);

        Integer generatedId = extractGeneratedKey(statement);
        return TaskRelationEntity.builder()
                .id(generatedId)
                .parentId(newRelation.getParentId())
                .childId(newRelation.getChildId())
                .build();
    }

    private void abortIfEntityWouldMakeCircularDependency(TaskRelationEntity entity)
            throws SQLException, TaskRelationPersistenceException {
        PreparedStatement statement = connector.prepareStatement(TaskRelationStatements.FIND_BY_PARENT_AND_CHILD);
        statement.setInt(1, entity.getChildId());
        statement.setInt(2, entity.getParentId());
        ResultSet results = connector.executeQuery(statement);
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
        try {
            List<TaskRelationEntity> savedEntities = new ArrayList<>(newRelations.size());
            for (TaskRelationEntity entity : newRelations) {
                TaskRelationEntity savedEntity = saveNewRelation(entity);
                savedEntities.add(savedEntity);
            }

            return savedEntities;
        } catch (SQLException sqle) {
            log.warn("Exception while trying to save all entities", sqle);
            return Collections.EMPTY_LIST;
        } finally {
            connector.finishedOperations();
        }
    }

    @Override
    public void removeRelation(int relationId) {
        try {
            PreparedStatement statement = connector.prepareStatement(TaskRelationStatements.REMOVE_BY_ID);
            statement.setInt(1, relationId);
            statement.executeUpdate();
        } catch (SQLException sqle) {
            log.warn("Exception while trying to delete task relation with id: " + relationId, sqle);
        } finally {
            connector.finishedOperations();
        }
    }

    @Override
    public void removeAllWhereParentOrChildIdIs(int id) {
        try {
            PreparedStatement statement = connector.prepareStatement(TaskRelationStatements.REMOVE_WHERE_PARENT_OR_CHILD);
            statement.setInt(1, id);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException sqle) {
            log.warn("Exception while trying to delete rows with parent or child id: " + id, sqle);
        } finally {
            connector.finishedOperations();
        }
    }

    @Override
    public void clearTable() {
        try {
            Statement statement = connector.createStatement();
            statement.executeUpdate(TaskRelationStatements.CLEAR);
        } catch (SQLException sqle) {
            log.warn("Failed to clear task relations table", sqle);
        } finally {
            connector.finishedOperations();
        }
    }

}
