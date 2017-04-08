package debrecen.university.pti.kovtamas.data.test.refactored.category;

import debrecen.university.pti.kovtamas.data.entity.todo.CategoryEntity;
import debrecen.university.pti.kovtamas.data.test.refactored.util.JdbcTestUtils;
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
        JdbcTestUtils.switchToTestTables();
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

    @Test
    public void findAllShouldReturnEveryCategoryInSaveOrder() throws CategorySaveFailureException {
        final int expectedSize = 3;
        List<CategoryEntity> expected = CategoryTestDataGenerator.generateUnsavedEntities(expectedSize);
        saveAllInOrder(expected);

        List<CategoryEntity> actual = categoryRepo.findAll();
        assertEquals(expectedSize, actual.size());

        for (int i = 0; i < expectedSize; i++) {
            final String expectedName = expected.get(i).getName();
            final String actualName = actual.get(i).getName();
            assertEquals(expectedName, actualName);
        }
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
        List<CategoryEntity> unsavedEntities = CategoryTestDataGenerator.generateUnsavedEntities(testEntityCount);
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
        List<CategoryEntity> unsavedEntities = CategoryTestDataGenerator.generateUnsavedEntities(testEntityCount);
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
        List<CategoryEntity> unsavedEntities = CategoryTestDataGenerator.generateUnsavedEntities(testEntityCount);
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
