package debrecen.university.pti.kovtamas.data.test.task;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.task.JdbcTaskRepositoryQueries;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.task.JdbcTaskRepositoryUpdates;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRemovalException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepositoryQueries;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepositoryUpdates;
import debrecen.university.pti.kovtamas.data.test.util.JdbcTestUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//@Ignore
public class JdbcTaskRepositoryUpdatesTest {

    private final TaskRepositoryUpdates taskUpdates;
    private final TaskRepositoryQueries taskQueries;

    public JdbcTaskRepositoryUpdatesTest() {
        taskUpdates = JdbcTaskRepositoryUpdates.getInstance();
        taskQueries = JdbcTaskRepositoryQueries.getInstance();
    }

    @BeforeClass
    static public void switchToTestTables() {
        JdbcTestUtils.switchToTestTables(JdbcTestUtils.TestType.UNIT);
    }

    @AfterClass
    static public void switchToProductionTables() {
        JdbcTestUtils.switchToProductionTables();
    }

    @After
    public void clearTable() {
        taskUpdates.clearTable();
    }

    @Test
    public void clearTableTest() throws TaskPersistenceException {
        final int testEntityCount = 7;
        List<TaskEntity> testEntities = TaskTestDataGenerator.generateEntities(testEntityCount);
        taskUpdates.saveOrUpdateAll(testEntities);

        int expectedSavedEntityCount = testEntities.size();
        int actualSavedEntityCount = taskQueries.getRowCount();
        assertEquals(expectedSavedEntityCount, actualSavedEntityCount);

        taskUpdates.clearTable();

        expectedSavedEntityCount = 0;
        actualSavedEntityCount = taskQueries.getRowCount();
        assertEquals(expectedSavedEntityCount, actualSavedEntityCount);
    }

    @Test
    public void saveTest() throws TaskPersistenceException {
        final int testEntityCount = 7;
        List<TaskEntity> unsavedEntities = TaskTestDataGenerator.generateEntities(testEntityCount);
        List<TaskEntity> savedEntities = saveEntitiesOneByOne(unsavedEntities);
        List<TaskEntity> loadedEntities = taskQueries.findAll();

        assertUnsavedSavedLoaded(unsavedEntities, savedEntities, loadedEntities);
    }

    @Test
    public void saveAllTest() throws TaskPersistenceException {
        final int testEntityCount = 7;
        List<TaskEntity> unsavedEntities = TaskTestDataGenerator.generateEntities(testEntityCount);
        List<TaskEntity> savedEntities = taskUpdates.saveOrUpdateAll(unsavedEntities);
        List<TaskEntity> loadedEntities = taskQueries.findAll();

        assertUnsavedSavedLoaded(unsavedEntities, savedEntities, loadedEntities);
    }

    private void assertUnsavedSavedLoaded(List<TaskEntity> unsaved, List<TaskEntity> saved,
            List<TaskEntity> loaded) {
        final int entityCount = unsaved.size();
        for (int i = 0; i < entityCount; i++) {
            TaskEntity currentUnsavedEntity = unsaved.get(i);
            TaskEntity currentSavedEntity = saved.get(i);
            TaskEntity currentLoadedEntity = loaded.get(i);

            assertTrue(currentSavedEntity.hasId());
            assertEquals(currentLoadedEntity.getId(), currentSavedEntity.getId());
            currentUnsavedEntity.setId(currentSavedEntity.getId());
            assertEquals(currentUnsavedEntity, currentSavedEntity);
            assertEquals(currentUnsavedEntity, currentLoadedEntity);
        }
    }

    @Test
    public void updateTest() throws TaskPersistenceException, TaskNotFoundException {
        TaskEntity unsavedEntity = TaskTestDataGenerator.generateOneEntity();
        TaskEntity expectedEntity = taskUpdates.saveOrUpdate(unsavedEntity);
        expectedEntity.setCategoryId(null);
        expectedEntity.setDeadline(LocalDate.now().plusDays(12));
        expectedEntity.setPriority(1);
        expectedEntity.setTaskDef("Updated task");
        expectedEntity.setCompleted(true);

        TaskEntity updatedEntity = taskUpdates.saveOrUpdate(expectedEntity);
        TaskEntity loadedEntity = taskQueries.findById(expectedEntity.getId());

        assertEquals(expectedEntity, updatedEntity);
        assertEquals(expectedEntity, loadedEntity);
    }

    @Test
    public void voidUpdateAll() throws TaskPersistenceException {
        final int testEntityCount = 3;
        List<TaskEntity> unsavedEntities = TaskTestDataGenerator.generateEntities(testEntityCount);
        List<TaskEntity> expectedEntities = taskUpdates.saveOrUpdateAll(unsavedEntities);
        for (TaskEntity expectedEntity : expectedEntities) {
            expectedEntity.setDeadline(expectedEntity.getDeadline().plusDays(2));
            expectedEntity.setPriority(expectedEntity.getPriority() % 2);
            expectedEntity.setTaskDef(expectedEntity.getTaskDef() + " upadated");
            expectedEntity.setCompleted(!expectedEntity.isCompleted());
        }

        List<TaskEntity> updatedEntities = taskUpdates.saveOrUpdateAll(expectedEntities);
        List<TaskEntity> loadedEntities = taskQueries.findAll();

        for (int i = 0; i < testEntityCount; i++) {
            assertEquals(expectedEntities.get(i), updatedEntities.get(i));
            assertEquals(expectedEntities.get(i), loadedEntities.get(i));
        }
    }

    private List<TaskEntity> saveEntitiesOneByOne(Collection<TaskEntity> entitiesToSave)
            throws TaskPersistenceException {
        List<TaskEntity> savedEntities = new ArrayList<>(entitiesToSave.size());
        for (TaskEntity entity : entitiesToSave) {
            TaskEntity savedEntity = taskUpdates.saveOrUpdate(entity);
            savedEntities.add(savedEntity);
        }

        return savedEntities;
    }

    @Test
    public void removeTest() throws TaskPersistenceException, TaskRemovalException {
        final int testEntityCount = 3;
        List<TaskEntity> unsavedEntities = TaskTestDataGenerator.generateEntities(testEntityCount);
        List<TaskEntity> savedEntities = taskUpdates.saveOrUpdateAll(unsavedEntities);

        while (!savedEntities.isEmpty()) {
            TaskEntity entityToRemove = savedEntities.remove(0);
            taskUpdates.remove(entityToRemove.getId());

            int expectedSavedEntityCount = savedEntities.size();
            int actualSavedEntityCount = taskQueries.getRowCount();
            assertEquals(expectedSavedEntityCount, actualSavedEntityCount);
        }
    }

    @Test
    public void removeAllTest() throws TaskPersistenceException, TaskRemovalException {
        final int testEntityCount = 3;
        List<TaskEntity> unsavedEntities = TaskTestDataGenerator.generateEntities(testEntityCount);
        List<TaskEntity> savedEntities = taskUpdates.saveOrUpdateAll(unsavedEntities);

        List<Integer> removeTaskIds = savedEntities.stream()
                .map(TaskEntity::getId)
                .collect(Collectors.toList());
        taskUpdates.removeAll(removeTaskIds);

        final int expectedTaskCount = 0;
        final int actualTaskCount = taskQueries.getRowCount();
        assertEquals(expectedTaskCount, actualTaskCount);
    }

}
