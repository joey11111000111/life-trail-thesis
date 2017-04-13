package debrecen.university.pti.kovtamas.data.test.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntityTreeBuilder;
import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntityTreeBuilder.TaskRelations;
import debrecen.university.pti.kovtamas.data.entity.todo.TaskRelationEntity;
import debrecen.university.pti.kovtamas.general.util.TreeNode;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TaskEntityTreeBuilderTest {

    @Test
    public void buildTaskTreeWithNoRelationsTest() {
        TaskRelationsWithTrees testData = TreeBuilderTestDataGenerator.notRelatedEntiities();
        testBuildWithGivenTestData(testData);
    }

    @Test
    public void buildTaskTreeWhenAllTasksAreOneTreeTest() {
        TaskRelationsWithTrees testData = TreeBuilderTestDataGenerator.allEntitiesAreInOneTree();
        testBuildWithGivenTestData(testData);
    }

    @Test
    public void buildTaskTreeWithIndependentTreesTest() {
        TaskRelationsWithTrees testData = TreeBuilderTestDataGenerator.tasksAreOnMultipleTrees();
        List<TreeNode<TaskEntity>> trees = testData.getTrees();
        testBuildWithGivenTestData(testData);

    }

    private void testBuildWithGivenTestData(TaskRelationsWithTrees testData) {
        List<TreeNode<TaskEntity>> expectedTrees = testData.getTrees();

        TaskRelations taskRelations = new TaskRelations(testData.getTasks(), testData.getRelations());
        List<TreeNode<TaskEntity>> actualTrees = TaskEntityTreeBuilder.buildTaskTrees(taskRelations);

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

    @Test
    public void collapseTaskTreeWithNoRelationsTest() {
        TaskRelationsWithTrees testData = TreeBuilderTestDataGenerator.notRelatedEntiities();
        testCollapseWithGivenTestData(testData);
    }

    @Test
    public void collapseTaskTreeWhenAllTasksAreOneTreeTest() {
        TaskRelationsWithTrees testData = TreeBuilderTestDataGenerator.allEntitiesAreInOneTree();
        testCollapseWithGivenTestData(testData);
    }

    @Test
    public void collapseTaskTreeWithIndependentTreesTest() {
        TaskRelationsWithTrees testData = TreeBuilderTestDataGenerator.tasksAreOnMultipleTrees();
        testCollapseWithGivenTestData(testData);
    }

    private void testCollapseWithGivenTestData(TaskRelationsWithTrees testData) {
        List<TaskEntity> tasks = testData.getTasks();
        List<TaskRelationEntity> relations = testData.getRelations();
        List<TreeNode<TaskEntity>> trees = testData.getTrees();

        TaskRelations expectedTaskRelations = new TaskRelations(tasks, relations);
        TaskRelations actualTaskRelations = TaskEntityTreeBuilder.collapseTaskTrees(trees);

        collectionEqualsIgnoreOrder(expectedTaskRelations.getTasks(), actualTaskRelations.getTasks());
        collectionEqualsIgnoreOrder(expectedTaskRelations.getRelations(), actualTaskRelations.getRelations());
    }

    private <T> void collectionEqualsIgnoreOrder(Collection<T> expectedElements, Collection<T> actualElements) {
        assertEquals(expectedElements.size(), actualElements.size());
        Set<T> expectedSet = new HashSet<>(expectedElements);
        Set<T> actualSet = new HashSet<>(actualElements);
        assertEquals(expectedSet, actualSet);
    }

}
