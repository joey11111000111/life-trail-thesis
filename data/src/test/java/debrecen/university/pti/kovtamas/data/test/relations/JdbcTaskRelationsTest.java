package debrecen.university.pti.kovtamas.data.test.relations;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskRelationEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.relations.JdbcTaskRelationsRepository;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRelationPersistenceException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRelationsRepository;
import debrecen.university.pti.kovtamas.data.test.util.JdbcTestUtils;
import java.util.ArrayList;
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
public class JdbcTaskRelationsTest {

    private final TaskRelationsRepository relationsRepo;

    public JdbcTaskRelationsTest() {
        relationsRepo = JdbcTaskRelationsRepository.getInstance();
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
        relationsRepo.clearTable();
    }

    @Test
    public void findAllOnEmptyTableShouldReturnEmptyList() {
        List<TaskRelationEntity> actualList = relationsRepo.findAll();
        final int expectedListSize = 0;
        final int actualListSize = actualList.size();
        assertEquals(expectedListSize, actualListSize);
    }

    @Test
    public void findAllWhereParentOrChildIdTest() throws TaskRelationPersistenceException {
        List<TaskRelationEntity> testEntities = TaskRelationTestDataGenerator.generateEntitiesWhereParentIsChildToo();
        relationsRepo.saveAll(testEntities);

        final int parentAndChildTaskId = 1;
        List<TaskRelationEntity> expectedEntities = testEntities.stream()
                .filter(e -> e.getChildId() == parentAndChildTaskId || e.getParentId() == parentAndChildTaskId)
                .collect(Collectors.toList());
        List<TaskRelationEntity> actualEntities = relationsRepo.findAllWhereParentOrChildId(parentAndChildTaskId);

        orderedListEqualsIgnoreId(expectedEntities, actualEntities);
    }

    @Test
    public void saveShouldReturnNewEntityWithExistingId() throws TaskRelationPersistenceException {
        final int testEntityCount = 5;
        List<TaskRelationEntity> unsavedEntities = TaskRelationTestDataGenerator.generateTestEntities(testEntityCount);
        List<TaskRelationEntity> savedEntities = saveEntitiesOneByOne(unsavedEntities);
        orderedListEqualsIgnoreId(unsavedEntities, savedEntities);
    }

    private List<TaskRelationEntity> saveEntitiesOneByOne(List<TaskRelationEntity> entities) throws TaskRelationPersistenceException {
        List<TaskRelationEntity> savedEntities = new ArrayList<>(entities.size());

        for (TaskRelationEntity entity : entities) {
            TaskRelationEntity savedEntity = relationsRepo.save(entity);
            savedEntities.add(savedEntity);
        }

        return savedEntities;
    }

    @Test
    public void saveAndFindAllTest() throws TaskRelationPersistenceException {
        final int testEntityCount = 5;
        List<TaskRelationEntity> unsavedEntities = TaskRelationTestDataGenerator.generateTestEntities(testEntityCount);
        List<TaskRelationEntity> savedEntities = saveEntitiesOneByOne(unsavedEntities);
        List<TaskRelationEntity> loadedEntities = relationsRepo.findAll();
        orderedListEqualsIgnoreId(savedEntities, loadedEntities);
    }

    @Test(expected = TaskRelationPersistenceException.class)
    public void saveCircularRelationsShouldThrowException() throws TaskRelationPersistenceException {
        List<TaskRelationEntity> circularDependencyEntities = TaskRelationTestDataGenerator.generateCircularDependencyEntities();
        for (TaskRelationEntity entity : circularDependencyEntities) {
            relationsRepo.save(entity);
        }
    }

    @Test(expected = TaskRelationPersistenceException.class)
    public void childIdShouldBeUniqueTest() throws TaskRelationPersistenceException {
        List<TaskRelationEntity> duplicatedChildIdEntities = TaskRelationTestDataGenerator.generateDuplicatedChildIdEntities();
        for (TaskRelationEntity entity : duplicatedChildIdEntities) {
            relationsRepo.save(entity);
        }
    }

    @Test
    public void saveAllShouldReturnNewEntitiesInOrderWithExistingIds() throws TaskRelationPersistenceException {
        final int testEntityCount = 6;
        List<TaskRelationEntity> unsavedEntities = TaskRelationTestDataGenerator.generateTestEntities(testEntityCount);
        List<TaskRelationEntity> savedEntities = relationsRepo.saveAll(unsavedEntities);

        for (TaskRelationEntity entity : savedEntities) {
            assertTrue(entity.hasId());
        }
    }

    @Test
    public void saveAllTest() throws TaskRelationPersistenceException {
        final int testEntityCount = 6;
        List<TaskRelationEntity> unsavedEntities = TaskRelationTestDataGenerator.generateTestEntities(testEntityCount);
        List<TaskRelationEntity> savedEntities = relationsRepo.saveAll(unsavedEntities);
        List<TaskRelationEntity> loadedEntities = relationsRepo.findAll();

        orderedListEqualsIgnoreId(unsavedEntities, loadedEntities);
        for (int i = 0; i < testEntityCount; i++) {
            assertEquals(savedEntities.get(i), loadedEntities.get(i));
        }
    }

    @Test
    public void deleteRelationTest() throws TaskRelationPersistenceException {
        TaskRelationEntity testEntity = TaskRelationTestDataGenerator.generateOneEntity();
        TaskRelationEntity savedEntity = relationsRepo.save(testEntity);
        relationsRepo.removeRelation(savedEntity.getId());

        List<TaskRelationEntity> loadedEntities = relationsRepo.findAll();
        final int savedEntityCount = loadedEntities.size();
        final int expectedEntityCount = 0;

        assertEquals(expectedEntityCount, savedEntityCount);
    }

    @Test
    public void removeAllWhereParentOrChildIdIsTest() throws TaskRelationPersistenceException {
        // Prepare test data
        List<TaskRelationEntity> unsavedEntities = TaskRelationTestDataGenerator.generateEntitiesWhereParentIsChildToo();
        relationsRepo.saveAll(unsavedEntities);
        final int parentAndChildTaskId = 1;
        relationsRepo.removeAllWhereParentOrChildIdIs(parentAndChildTaskId);

        List<TaskRelationEntity> expectedEntities = unsavedEntities.stream()
                .filter(entity -> entity.getChildId() != parentAndChildTaskId && entity.getParentId() != parentAndChildTaskId)
                .collect(Collectors.toList());
        List<TaskRelationEntity> actualEntities = relationsRepo.findAll();

        orderedListEqualsIgnoreId(expectedEntities, actualEntities);
    }

    private void orderedListEqualsIgnoreId(List<TaskRelationEntity> leftEntities, List<TaskRelationEntity> rightEntities) {
        assertEquals(leftEntities.size(), rightEntities.size());
        for (int i = 0; i < leftEntities.size(); i++) {
            TaskRelationEntity leftEntity = leftEntities.get(i);
            TaskRelationEntity rightEntity = rightEntities.get(i);
            entityEqualsIgnoreId(leftEntity, rightEntity);
        }
    }

    private boolean entityEqualsIgnoreId(TaskRelationEntity leftEntity, TaskRelationEntity rightEntity) {
        return leftEntity.getParentId() == rightEntity.getParentId()
                && leftEntity.getChildId() == rightEntity.getChildId();
    }

}
