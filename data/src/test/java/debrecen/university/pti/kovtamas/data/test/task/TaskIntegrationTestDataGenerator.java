package debrecen.university.pti.kovtamas.data.test.task;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.entity.todo.TaskRelationEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.relations.JdbcTaskRelationsRepository;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.task.JdbcTaskRepositoryUpdates;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRelationPersistenceException;
import debrecen.university.pti.kovtamas.data.test.todo.TaskRelationsWithTrees;
import debrecen.university.pti.kovtamas.general.util.SimpleTreeNode;
import debrecen.university.pti.kovtamas.general.util.TreeNode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public final class TaskIntegrationTestDataGenerator {

    static private final JdbcTaskRepositoryUpdates taskUpdates;
    static private final JdbcTaskRelationsRepository relationsRepo;

    static {
        taskUpdates = JdbcTaskRepositoryUpdates.getInstance();
        relationsRepo = JdbcTaskRelationsRepository.getInstance();
    }

    static public TaskRelationsWithTrees generateDataForFindCompletedTest() throws TaskPersistenceException, TaskRelationPersistenceException {
        TaskEntity excludedPrototype = TaskEntity.builder()
                .categoryId(null)
                .priority(1)
                .deadline(LocalDate.now())
                .taskDef("")
                .completed(false)
                .build();
        TaskEntity includedPrototype = TaskEntity.builder()
                .categoryId(null)
                .priority(1)
                .deadline(LocalDate.now())
                .taskDef("")
                .completed(true)
                .build();

        return generateAndSaveTestData(excludedPrototype, includedPrototype);
    }

    static public TaskRelationsWithTrees generateDataForFindUncategorizedTest(int existingCategoryId)
            throws TaskPersistenceException, TaskRelationPersistenceException {
        TaskEntity excludedPrototype = TaskEntity.builder()
                .categoryId(existingCategoryId)
                .priority(1)
                .deadline(LocalDate.now())
                .taskDef("")
                .completed(true)
                .build();
        TaskEntity includedPrototype = TaskEntity.builder()
                .categoryId(null)
                .priority(1)
                .deadline(LocalDate.now())
                .taskDef("")
                .completed(false)
                .build();

        return generateAndSaveTestData(excludedPrototype, includedPrototype);
    }

    static public TaskRelationsWithTrees generateDataForActiveByCategoryTest(int includeCategoryId, int badCategoryId)
            throws TaskPersistenceException, TaskRelationPersistenceException {
        TaskEntity excludedPrototype = TaskEntity.builder()
                .categoryId(badCategoryId)
                .priority(1)
                .deadline(LocalDate.now())
                .taskDef("")
                .completed(true)
                .build();
        TaskEntity includedPrototype = TaskEntity.builder()
                .categoryId(includeCategoryId)
                .priority(1)
                .deadline(LocalDate.now())
                .taskDef("")
                .completed(false)
                .build();

        return generateAndSaveTestData(excludedPrototype, includedPrototype);
    }

    static public TaskRelationsWithTrees generateDataForTodayTasksTest()
            throws TaskPersistenceException, TaskRelationPersistenceException {
        TaskEntity excludedPrototype = TaskEntity.builder()
                .categoryId(null)
                .priority(1)
                .deadline(LocalDate.now().minusDays(2))
                .taskDef("")
                .completed(true)
                .build();
        TaskEntity includedPrototype = TaskEntity.builder()
                .categoryId(null)
                .priority(2)
                .deadline(LocalDate.now())
                .taskDef("")
                .completed(true)
                .build();

        return generateAndSaveTestData(excludedPrototype, includedPrototype);
    }

    static public TaskRelationsWithTrees generateDataForActivePastTasksTest()
            throws TaskPersistenceException, TaskRelationPersistenceException {
        TaskEntity excludedPrototype = TaskEntity.builder()
                .categoryId(null)
                .priority(1)
                .deadline(LocalDate.now().minusDays(2))
                .taskDef("")
                .completed(true)
                .build();
        TaskEntity includedPrototype = TaskEntity.builder()
                .categoryId(null)
                .priority(2)
                .deadline(LocalDate.now().minusDays(2))
                .taskDef("")
                .completed(false)
                .build();

        return generateAndSaveTestData(excludedPrototype, includedPrototype);
    }

    static public TaskRelationsWithTrees generateDataForFindTasksBetweenTest(LocalDate since, LocalDate until)
            throws TaskPersistenceException, TaskRelationPersistenceException {
        TaskEntity tooSoonTask = buildEntityWithDeadline(since.minusDays(1));
        TaskEntity tooLateTask = buildEntityWithDeadline(until.plusDays(1));
        TaskEntity sinceTask = buildEntityWithDeadline(since);
        TaskEntity untilTask = buildEntityWithDeadline(until);

        TaskRelationsWithTrees result1 = generateAndSaveTestData(tooSoonTask, sinceTask);
        TaskRelationsWithTrees result2 = generateAndSaveTestData(tooLateTask, untilTask);

        return mergeInOrder(result1, result2);
    }

    static private TaskRelationsWithTrees mergeInOrder(TaskRelationsWithTrees res1,
            TaskRelationsWithTrees res2) {

        List<TaskEntity> tasks = new ArrayList<>(res1.getTasks());
        List<TaskRelationEntity> relations = new ArrayList<>(res1.getRelations());
        List<TreeNode<TaskEntity>> trees = new ArrayList<>(res1.getTrees());

        tasks.addAll(res2.getTasks());
        relations.addAll(res2.getRelations());
        trees.addAll(res2.getTrees());

        return new TaskRelationsWithTrees(tasks, relations, trees);
    }

    static private TaskEntity buildEntityWithDeadline(LocalDate taskDeadline) {
        return TaskEntity.builder()
                .categoryId(null)
                .priority(1)
                .deadline(taskDeadline)
                .taskDef("")
                .completed(false)
                .build();
    }

    static private TaskRelationsWithTrees generateAndSaveTestData(TaskEntity excludedPrototype, TaskEntity includedPrototype)
            throws TaskPersistenceException, TaskRelationPersistenceException {
        createAndSaveExcludedAsOneTree(4, excludedPrototype);
        List<TaskEntity> includedTasks = createFromPrototype(4, includedPrototype);
        return buildResult(includedTasks);
    }

    static private TaskRelationsWithTrees buildResult(List<TaskEntity> unsavedTasks)
            throws TaskPersistenceException, TaskRelationPersistenceException {
        List<TaskEntity> savedTasks = saveEntities(unsavedTasks);
        List<TreeNode<TaskEntity>> nodes = createNodesFromEntities(savedTasks);
        List<TaskRelationEntity> relations = createAndSaveRelations(nodes);

        List<TreeNode<TaskEntity>> trees = nodes.stream()
                .filter(entity -> !entity.hasParent())
                .collect(Collectors.toList());

        return new TaskRelationsWithTrees(savedTasks, relations, trees);
    }

    static private List<TaskRelationEntity> createAndSaveRelations(List<TreeNode<TaskEntity>> nodes) throws TaskRelationPersistenceException {
        LinkedList<TaskRelationEntity> relations = new LinkedList<>();

        for (int i = 0; i < nodes.size() - 1; i++) {
            setRelation(relations, nodes, i, i + 1);
            TaskRelationEntity newRelation = relations.getLast();
            relationsRepo.save(newRelation);
        }

        return relations;
    }

    static private List<TaskEntity> saveEntities(List<TaskEntity> entities) throws TaskPersistenceException {
        return taskUpdates.saveOrUpdateAll(entities);
    }

    static private List<TreeNode<TaskEntity>> createNodesFromEntities(List<TaskEntity> entities) {
        return entities.stream()
                .map(SimpleTreeNode<TaskEntity>::new)
                .collect(Collectors.toList());
    }

    static private void setRelation(List<TaskRelationEntity> relations, List<TreeNode<TaskEntity>> nodes,
            int parentIndex, int childIndex) {
        relations.add(TaskRelationEntity.builder()
                .parentId(nodes.get(parentIndex).getElement().getId())
                .childId(nodes.get(childIndex).getElement().getId())
                .build()
        );
        nodes.get(parentIndex).addChild(nodes.get(childIndex));
        nodes.get(childIndex).setParent(nodes.get(parentIndex));
    }

    static private void createAndSaveExcludedAsOneTree(int num, TaskEntity excludedPrototype)
            throws TaskPersistenceException, TaskRelationPersistenceException {

        List<TaskEntity> unsavedExcludedTasks = createFromPrototype(num, excludedPrototype);
        List<TaskEntity> savedExcludedTasks = saveEntities(unsavedExcludedTasks);

        for (int i = 0; i < num - 1; i++) {
            TaskRelationEntity excludedRelation = TaskRelationEntity.builder()
                    .parentId(savedExcludedTasks.get(i).getId())
                    .childId(savedExcludedTasks.get(i + 1).getId())
                    .build();
            relationsRepo.save(excludedRelation);
        }
    }

    static private List<TaskEntity> createFromPrototype(int num, TaskEntity prototype) {
        List<TaskEntity> excludedEntities = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            TaskEntity excludedEntity = TaskEntity.builder()
                    .categoryId(prototype.getCategoryId())
                    .taskDef("Test task " + i)
                    .priority(prototype.getPriority())
                    .deadline(prototype.getDeadline())
                    .completed(prototype.isCompleted())
                    .build();
            excludedEntities.add(excludedEntity);
        }

        return excludedEntities;
    }

    private TaskIntegrationTestDataGenerator() {
    }

}
