package debrecen.university.pti.kovtamas.data.test;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.JdbcTodoRepository;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRemovalException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TodoRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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
    private static final JdbcTestUtils JDBC_TEST_UTILS;

    private static final TodoRepository REPO;
    private static final DateTimeFormatter FORMATTER;

    static {
        FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        REPO = new JdbcTodoRepository(FORMATTER);
        JDBC_TEST_UTILS = new JdbcTestUtils(REPO);
    }

    public JdbcTodoRepositoryTest() {
    }

//    @BeforeClass
//    public static void switchToTestTable() {
//        JDBC_TEST_UTILS.switchToTestTable();
//    }
//
//    @AfterClass
//    public static void switchToOriginalTable() {
//        JDBC_TEST_UTILS.switchToOriginalTable();
//    }
//
//    @After
//    public void cleanTestTable() {
//        JDBC_TEST_UTILS.cleanTestTable();
//    }
//
//    @Test
//    public void queryMethodsOnEmptyTableTest() {
//        // find all
//        Set<TaskEntity> entities = REPO.findAll();
//        assertNotNull(entities);
//        assertEquals(0, entities.size());
//
//        // find by id/ids
//        try {
//            TaskEntity entity = REPO.findById(0);
//            fail("Found item by id in empty database table!");
//        } catch (TaskNotFoundException tnfe) {
//        }
//        try {
//            entities = REPO.findByIds(Arrays.asList(1, 2, 3, 4, 5, 6));
//            fail("Found items by id collection in empty database table!");
//        } catch (TaskNotFoundException tnfe) {
//        }
//
//        // find by category
//        entities = REPO.findByCategory("non existing category");
//        assertNotNull(entities);
//        assertEquals(0, entities.size());
//
//        int rowCount = REPO.getRowCount();
//        assertEquals(0, rowCount);
//    }
//
//    @Test
//    public void saveFindAllAndCleanTest() {
//        Set<TaskEntity> entities = TestDataGenerator.generateOriginalEntitySet();
//        JDBC_TEST_UTILS.populateDatabase(entities);
//        collectionEquals(entities, REPO.findAll());
//        JDBC_TEST_UTILS.cleanTestTable();
//
//        entities = TestDataGenerator.generateOriginalEntitySet();
//        entities.forEach(entity -> {
//            try {
//                REPO.save(entity);
//            } catch (TaskPersistenceException ex) {
//                String message = "Could not save entity:" + System.getProperty("line.separator") + entity.toString();
//                LOG.warn(message, ex);
//            }
//        });
//        collectionEquals(entities, REPO.findAll());
//    }
//
//    @Test
//    public void findByIdTest() {
//        Set<TaskEntity> entities = TestDataGenerator.generateOriginalEntitySet();
//        JDBC_TEST_UTILS.populateDatabase(entities);
//
//        int id = 1;
//        TaskEntity originalEntity = entities.stream()
//                .filter(e -> e.getId() == id)
//                .findFirst()
//                .get();
//
//        TaskEntity foundEntity = null;
//        try {
//            foundEntity = REPO.findById(id);
//        } catch (TaskNotFoundException tnfe) {
//            String message = "Could not find task with id: " + id;
//            LOG.warn(message, tnfe);
//            fail(message);
//        }
//
//        assertEquals(originalEntity, foundEntity);
//    }
//
//    @Test
//    public void findIdCollectionTest() {
//        Set<TaskEntity> entities = TestDataGenerator.generateOriginalEntitySet();
//        JDBC_TEST_UTILS.populateDatabase(entities);
//
//        List<Integer> ids = Arrays.asList(1, 2);
//        Set<TaskEntity> originalEntities = entities.stream()
//                .filter(e -> ids.contains(e.getId()))
//                .collect(Collectors.toSet());
//
//        try {
//            collectionEquals(originalEntities, REPO.findByIds(ids));
//        } catch (TaskNotFoundException tnfe) {
//            String message = "Could not find task(s) by id";
//            LOG.warn(message, tnfe);
//            fail(message);
//        }
//    }
//
//    @Test
//    public void findByCategoryTest() {
//        Set<TaskEntity> entities = TestDataGenerator.generateOriginalEntitySet();
//        JDBC_TEST_UTILS.populateDatabase(entities);
//
//        String category = "everyday life";
//        Set<TaskEntity> originalEntities = entities.stream()
//                .filter(e -> category.equals(e.getCategory()))
//                .collect(Collectors.toSet());
//
//        collectionEquals(originalEntities, REPO.findByCategory(category));
//    }
//
//    @Test
//    public void findTodayTasksTest() {
//        Set<TaskEntity> entities = TestDataGenerator.generateEntitiesForTodayTest(FORMATTER);
//        JDBC_TEST_UTILS.populateDatabase(entities);
//
//        Set<TaskEntity> expectedEntities = entities.stream()
//                .filter(entity -> !"exclude me".equals(entity.getCategory()))
//                .collect(Collectors.toSet());
//
//        Set<TaskEntity> results = REPO.findTodayTasks();
//        collectionEquals(expectedEntities, results);
//    }
//
//    @Test
//    public void findTasksUntilTest() {
//        Set<TaskEntity> entities = TestDataGenerator.generateEntitiesForUntilTest(FORMATTER);
//        JDBC_TEST_UTILS.populateDatabase(entities);
//
//        Set<TaskEntity> expectedEntities = entities.stream()
//                .filter(entity -> !"exclude me".equals(entity.getCategory()))
//                .collect(Collectors.toSet());
//
//        Set<TaskEntity> results = REPO.findTasksUntil(LocalDate.now().plusWeeks(2).format(FORMATTER), FORMATTER);
//        collectionEquals(expectedEntities, results);
//    }
//
//    @Test
//    public void removeTest() {
//        Set<TaskEntity> entities = TestDataGenerator.generateOriginalEntitySet();
//        JDBC_TEST_UTILS.populateDatabase(entities);
//
//        int id = 1;
//        Set<TaskEntity> originalEntities = entities.stream()
//                .filter(e -> e.getId() != id)
//                .collect(Collectors.toSet());
//
//        try {
//            REPO.remove(id);
//        } catch (TaskNotFoundException tnfe) {
//            String message = "Could not find task with id: " + id + " to remove!";
//            LOG.warn(message, tnfe);
//            fail(message);
//        } catch (TaskRemovalException tre) {
//            String message = "Could not remove task with id : " + id;
//            LOG.error(message, tre);
//            fail(message);
//        }
//
//        collectionEquals(originalEntities, REPO.findAll());
//    }
//
//    @Test
//    public void removeAllTest() {
//        Set<TaskEntity> entities = TestDataGenerator.generateOriginalEntitySet();
//        JDBC_TEST_UTILS.populateDatabase(entities);
//        assertEquals(entities.size(), REPO.getRowCount());
//
//        try {
//            Set<Integer> ids = entities.stream()
//                    .map(entity -> entity.getId())
//                    .collect(Collectors.toSet());
//            REPO.removeAll(ids);
//        } catch (TaskNotFoundException tnfe) {
//            String message = "Could not find task from collection to remove!";
//            LOG.warn(message, tnfe);
//            fail(message);
//        } catch (TaskRemovalException tre) {
//            String message = "Could not remove task specified in the collection!";
//            LOG.error(message, tre);
//            fail(message);
//        }
//
//        assertEquals(0, REPO.getRowCount());
//    }
//
//    @Test
//    public void singleUpdateTest() {
//        Set<TaskEntity> entities = TestDataGenerator.generateOriginalEntitySet();
//        JDBC_TEST_UTILS.populateDatabase(entities);
//
//        TaskEntity toUpdate = entities.iterator().next();
//        String newCategory = "new category";
//        toUpdate.setCategory(newCategory);
//        toUpdate.setPriority(0);
//        toUpdate.setRepeating(!toUpdate.isRepeating());
//
//        try {
//            REPO.save(toUpdate);
//        } catch (TaskPersistenceException tsfe) {
//            String message = "Exception while trying to update entity!";
//            LOG.error(message, tsfe);
//            fail(message);
//        }
//
//        TaskEntity readEntity = null;
//        try {
//            readEntity = REPO.findById(toUpdate.getId());
//        } catch (TaskNotFoundException tnfe) {
//            String message = "Could not find the updated task!";
//            LOG.error(message, tnfe);
//            fail(message);
//        }
//
//        assertEquals(readEntity, toUpdate);
//    }
//
//    @Test
//    public void mixedSaveUpdateTest() {
//        Set<TaskEntity> entities = TestDataGenerator.generateOriginalEntitySet();
//        TaskEntity toUpdate = entities.iterator().next();
//        try {
//            REPO.save(toUpdate);
//        } catch (TaskPersistenceException tsfe) {
//            String message = "Could not save single entity!";
//            LOG.error(message, tsfe);
//            fail(message);
//        }
//
//        toUpdate.setDeadline("1009.12.12");
//        toUpdate.setPriority(8);
//        toUpdate.setSubTaskIds("7,8,5,6,2");
//
//        try {
//            REPO.saveAll(entities);
//        } catch (TaskPersistenceException tpe) {
//            String message = "Could not saveAll entities!";
//            LOG.error(message, tpe);
//            fail(message);
//        }
//
//        collectionEquals(entities, REPO.findAll());
//    }
//
//    @Test
//    public void IdsAreSetTest() {
//        Set<TaskEntity> entities = TestDataGenerator.generateOriginalEntitySet();
//        entities.forEach(entity -> entity.setId(null));
//
//        try {
//            REPO.saveAll(entities);
//        } catch (TaskPersistenceException tpe) {
//            String message = "Could not saveAll entities!";
//            LOG.error(message, tpe);
//            fail(message);
//        }
//
//        entities.forEach(entity -> assertNotNull(entity.getId()));
//    }
//
//    @Test
//    public void findAllCategoriesTest() {
//        Set<TaskEntity> entities = TestDataGenerator.generateEntitiesForFindCategoriesTest();
//        JDBC_TEST_UTILS.populateDatabase(entities);
//
//        Set<String> expected = new HashSet<>();
//        expected.add("personal");
//        expected.add("work");
//
//        Set<String> result = REPO.findAllCategories();
//        assertEquals(expected.size(), result.size());
//        result.removeAll(expected);
//        assertEquals(0, result.size());
//    }
//
//    private void populateDatabase(Collection<TaskEntity> entities) {
//        try {
//            REPO.saveAll(entities);
//        } catch (TaskPersistenceException ex) {
//            String message = "Could not populate table for the test!";
//            LOG.error(message, ex);
//            fail(message);
//        }
//    }
//
//    private void collectionEquals(Collection<TaskEntity> c1, Collection<TaskEntity> c2) {
//        assertEquals(c1.size(), c2.size());
//
//        List<TaskEntity> l1 = new ArrayList<>(c1);
//        List<TaskEntity> l2 = new ArrayList<>(c2);
//        Comparator<TaskEntity> cmp = (e1, e2) -> e1.getId() - e2.getId();
//
//        Collections.sort(l1, cmp);
//        Collections.sort(l2, cmp);
//
//        for (int i = 0; i < l1.size(); i++) {
//            assertEquals(l1.get(i), l2.get(i));
//        }
//    }
}
