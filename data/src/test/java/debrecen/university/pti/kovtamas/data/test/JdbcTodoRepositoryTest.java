package debrecen.university.pti.kovtamas.data.test;

import debrecen.university.pti.kovtamas.data.entity.todo.TodoEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.datasource.DataSourceManager;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.JdbcTodoRepository;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskSaveFailureException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TodoRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class JdbcTodoRepositoryTest {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcTodoRepositoryTest.class.getName());

    private static TodoRepository repo;

    public JdbcTodoRepositoryTest() {
        repo = new JdbcTodoRepository();
    }

    @BeforeClass
    public static void switchToTestTable() {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            Statement statement = conn.createStatement();
            statement.executeUpdate(JdbcTestQueries.RENAME_ORIGINAL_TABLE);
            statement.executeUpdate(JdbcTestQueries.CREATE_TEST_TABLE);
        } catch (SQLException sqle) {
            LOG.error("Could not switch to test table!", sqle);
            fail();     // Do not start the tests on the original table
        }
    }

    @AfterClass
    public static void switchToOriginalTable() {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            Statement statement = conn.createStatement();
            statement.executeUpdate(JdbcTestQueries.DROP_TEST_TABLE);
            statement.executeUpdate(JdbcTestQueries.RESTORE_ORIGINAL_TABLE);
        } catch (SQLException sqle) {
            LOG.error("Could not switch back to the original table!", sqle);
            fail();
        }
    }

    @Test
    public void findMethodsOnEmptyTableTest() {
        Set<TodoEntity> entities = repo.findAll();
        assertNotNull(entities);
        assertEquals(0, entities.size());

        try {
            TodoEntity entity = repo.findById(0);
            fail("Found item by id in empty database table!");
        } catch (TaskNotFoundException tnfe) {
        }

        try {
            entities = repo.findByIds(Arrays.asList(1, 2, 3, 4, 5, 6));
            fail("Found items by id collection in empty database table!");
        } catch (TaskNotFoundException tnfe) {
        }

        entities = repo.findByCategory("non existing category");
        assertNotNull(entities);
        assertEquals(0, entities.size());

        entities = repo.findByNotCategory("skip this if you can");
        assertNotNull(entities);
        assertEquals(0, entities.size());
    }

//    @Test
//    public void saveAndFindAllTest() {
//
//    }
    private void populateTable() {
        Collection<TodoEntity> entities = generateEntities();
        try {
            repo.saveAll(entities);
        } catch (TaskSaveFailureException ex) {
            LOG.error("Could not populate test table! Aborting test...");
            fail();
            // @AfterClass method is guaranteed to run even when exception is thrown
        }
    }

    private List<TodoEntity> generateEntities() {
        List<TodoEntity> entities = new ArrayList<>(4);
        entities.add(
                TodoEntity.builder()
                        .id(1)
                        .taskDef("Go to the gym")
                        .priority(1)
                        .deadline("2017.1.10")
                        .category("personal")
                        .subTaskIds(null)
                        .repeating(false)
                        .build()
        );
        entities.add(
                TodoEntity.builder()
                        .id(2)
                        .taskDef("Go shopping")
                        .priority(2)
                        .deadline("2017.1.12")
                        .category("everyday life")
                        .subTaskIds("3, 4")
                        .repeating(true)
                        .build()
        );
        entities.add(
                TodoEntity.builder()
                        .id(3)
                        .taskDef("Prepare the bike")
                        .priority(2)
                        .deadline("2017.1.12")
                        .category("everyday life")
                        .subTaskIds(null)
                        .repeating(null)
                        .build()
        );
        entities.add(
                TodoEntity.builder()
                        .id(4)
                        .taskDef("Lock the door")
                        .priority(1)
                        .deadline("2017.1.12")
                        .category("everyday life")
                        .subTaskIds(null)
                        .repeating(null)
                        .build()
        );

        return entities;
    }
}
