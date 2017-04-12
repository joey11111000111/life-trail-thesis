package debrecen.university.pti.kovtamas.data.impl.sql.todo.task;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.datasource.DatabaseConnector;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepositoryQueries;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JdbcTaskRepositoryQueries implements TaskRepositoryQueries {

    static private final JdbcTaskRepositoryQueries INSTANCE;

    private final DatabaseConnector connector;

    static {
        INSTANCE = new JdbcTaskRepositoryQueries();
    }

    static public JdbcTaskRepositoryQueries getInstance() {
        return INSTANCE;
    }

    public JdbcTaskRepositoryQueries() {
        connector = DatabaseConnector.getInstance();
    }

    @Override
    public List<TaskEntity> findAll() {
        try {
            Statement statement = connector.createStatement();
            ResultSet results = connector.executeQuery(statement, TaskQueryStatements.FIND_ALL);
            return extractAllResults(results);
        } catch (SQLException sqle) {
            log.warn("Exception while trying to find all tasks", sqle);
            return Collections.EMPTY_LIST;
        } finally {
            connector.finishedOperations();
        }
    }

    @Override
    public TaskEntity findById(int id) throws TaskNotFoundException {
        try {
            return findByIdIfPresent(id);
        } catch (SQLException sqle) {
            throw new TaskNotFoundException("Exception while trying to find task with id: " + id, sqle);
        } finally {
            connector.finishedOperations();
        }
    }

    private TaskEntity findByIdIfPresent(int id) throws SQLException, TaskNotFoundException {
        PreparedStatement statement = connector.prepareStatement(TaskQueryStatements.FIND_BY_ID);
        statement.setInt(1, id);

        ResultSet results = connector.executeQuery(statement);
        if (results.next()) {
            return extractResult(results);
        }

        throw new TaskNotFoundException("Could not find task with id: " + id);
    }

    @Override
    public List<TaskEntity> findByIds(@NonNull final Collection<Integer> ids) {
        try {
            return findAllExistingTasksByIds(ids);
        } catch (SQLException exception) {
            log.warn("Exception while trying to find tasks by ids", exception);
            return Collections.EMPTY_LIST;
        } finally {
            connector.finishedOperations();
        }
    }

    private List<TaskEntity> findAllExistingTasksByIds(Collection<Integer> ids) throws SQLException {
        List<TaskEntity> loadedEntities = new ArrayList<>(ids.size());
        for (Integer id : ids) {
            try {
                TaskEntity entity = findByIdIfPresent(id);
                loadedEntities.add(entity);
            } catch (TaskNotFoundException tnfe) {
                log.warn(tnfe.getMessage());
            }
        }

        return loadedEntities;
    }

    @Override
    public List<TaskEntity> findTodayAndUnfinishedPastTasks() {
        try {
            PreparedStatement statement = connector.prepareStatement(TaskQueryStatements.FIND_TODAY_AND_ACTIVE_PAST);
            Date today = Date.valueOf(LocalDate.now());
            statement.setDate(1, today);
            statement.setDate(2, today);

            ResultSet results = connector.executeQuery(statement);
            return extractAllResults(results);
        } catch (SQLException sqle) {
            log.warn("Exception while trying to find tasks for today", sqle);
            return Collections.EMPTY_LIST;
        } finally {
            connector.finishedOperations();
        }
    }

    @Override
    public List<TaskEntity> findActiveByCategoryId(int categoryId) {
        try {
            PreparedStatement statement = connector.prepareStatement(TaskQueryStatements.FIND_ACTIVE_BY_CATEGORY);
            statement.setInt(1, categoryId);

            ResultSet results = connector.executeQuery(statement);
            return extractAllResults(results);
        } catch (SQLException sqle) {
            log.warn("Exception while trying to find active tasks by category", sqle);
            return Collections.EMPTY_LIST;
        } finally {
            connector.finishedOperations();
        }
    }

    @Override
    public List<TaskEntity> findCompletedTasks() {
        try {
            Statement statement = connector.createStatement();
            ResultSet results = connector.executeQuery(statement, TaskQueryStatements.FIND_COMPLETED);
            return extractAllResults(results);
        } catch (SQLException sqle) {
            log.warn("Exception while trying to find completed tasks", sqle);
            return Collections.EMPTY_LIST;
        } finally {
            connector.finishedOperations();
        }
    }

    @Override
    public List<TaskEntity> findActiveTasksBetween(LocalDate since, LocalDate until) {
        try {
            PreparedStatement statement = connector.prepareStatement(TaskQueryStatements.FIND_ACTIVE_BETWEEN);
            statement.setDate(1, Date.valueOf(since));
            statement.setDate(2, Date.valueOf(until));

            ResultSet results = connector.executeQuery(statement);
            return extractAllResults(results);
        } catch (SQLException sqle) {
            log.warn("Exception while trying to find active tasks between given times", sqle);
            return Collections.EMPTY_LIST;
        } finally {
            connector.finishedOperations();
        }
    }

    @Override
    public int getRowCount() {
        try {
            Statement statement = connector.createStatement();
            ResultSet result = connector.executeQuery(statement, TaskQueryStatements.ROW_COUNT);
            if (result.next()) {
                return result.getInt(1);
            }

            throw new RuntimeException("Failed to get row count of task table");
        } catch (SQLException sqle) {
            throw new RuntimeException("Excpetion while trying to get row count of task table", sqle);
        } finally {
            connector.finishedOperations();
        }
    }

    private List<TaskEntity> extractAllResults(ResultSet results) throws SQLException {
        List<TaskEntity> allLoadedEntities = new ArrayList<>();
        while (results.next()) {
            TaskEntity entity = extractResult(results);
            allLoadedEntities.add(entity);
        }

        return allLoadedEntities;
    }

    private TaskEntity extractResult(ResultSet resultRow) throws SQLException {
        boolean isCategoryIdNull = resultRow.getObject("CATEGORY_ID") == null;
        Integer categoryId = isCategoryIdNull ? null : resultRow.getInt("CATEGORY_ID");

        return TaskEntity.builder()
                .id(resultRow.getInt("ID"))
                .categoryId(categoryId)
                .taskDef(resultRow.getString("TASK_DEF"))
                .priority(resultRow.getInt("PRIORITY"))
                .deadline(resultRow.getDate("DEADLINE").toLocalDate())
                .completed(Boolean.parseBoolean(resultRow.getString("COMPLETED")))
                .build();
    }

}
