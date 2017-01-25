package debrecen.university.pti.kovtamas.data.test;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.datasource.DataSourceManager;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TodoRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class JdbcTestUtils {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcTestUtils.class);
    private final TodoRepository REPO;

    public JdbcTestUtils(TodoRepository repo) {
        this.REPO = repo;
    }

    public void switchToTestTable() {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            Statement statement = conn.createStatement();
            statement.executeUpdate(JdbcTestQueries.RENAME_ORIGINAL_TABLE);
            statement.executeUpdate(JdbcTestQueries.CREATE_TEST_TABLE);
        } catch (SQLException sqle) {
            LOG.error("Could not switch to test table!", sqle);
            fail();     // Do not start the tests on the original table
        }
    }

    public void switchToOriginalTable() {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            Statement statement = conn.createStatement();
            statement.executeUpdate(JdbcTestQueries.DROP_TEST_TABLE);
            statement.executeUpdate(JdbcTestQueries.RESTORE_ORIGINAL_TABLE);
        } catch (SQLException sqle) {
            LOG.error("Could not switch back to the original table!", sqle);
            fail();
        }
    }

    public void cleanTestTable() {
        REPO.clean();
        int rowCount = REPO.getRowCount();
        assertEquals(0, rowCount);
    }

    public void populateDatabase(Collection<TaskEntity> entities) {
        try {
            REPO.saveAll(entities);
        } catch (TaskPersistenceException ex) {
            String message = "Could not populate table for the test!";
            LOG.error(message, ex);
            fail(message);
        }
    }

}
