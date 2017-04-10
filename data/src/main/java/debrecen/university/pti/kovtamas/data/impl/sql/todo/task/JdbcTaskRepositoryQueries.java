package debrecen.university.pti.kovtamas.data.impl.sql.todo.task;

import debrecen.university.pti.kovtamas.data.entity.todo.RefactoredTaskEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.datasource.DataSourceManager;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepositoryQueries;
import java.sql.Connection;
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

    static {
        INSTANCE = new JdbcTaskRepositoryQueries();
    }

    static public JdbcTaskRepositoryQueries getInstance() {
        return INSTANCE;
    }

    @Override
    public List<RefactoredTaskEntity> findAll() {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(TaskQueryStatements.FIND_ALL);
            return extractAllResults(results);
        } catch (SQLException sqle) {
            log.warn("Exception while trying to find all tasks", sqle);
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public RefactoredTaskEntity findById(int id) throws TaskNotFoundException {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            return findById(conn, id);
        } catch (SQLException sqle) {
            throw new TaskNotFoundException("Exception while trying to find task with id: " + id, sqle);
        }
    }

    private RefactoredTaskEntity findById(Connection conn, int id) throws SQLException, TaskNotFoundException {
        PreparedStatement statement = conn.prepareStatement(TaskQueryStatements.FIND_BY_ID);
        statement.setInt(1, id);

        ResultSet results = statement.executeQuery();
        if (results.next()) {
            return extractResult(results);
        }

        throw new TaskNotFoundException("Could not find task with id: " + id);
    }

    @Override
    public List<RefactoredTaskEntity> findByIds(@NonNull final Collection<Integer> ids) {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            return findAllExistingTasksByIds(conn, ids);
        } catch (SQLException exception) {
            log.warn("Exception while trying to find tasks by ids", exception);
            return Collections.EMPTY_LIST;
        }
    }

    private List<RefactoredTaskEntity> findAllExistingTasksByIds(Connection conn, Collection<Integer> ids) throws SQLException {
        List<RefactoredTaskEntity> loadedEntities = new ArrayList<>(ids.size());
        for (Integer id : ids) {
            try {
                RefactoredTaskEntity entity = findById(conn, id);
                loadedEntities.add(entity);
            } catch (TaskNotFoundException tnfe) {
                log.warn(tnfe.getMessage());
            }
        }

        return loadedEntities;
    }

    @Override
    public List<RefactoredTaskEntity> findTodayAndUnfinishedPastTasks() {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            return findTodayAndUnfinishedPastTasks(conn);
        } catch (SQLException sqle) {
            log.warn("Exception while trying to find tasks for today", sqle);
            return Collections.EMPTY_LIST;
        }
    }

    private List<RefactoredTaskEntity> findTodayAndUnfinishedPastTasks(Connection conn) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(TaskQueryStatements.FIND_TODAY_AND_ACTIVE_PAST);
        Date today = Date.valueOf(LocalDate.now());
        statement.setDate(1, today);
        statement.setDate(2, today);

        ResultSet results = statement.executeQuery();
        return extractAllResults(results);
    }

    @Override
    public List<RefactoredTaskEntity> findActiveByCategoryId(int categoryId) {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            return findActiveByCategoryId(conn, categoryId);
        } catch (SQLException sqle) {
            log.warn("Exception while trying to find active tasks by category", sqle);
            return Collections.EMPTY_LIST;
        }
    }

    private List<RefactoredTaskEntity> findActiveByCategoryId(Connection conn, int categoryId) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(TaskQueryStatements.FIND_ACTIVE_BY_CATEGORY);
        statement.setInt(1, categoryId);

        ResultSet results = statement.executeQuery();
        return extractAllResults(results);
    }

    @Override
    public List<RefactoredTaskEntity> findCompletedTasks() {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(TaskQueryStatements.FIND_COMPLETED);
            return extractAllResults(results);
        } catch (SQLException sqle) {
            log.warn("Exception while trying to find completed tasks", sqle);
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public List<RefactoredTaskEntity> findActiveTasksBetween(LocalDate since, LocalDate until) {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            PreparedStatement statement = conn.prepareStatement(TaskQueryStatements.FIND_ACTIVE_BETWEEN);
            statement.setDate(1, Date.valueOf(since));
            statement.setDate(2, Date.valueOf(until));

            ResultSet results = statement.executeQuery();
            return extractAllResults(results);
        } catch (SQLException sqle) {
            log.warn("Exception while trying to find active tasks between given times", sqle);
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public int getRowCount() {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(TaskQueryStatements.ROW_COUNT);
            if (result.next()) {
                return result.getInt(1);
            }

            throw new RuntimeException("Failed to get row count of task table");
        } catch (SQLException sqle) {
            throw new RuntimeException("Excpetion while trying to get row count of task table", sqle);
        }
    }

    private List<RefactoredTaskEntity> extractAllResults(ResultSet results) throws SQLException {
        List<RefactoredTaskEntity> allLoadedEntities = new ArrayList<>();
        while (results.next()) {
            RefactoredTaskEntity entity = extractResult(results);
            allLoadedEntities.add(entity);
        }

        return allLoadedEntities;
    }

    private RefactoredTaskEntity extractResult(ResultSet resultRow) throws SQLException {
        boolean isCategoryIdNull = resultRow.getObject("CATEGORY_ID") == null;
        Integer categoryId = isCategoryIdNull ? null : resultRow.getInt("CATEGORY_ID");

        return RefactoredTaskEntity.builder()
                .id(resultRow.getInt("ID"))
                .categoryId(categoryId)
                .taskDef(resultRow.getString("TASK_DEF"))
                .priority(resultRow.getInt("PRIORITY"))
                .deadline(resultRow.getDate("DEADLINE").toLocalDate())
                .completed(Boolean.parseBoolean(resultRow.getString("COMPLETED")))
                .build();
    }

}
