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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.After;
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

    @After
    public void cleanTestTable() {
        clean();
    }

    @Test
    public void queryMethodsOnEmptyTableTest() {
        // find all
        Set<TodoEntity> entities = repo.findAll();
        assertNotNull(entities);
        assertEquals(0, entities.size());

        // find by id/ids
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

        // find by category/not category
        entities = repo.findByCategory("non existing category");
        assertNotNull(entities);
        assertEquals(0, entities.size());
        entities = repo.findByNotCategory("skip this if you can");
        assertNotNull(entities);
        assertEquals(0, entities.size());

        int rowCount = repo.getRowCount();
        assertEquals(0, rowCount);
    }

    @Test
    public void saveFindAllAndCleanTest() {
        Set<TodoEntity> entities = generateEntities();
        try {
            repo.saveAll(entities);
        } catch (TaskSaveFailureException ex) {
            String message = "Could not save entity collection!";
            LOG.error(message, ex);
            fail(message);
        }
        validateSave(entities);
        clean();
        entities.forEach(entity -> {
            try {
                repo.save(entity);
            } catch (TaskSaveFailureException ex) {
                String message = "Could not save entity:" + System.getProperty("line.separator") + entity.toString();
                LOG.warn(message, ex);
            }
        });
        validateSave(entities);
    }

    private void validateSave(Set<TodoEntity> entities) {
        int rowCount = repo.getRowCount();
        assertEquals(entities.size(), rowCount);

        List<TodoEntity> originals = new ArrayList<>(entities);
        List<TodoEntity> founds = new ArrayList<>(repo.findAll());
        Comparator<TodoEntity> cmp = (e1, e2) -> e1.getId() - e2.getId();
        Collections.sort(originals, cmp);
        Collections.sort(founds, cmp);

        for (int i = 0; i < originals.size(); i++) {
            assertEquals(originals.get(i), founds.get(i));
        }
    }

    private void clean() {
        repo.clean();
        int rowCount = repo.getRowCount();
        assertEquals(0, rowCount);
    }

    private void populateTable() {
        Collection<TodoEntity> entities = generateEntities();
        try {
            repo.saveAll(entities);
        } catch (TaskSaveFailureException ex) {
            LOG.error("Could not populate test table! Aborting test...", ex);
            fail();
            // @AfterClass method is guaranteed to run even when exception is thrown
        }
    }

    private Set<TodoEntity> generateEntities() {
        Set<TodoEntity> entities = new HashSet<>();
        entities.add(
                TodoEntity.builder()
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
                        .taskDef("Prepare the bike")
                        .priority(2)
                        .deadline("2017.1.12")
                        .category("everyday life")
                        .subTaskIds(null)
                        .build()
        );
        entities.add(
                TodoEntity.builder()
                        .taskDef("Lock the door")
                        .priority(1)
                        .deadline("2017.1.12")
                        .category("everyday life")
                        .subTaskIds(null)
                        .build()
        );

        return entities;
    }
}
