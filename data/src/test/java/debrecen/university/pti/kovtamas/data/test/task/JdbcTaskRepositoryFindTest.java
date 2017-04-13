package debrecen.university.pti.kovtamas.data.test.task;

import debrecen.university.pti.kovtamas.data.entity.todo.CategoryEntity;
import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.category.JdbcCategoryRepository;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.relations.JdbcTaskRelationsRepository;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.task.JdbcTaskRepository;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.task.JdbcTaskRepositoryQueries;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.task.JdbcTaskRepositoryUpdates;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.CategorySaveFailureException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRelationPersistenceException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepository;
import debrecen.university.pti.kovtamas.data.test.todo.TaskRelationsWithTrees;
import debrecen.university.pti.kovtamas.data.test.util.JdbcTestUtils;
import debrecen.university.pti.kovtamas.general.util.TreeNode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JdbcTaskRepositoryFindTest {

    static private List<CategoryEntity> allCategories;
    static private JdbcCategoryRepository categoryRepo;

    private final TaskRepository repo;
    private final JdbcTaskRepositoryUpdates taskUpdates;
    private final JdbcTaskRepositoryQueries taskQueries;
    private final JdbcTaskRelationsRepository taskRelations;

    @BeforeClass
    static public void switchToTestTablesAndSetupCategories() throws CategorySaveFailureException {
        JdbcTestUtils.switchToTestTables(JdbcTestUtils.TestType.INTEGRATION);
        setupCategories();
    }

    static private void setupCategories() throws CategorySaveFailureException {
        allCategories = new ArrayList<>();
        categoryRepo = JdbcCategoryRepository.getInstance();

        allCategories.add(new CategoryEntity("Personal", 1));
        allCategories.add(new CategoryEntity("Work", 2));
        allCategories.add(new CategoryEntity("Fitness", 3));
        allCategories.add(new CategoryEntity("Family", 4));

        List<CategoryEntity> savedCategories = new ArrayList<>();
        for (CategoryEntity unsavedEntity : allCategories) {
            CategoryEntity savedEntity = categoryRepo.saveOrUpdate(unsavedEntity);
            savedCategories.add(savedEntity);
        }

        allCategories = savedCategories;
    }

    @AfterClass
    static public void switchToProductionTables() {
        JdbcTestUtils.switchToProductionTables();
    }

    public JdbcTaskRepositoryFindTest() {
        repo = JdbcTaskRepository.getInstance();
        taskQueries = JdbcTaskRepositoryQueries.getInstance();
        taskUpdates = JdbcTaskRepositoryUpdates.getInstance();
        taskRelations = JdbcTaskRelationsRepository.getInstance();
    }

    @After
    public void clearTaskAndRelationsTables() {
        taskRelations.clearTable();
        taskUpdates.clearTable();
    }

    @Test
    public void findCompletedTest() throws TaskPersistenceException, TaskRelationPersistenceException {
        TaskRelationsWithTrees testData = TaskIntegrationTestDataGenerator.generateDataForFindCompletedTest();
        List<TreeNode<TaskEntity>> expectedTrees = testData.getTrees();
        List<TreeNode<TaskEntity>> actualTrees = repo.findCompleted();
        treeListEqualsOredered(expectedTrees, actualTrees);
    }

    @Test
    public void findUncategorized() throws TaskPersistenceException, TaskRelationPersistenceException {
        int categoryId = allCategories.get(0).getId();
        TaskRelationsWithTrees testData = TaskIntegrationTestDataGenerator.generateDataForFindUncategorizedTest(categoryId);

        List<TreeNode<TaskEntity>> expectedTrees = testData.getTrees();
        List<TreeNode<TaskEntity>> actualTrees = repo.findUncategorized();

        treeListEqualsOredered(expectedTrees, actualTrees);
    }

    @Test
    public void findActiveByCategoryTest() throws TaskPersistenceException, TaskRelationPersistenceException {
        int includeCategoryId = allCategories.get(0).getId();
        int badCategoryId = allCategories.get(2).getId();
        TaskRelationsWithTrees testData = TaskIntegrationTestDataGenerator
                .generateDataForActiveByCategoryTest(includeCategoryId, badCategoryId);

        List<TreeNode<TaskEntity>> expectedTrees = testData.getTrees();
        List<TreeNode<TaskEntity>> actualTrees = repo.findActiveByCategory(allCategories.get(0));

        treeListEqualsOredered(expectedTrees, actualTrees);
    }

    @Test
    public void findTodayTasksTest() throws TaskPersistenceException, TaskRelationPersistenceException {
        TaskRelationsWithTrees testData = TaskIntegrationTestDataGenerator.generateDataForTodayTasksTest();

        List<TreeNode<TaskEntity>> expectedTrees = testData.getTrees();
        List<TreeNode<TaskEntity>> actualTrees = repo.findTodayAndUnfinishedPastTasks();

        treeListEqualsOredered(expectedTrees, actualTrees);
    }

    @Test
    public void findActivePastTasksTest() throws TaskPersistenceException, TaskRelationPersistenceException {
        TaskRelationsWithTrees testData = TaskIntegrationTestDataGenerator.generateDataForActivePastTasksTest();

        List<TreeNode<TaskEntity>> expectedTrees = testData.getTrees();
        List<TreeNode<TaskEntity>> actualTrees = repo.findTodayAndUnfinishedPastTasks();

        treeListEqualsOredered(expectedTrees, actualTrees);
    }

    @Test
    public void findTasksBetweenTest() throws TaskPersistenceException, TaskRelationPersistenceException {
        LocalDate since = LocalDate.now().minusDays(10);
        LocalDate until = LocalDate.now().plusDays(10);
        TaskRelationsWithTrees testData = TaskIntegrationTestDataGenerator
                .generateDataForFindTasksBetweenTest(since, until);

        List<TreeNode<TaskEntity>> expectedTrees = testData.getTrees();
        List<TreeNode<TaskEntity>> actualTrees = repo.findActiveTasksBetween(since, until);
        treeListEqualsOredered(expectedTrees, actualTrees);
    }

    private void treeListEqualsOredered(List<TreeNode<TaskEntity>> expected, List<TreeNode<TaskEntity>> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            TreeNode<TaskEntity> expectedTree = expected.get(i);
            TreeNode<TaskEntity> actualTree = actual.get(i);
            assertEquals(expectedTree.getElement(), actualTree.getElement());

            List<TreeNode<TaskEntity>> expectedChildren = expectedTree.getChildren();
            List<TreeNode<TaskEntity>> actualChildren = actualTree.getChildren();
            treeListEqualsOredered(expectedChildren, actualChildren);
        }
    }

}
