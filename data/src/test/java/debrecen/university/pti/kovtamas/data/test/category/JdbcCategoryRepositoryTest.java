package debrecen.university.pti.kovtamas.data.test.category;

import debrecen.university.pti.kovtamas.data.entity.todo.CategoryEntity;
import debrecen.university.pti.kovtamas.data.test.util.JdbcTestUtils;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.category.JdbcCategoryRepository;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.CategoryNotFoundException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.CategorySaveFailureException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.CategoryRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class JdbcCategoryRepositoryTest {

    private final CategoryRepository categoryRepo;

    @BeforeClass
    static public void switchToTestTables() {
        JdbcTestUtils.switchToTestTables(JdbcTestUtils.TestType.UNIT);
    }

    @AfterClass
    static public void switchToProductionTables() {
        JdbcTestUtils.switchToProductionTables();
    }

    @Before
    public void clearTable() {
        categoryRepo.clearTable();
    }

    public JdbcCategoryRepositoryTest() {
        categoryRepo = JdbcCategoryRepository.getInstance();
    }

    @Test
    public void findAllOnEmptyTableShouldReturnEmptyListTest() {
        List<CategoryEntity> expected = Collections.EMPTY_LIST;
        List<CategoryEntity> actual = categoryRepo.findAll();

        assertEquals(expected.size(), actual.size());
    }

    @Test
    public void findAllShouldReturnEntitiesInAscDisplayOrder() throws CategorySaveFailureException {
        final int testEntityCount = 5;
        List<CategoryEntity> wrongOrderedEntities
                = CategoryTestDataGenerator.generateUnsavedEntitiesInDescDisplayOrder(testEntityCount);
        List<CategoryEntity> expectedOrderedEntities
                = CategoryTestDataGenerator.generateUnsavedEntitiesInAscDisplayOrder(testEntityCount);

        saveAllInOrder(wrongOrderedEntities);
        List<CategoryEntity> actualOrderedEntities = categoryRepo.findAll();

        for (int i = 0; i < testEntityCount; i++) {
            int expectedDisplayIndex = expectedOrderedEntities.get(i).getDisplayIndex();
            int actualDisplayIndex = actualOrderedEntities.get(i).getDisplayIndex();
            assertEquals(expectedDisplayIndex, actualDisplayIndex);
        }
    }

    @Test(expected = CategorySaveFailureException.class)
    public void insertExistingCategoryShouldThrowException() throws CategorySaveFailureException {
        CategoryEntity categoryEntity = CategoryTestDataGenerator.generateOneUnsavedEntity();
        categoryRepo.saveOrUpdate(categoryEntity);
        categoryRepo.saveOrUpdate(categoryEntity);
    }

    @Test(expected = NullPointerException.class)
    public void insertNullShouldThrowNullPointerException() throws CategorySaveFailureException {
        categoryRepo.saveOrUpdate(null);
    }

    @Test
    public void saveShouldReturnEntityWithExistingId() throws CategorySaveFailureException {
        CategoryEntity unsavedEntity = CategoryTestDataGenerator.generateOneUnsavedEntity();
        CategoryEntity savedEntity = categoryRepo.saveOrUpdate(unsavedEntity);

        assertTrue(savedEntity.hasId());
        assertEquals(unsavedEntity.getName(), savedEntity.getName());
    }

    @Test
    public void saveShouldReturnDetachedEntity() throws CategorySaveFailureException {
        CategoryEntity unsavedEntity = CategoryTestDataGenerator.generateOneUnsavedEntity();
        CategoryEntity savedEntity = categoryRepo.saveOrUpdate(unsavedEntity);

        final String modifiedName = "Changed category name";
        final String savedCategoryName = savedEntity.getName();

        unsavedEntity.setName(modifiedName);
        assertFalse(modifiedName.equals(savedCategoryName));
    }

    private List<CategoryEntity> saveAllInOrder(List<CategoryEntity> unsavedEntities) throws CategorySaveFailureException {
        List<CategoryEntity> savedEntities = new ArrayList<>(unsavedEntities.size());
        for (CategoryEntity entity : unsavedEntities) {
            CategoryEntity savedEntity = categoryRepo.saveOrUpdate(entity);
            savedEntities.add(savedEntity);
        }

        return savedEntities;
    }

    @Test(expected = CategoryNotFoundException.class)
    public void findByIdShouldThrowExceptionWhenIdIsNotPresentInTable() throws CategoryNotFoundException {
        categoryRepo.findById(0);
    }

    @Test
    public void findByIdTest() throws CategorySaveFailureException, CategoryNotFoundException {
        final int testEntityCount = 3;
        List<CategoryEntity> unsavedEntities = CategoryTestDataGenerator.generateUnsavedEntitiesInAscDisplayOrder(testEntityCount);
        List<CategoryEntity> savedEntities = saveAllInOrder(unsavedEntities);

        for (CategoryEntity savedEntity : savedEntities) {
            CategoryEntity loadedEntity = categoryRepo.findById(savedEntity.getId());
            assertEquals(savedEntity, loadedEntity);
        }
    }

    @Test(expected = CategoryNotFoundException.class)
    public void idOfShouldThrowExceptionWhenIdIsNotPresentInTable() throws CategoryNotFoundException {
        categoryRepo.idOf("not existing category");
    }

    @Test
    public void idOfTest() throws CategorySaveFailureException, CategoryNotFoundException {
        final int testEntityCount = 3;
        List<CategoryEntity> unsavedEntities = CategoryTestDataGenerator.generateUnsavedEntitiesInAscDisplayOrder(testEntityCount);
        List<CategoryEntity> savedEntities = saveAllInOrder(unsavedEntities);

        for (CategoryEntity entity : savedEntities) {
            Integer loadedId = categoryRepo.idOf(entity.getName());
            assertEquals(entity.getId(), loadedId);
        }
    }

    @Test(expected = NullPointerException.class)
    public void idOfShouldNotAcceptNullParameter() throws CategoryNotFoundException {
        categoryRepo.idOf(null);
    }

    @Test
    public void removeByIdTest() throws CategorySaveFailureException {
        removeWithGivenMethodTest(categoryEntity -> {
            categoryRepo.remove(categoryEntity.getId());
        });
    }

    @Test
    public void removeByNameTest() throws CategorySaveFailureException {
        removeWithGivenMethodTest(categoryEntity -> {
            categoryRepo.remove(categoryEntity.getName());
        });
    }

    private void removeWithGivenMethodTest(Consumer<CategoryEntity> removeMethod) throws CategorySaveFailureException {
        final int testEntityCount = 3;
        List<CategoryEntity> unsavedEntities = CategoryTestDataGenerator.generateUnsavedEntitiesInAscDisplayOrder(testEntityCount);
        List<CategoryEntity> savedEntities = saveAllInOrder(unsavedEntities);

        while (!savedEntities.isEmpty()) {
            CategoryEntity entityToRemove = savedEntities.remove(0);
            removeMethod.accept(entityToRemove);

            List<CategoryEntity> allRemainingEntities = categoryRepo.findAll();
            collectionEquals(savedEntities, allRemainingEntities);
        }
    }

    private void collectionEquals(Collection<CategoryEntity> collection, Collection<CategoryEntity> otherCollection) {
        assertEquals(collection.size(), otherCollection.size());

        List<CategoryEntity> list = new ArrayList<>(collection);
        List<CategoryEntity> otherList = new ArrayList<>(otherCollection);
        Comparator<CategoryEntity> cmp = (e1, e2) -> e1.getId() - e2.getId();

        Collections.sort(list, cmp);
        Collections.sort(otherList, cmp);

        for (int i = 0; i < list.size(); i++) {
            assertEquals(list.get(i), otherList.get(i));
        }
    }

}
