package debrecen.university.pti.kovtamas.data.test.refactored.task;

import debrecen.university.pti.kovtamas.data.entity.todo.RefactoredTaskEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.task.JdbcTaskRepositoryQueries;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.task.JdbcTaskRepositoryUpdates;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRemovalException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepositoryQueries;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepositoryUpdates;
import debrecen.university.pti.kovtamas.data.test.refactored.util.JdbcTestUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        List<RefactoredTaskEntity> testEntities = TaskTestDataGenerator.generateEntities(testEntityCount);
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
        List<RefactoredTaskEntity> unsavedEntities = TaskTestDataGenerator.generateEntities(testEntityCount);
        List<RefactoredTaskEntity> savedEntities = saveEntitiesOneByOne(unsavedEntities);
        List<RefactoredTaskEntity> loadedEntities = taskQueries.findAll();

        assertUnsavedSavedLoaded(unsavedEntities, savedEntities, loadedEntities);
    }

    @Test
    public void saveAllTest() throws TaskPersistenceException {
        final int testEntityCount = 7;
        List<RefactoredTaskEntity> unsavedEntities = TaskTestDataGenerator.generateEntities(testEntityCount);
        List<RefactoredTaskEntity> savedEntities = taskUpdates.saveOrUpdateAll(unsavedEntities);
        List<RefactoredTaskEntity> loadedEntities = taskQueries.findAll();

        assertUnsavedSavedLoaded(unsavedEntities, savedEntities, loadedEntities);
    }

    private void assertUnsavedSavedLoaded(List<RefactoredTaskEntity> unsaved, List<RefactoredTaskEntity> saved,
            List<RefactoredTaskEntity> loaded) {
        final int entityCount = unsaved.size();
        for (int i = 0; i < entityCount; i++) {
            RefactoredTaskEntity currentUnsavedEntity = unsaved.get(i);
            RefactoredTaskEntity currentSavedEntity = saved.get(i);
            RefactoredTaskEntity currentLoadedEntity = loaded.get(i);

            assertTrue(currentSavedEntity.hasId());
            assertEquals(currentLoadedEntity.getId(), currentSavedEntity.getId());
            currentUnsavedEntity.setId(currentSavedEntity.getId());
            assertEquals(currentUnsavedEntity, currentSavedEntity);
            assertEquals(currentUnsavedEntity, currentLoadedEntity);
        }
    }

    @Test
    public void updateTest() throws TaskPersistenceException, TaskNotFoundException {
        RefactoredTaskEntity unsavedEntity = TaskTestDataGenerator.generateOneEntity();
        RefactoredTaskEntity expectedEntity = taskUpdates.saveOrUpdate(unsavedEntity);
        expectedEntity.setCategoryId(null);
        expectedEntity.setDeadline(LocalDate.now().plusDays(12));
        expectedEntity.setPriority(1);
        expectedEntity.setTaskDef("Updated task");
        expectedEntity.setCompleted(true);

        RefactoredTaskEntity updatedEntity = taskUpdates.saveOrUpdate(expectedEntity);
        RefactoredTaskEntity loadedEntity = taskQueries.findById(expectedEntity.getId());

        assertEquals(expectedEntity, updatedEntity);
        assertEquals(expectedEntity, loadedEntity);
    }

    @Test
    public void voidUpdateAll() throws TaskPersistenceException {
        final int testEntityCount = 3;
        List<RefactoredTaskEntity> unsavedEntities = TaskTestDataGenerator.generateEntities(testEntityCount);
        List<RefactoredTaskEntity> expectedEntities = taskUpdates.saveOrUpdateAll(unsavedEntities);
        for (RefactoredTaskEntity expectedEntity : expectedEntities) {
            expectedEntity.setDeadline(expectedEntity.getDeadline().plusDays(2));
            expectedEntity.setPriority(expectedEntity.getPriority() % 2);
            expectedEntity.setTaskDef(expectedEntity.getTaskDef() + " upadated");
            expectedEntity.setCompleted(!expectedEntity.isCompleted());
        }

        List<RefactoredTaskEntity> updatedEntities = taskUpdates.saveOrUpdateAll(expectedEntities);
        List<RefactoredTaskEntity> loadedEntities = taskQueries.findAll();

        for (int i = 0; i < testEntityCount; i++) {
            assertEquals(expectedEntities.get(i), updatedEntities.get(i));
            assertEquals(expectedEntities.get(i), loadedEntities.get(i));
        }
    }

    private List<RefactoredTaskEntity> saveEntitiesOneByOne(Collection<RefactoredTaskEntity> entitiesToSave)
            throws TaskPersistenceException {
        List<RefactoredTaskEntity> savedEntities = new ArrayList<>(entitiesToSave.size());
        for (RefactoredTaskEntity entity : entitiesToSave) {
            RefactoredTaskEntity savedEntity = taskUpdates.saveOrUpdate(entity);
            savedEntities.add(savedEntity);
        }

        return savedEntities;
    }

    @Test
    public void removeTest() throws TaskPersistenceException, TaskRemovalException {
        final int testEntityCount = 3;
        List<RefactoredTaskEntity> unsavedEntities = TaskTestDataGenerator.generateEntities(testEntityCount);
        List<RefactoredTaskEntity> savedEntities = taskUpdates.saveOrUpdateAll(unsavedEntities);

        while (!savedEntities.isEmpty()) {
            RefactoredTaskEntity entityToRemove = savedEntities.remove(0);
            taskUpdates.remove(entityToRemove.getId());

            int expectedSavedEntityCount = savedEntities.size();
            int actualSavedEntityCount = taskQueries.getRowCount();
            assertEquals(expectedSavedEntityCount, actualSavedEntityCount);
        }
    }

    @Test
    public void removeAllTest() throws TaskPersistenceException, TaskRemovalException {
        final int testEntityCount = 3;
        List<RefactoredTaskEntity> unsavedEntities = TaskTestDataGenerator.generateEntities(testEntityCount);
        List<RefactoredTaskEntity> savedEntities = taskUpdates.saveOrUpdateAll(unsavedEntities);

        List<Integer> removeTaskIds = savedEntities.stream()
                .map(RefactoredTaskEntity::getId)
                .collect(Collectors.toList());
        taskUpdates.removeAll(removeTaskIds);

        final int expectedTaskCount = 0;
        final int actualTaskCount = taskQueries.getRowCount();
        assertEquals(expectedTaskCount, actualTaskCount);
    }

}
