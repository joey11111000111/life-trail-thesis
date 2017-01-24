package debrecen.university.pti.kovtamas.data.test;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.datasource.DataSourceManager;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.JdbcTodoRepository;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRemovalException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TodoRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
    private static DateTimeFormatter formatter;

    public JdbcTodoRepositoryTest() {
        formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        repo = new JdbcTodoRepository(formatter);
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
        Set<TaskEntity> entities = repo.findAll();
        assertNotNull(entities);
        assertEquals(0, entities.size());

        // find by id/ids
        try {
            TaskEntity entity = repo.findById(0);
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
        Set<TaskEntity> entities = TestDataGenerator.generateOriginalEntitySet();
        populateDatabase(entities);
        collectionEquals(entities, repo.findAll());
        clean();

        entities = TestDataGenerator.generateOriginalEntitySet();
        entities.forEach(entity -> {
            try {
                repo.save(entity);
            } catch (TaskPersistenceException ex) {
                String message = "Could not save entity:" + System.getProperty("line.separator") + entity.toString();
                LOG.warn(message, ex);
            }
        });
        collectionEquals(entities, repo.findAll());
    }

    @Test
    public void findByIdTest() {
        Set<TaskEntity> entities = TestDataGenerator.generateOriginalEntitySet();
        populateDatabase(entities);

        int id = 1;
        TaskEntity originalEntity = entities.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .get();

        TaskEntity foundEntity = null;
        try {
            foundEntity = repo.findById(id);
        } catch (TaskNotFoundException tnfe) {
            String message = "Could not find task with id: " + id;
            LOG.warn(message, tnfe);
            fail(message);
        }

        assertEquals(originalEntity, foundEntity);
    }

    @Test
    public void findIdCollectionTest() {
        Set<TaskEntity> entities = TestDataGenerator.generateOriginalEntitySet();
        populateDatabase(entities);

        List<Integer> ids = Arrays.asList(1, 2);
        Set<TaskEntity> originalEntities = entities.stream()
                .filter(e -> ids.contains(e.getId()))
                .collect(Collectors.toSet());

        try {
            collectionEquals(originalEntities, repo.findByIds(ids));
        } catch (TaskNotFoundException tnfe) {
            String message = "Could not find task(s) by id";
            LOG.warn(message, tnfe);
            fail(message);
        }
    }

    @Test
    public void findByCategoryTest() {
        Set<TaskEntity> entities = TestDataGenerator.generateOriginalEntitySet();
        populateDatabase(entities);

        String category = "everyday life";
        Set<TaskEntity> originalEntities = entities.stream()
                .filter(e -> category.equals(e.getCategory()))
                .collect(Collectors.toSet());

        collectionEquals(originalEntities, repo.findByCategory(category));
    }

    @Test
    public void findByNotCategory() {
        Set<TaskEntity> entities = TestDataGenerator.generateOriginalEntitySet();
        populateDatabase(entities);

        String catToSkip = "personal";
        Set<TaskEntity> originalEntities = entities.stream()
                .filter(e -> !catToSkip.equals(e.getCategory()))
                .collect(Collectors.toSet());
        collectionEquals(originalEntities, repo.findByNotCategory(catToSkip));
    }

    @Test
    public void findTodayTasksTest() {
        Set<TaskEntity> entities = TestDataGenerator.generateEntitiesForTodayTest(formatter);
        populateDatabase(entities);

        Set<TaskEntity> expectedEntities = entities.stream()
                .filter(entity -> !"exclude me".equals(entity.getCategory()))
                .collect(Collectors.toSet());

        Set<TaskEntity> results = repo.findTodayTasks();
        collectionEquals(expectedEntities, results);
    }

    @Test
    public void findTasksUntilTest() {
        Set<TaskEntity> entities = TestDataGenerator.generateEntitiesForUntilTest(formatter);
        populateDatabase(entities);

        Set<TaskEntity> expectedEntities = entities.stream()
                .filter(entity -> !"exclude me".equals(entity.getCategory()))
                .collect(Collectors.toSet());

        Set<TaskEntity> results = repo.findTasksUntil(LocalDate.now().plusWeeks(2).format(formatter), formatter);
        collectionEquals(expectedEntities, results);
    }

    @Test
    public void removeTest() {
        Set<TaskEntity> entities = TestDataGenerator.generateOriginalEntitySet();
        populateDatabase(entities);

        int id = 1;
        Set<TaskEntity> originalEntities = entities.stream()
                .filter(e -> e.getId() != id)
                .collect(Collectors.toSet());

        try {
            repo.remove(id);
        } catch (TaskNotFoundException tnfe) {
            String message = "Could not find task with id: " + id + " to remove!";
            LOG.warn(message, tnfe);
            fail(message);
        } catch (TaskRemovalException tre) {
            String message = "Could not remove task with id : " + id;
            LOG.error(message, tre);
            fail(message);
        }

        collectionEquals(originalEntities, repo.findAll());
    }

    @Test
    public void removeAllTest() {
        Set<TaskEntity> entities = TestDataGenerator.generateOriginalEntitySet();
        populateDatabase(entities);
        assertEquals(entities.size(), repo.getRowCount());

        try {
            Set<Integer> ids = entities.stream()
                    .map(entity -> entity.getId())
                    .collect(Collectors.toSet());
            repo.removeAll(ids);
        } catch (TaskNotFoundException tnfe) {
            String message = "Could not find task from collection to remove!";
            LOG.warn(message, tnfe);
            fail(message);
        } catch (TaskRemovalException tre) {
            String message = "Could not remove task specified in the collection!";
            LOG.error(message, tre);
            fail(message);
        }

        assertEquals(0, repo.getRowCount());
    }

    @Test
    public void singleUpdateTest() {
        Set<TaskEntity> entities = TestDataGenerator.generateOriginalEntitySet();
        populateDatabase(entities);

        TaskEntity toUpdate = entities.iterator().next();
        String newCategory = "new category";
        toUpdate.setCategory(newCategory);
        toUpdate.setPriority(0);
        toUpdate.setRepeating(!toUpdate.isRepeating());

        try {
            repo.save(toUpdate);
        } catch (TaskPersistenceException tsfe) {
            String message = "Exception while trying to update entity!";
            LOG.error(message, tsfe);
            fail(message);
        }

        TaskEntity readEntity = null;
        try {
            readEntity = repo.findById(toUpdate.getId());
        } catch (TaskNotFoundException tnfe) {
            String message = "Could not find the updated task!";
            LOG.error(message, tnfe);
            fail(message);
        }

        assertEquals(readEntity, toUpdate);
    }

    @Test
    public void mixedSaveUpdateTest() {
        Set<TaskEntity> entities = TestDataGenerator.generateOriginalEntitySet();
        TaskEntity toUpdate = entities.iterator().next();
        try {
            repo.save(toUpdate);
        } catch (TaskPersistenceException tsfe) {
            String message = "Could not save single entity!";
            LOG.error(message, tsfe);
            fail(message);
        }

        toUpdate.setDeadline("1009.12.12");
        toUpdate.setPriority(8);
        toUpdate.setSubTaskIds("7,8,5,6,2");

        try {
            repo.saveAll(entities);
        } catch (TaskPersistenceException tpe) {
            String message = "Could not saveAll entities!";
            LOG.error(message, tpe);
            fail(message);
        }

        collectionEquals(entities, repo.findAll());
    }

    @Test
    public void IdsAreSetTest() {
        Set<TaskEntity> entities = TestDataGenerator.generateOriginalEntitySet();
        entities.forEach(entity -> entity.setId(null));

        try {
            repo.saveAll(entities);
        } catch (TaskPersistenceException tpe) {
            String message = "Could not saveAll entities!";
            LOG.error(message, tpe);
            fail(message);
        }

        entities.forEach(entity -> assertNotNull(entity.getId()));
    }

    private void populateDatabase(Collection<TaskEntity> entities) {
        try {
            repo.saveAll(entities);
        } catch (TaskPersistenceException ex) {
            String message = "Could not populate table for the test!";
            LOG.error(message, ex);
            fail(message);
        }
    }

    private void collectionEquals(Collection<TaskEntity> c1, Collection<TaskEntity> c2) {
        assertEquals(c1.size(), c2.size());

        List<TaskEntity> l1 = new ArrayList<>(c1);
        List<TaskEntity> l2 = new ArrayList<>(c2);
        Comparator<TaskEntity> cmp = (e1, e2) -> e1.getId() - e2.getId();

        Collections.sort(l1, cmp);
        Collections.sort(l2, cmp);

        for (int i = 0; i < l1.size(); i++) {
            assertEquals(l1.get(i), l2.get(i));
        }
    }

    private void clean() {
        repo.clean();
        int rowCount = repo.getRowCount();
        assertEquals(0, rowCount);
    }

}
