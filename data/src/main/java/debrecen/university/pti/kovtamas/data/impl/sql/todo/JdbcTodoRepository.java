package debrecen.university.pti.kovtamas.data.impl.sql.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.TodoEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.datasource.DataSourceManager;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TodoRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcTodoRepository implements TodoRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcTodoRepository.class);

    @Override
    public Set<TodoEntity> findAll() {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(TodoQueries.FIND_ALL);

            Set<TodoEntity> entities = new HashSet<>();
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
    public Set<TodoEntity> findByCategory(String category) {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            PreparedStatement prStatement = conn.prepareStatement(TodoQueries.FIND_BY_CATEGORY);
            prStatement.setString(1, category);
            ResultSet results = prStatement.executeQuery();

            Set<TodoEntity> entities = new HashSet<>();
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
    public TodoEntity findById(int id) throws TaskNotFoundException {
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
    public Set<TodoEntity> findByIds(Set<Integer> ids) throws TaskNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void save(TodoEntity entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveAll(Collection<TodoEntity> entities) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private TodoEntity convertRecordToEntity(ResultSet record) throws SQLException {
        return TodoEntity.builder()
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
