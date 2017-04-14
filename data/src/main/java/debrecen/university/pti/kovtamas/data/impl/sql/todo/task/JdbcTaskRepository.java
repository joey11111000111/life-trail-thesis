package debrecen.university.pti.kovtamas.data.impl.sql.todo.task;

import debrecen.university.pti.kovtamas.data.entity.todo.CategoryEntity;
import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntityTreeBuilder;
import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntityTreeBuilder.TaskRelations;
import debrecen.university.pti.kovtamas.data.entity.todo.TaskRelationEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.relations.JdbcTaskRelationsRepository;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.RowModificationException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRelationPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRemovalException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepository;
import debrecen.university.pti.kovtamas.general.util.SimpleTreeNode;
import debrecen.university.pti.kovtamas.general.util.TreeNode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JdbcTaskRepository implements TaskRepository {

    static private final JdbcTaskRepository INSTANCE;

    private final JdbcTaskRepositoryQueries taskQueries;
    private final JdbcTaskRepositoryUpdates taskUpdates;
    private final JdbcTaskRelationsRepository relationsRepo;

    static {
        INSTANCE = new JdbcTaskRepository();
    }

    static public JdbcTaskRepository getInstance() {
        return INSTANCE;
    }

    private JdbcTaskRepository() {
        taskQueries = JdbcTaskRepositoryQueries.getInstance();
        taskUpdates = JdbcTaskRepositoryUpdates.getInstance();
        relationsRepo = JdbcTaskRelationsRepository.getInstance();
    }

    @Override
    public List<TreeNode<TaskEntity>> findTodayAndUnfinishedPastTasks() {
        List<TaskEntity> activeByCategoryTasks = taskQueries.findTodayAndUnfinishedPastTasks();
        return buildResultTreesFromEntities(activeByCategoryTasks);
    }

    @Override
    public List<TreeNode<TaskEntity>> findActiveByCategory(CategoryEntity category) {
        List<TaskEntity> activeByCategoryTasks = taskQueries.findActiveByCategoryId(category.getId());
        return buildResultTreesFromEntities(activeByCategoryTasks);
    }

    @Override
    public List<TreeNode<TaskEntity>> findUncategorized() {
        List<TaskEntity> uncategorizedTasks = taskQueries.findUncategorizedTasks();
        return buildResultTreesFromEntities(uncategorizedTasks);
    }

    @Override
    public List<TreeNode<TaskEntity>> findCompleted() {
        List<TaskEntity> completedTasks = taskQueries.findCompletedTasks();
        return buildResultTreesFromEntities(completedTasks);
    }

    private List<TreeNode<TaskEntity>> buildResultTreesFromEntities(List<TaskEntity> entities) {
        List<TaskEntity> completedSubTasks = findCompletedSubtasksIfPresent(entities);
        entities.addAll(completedSubTasks);

        List<TaskRelationEntity> relations = findRelationsForTasks(entities);
        TaskRelations taskRelations = new TaskRelations(entities, relations);
        return TaskEntityTreeBuilder.buildTaskTrees(taskRelations);
    }

    private List<TaskRelationEntity> findRelationsForTasks(Collection<TaskEntity> tasks) {
        List<Integer> allTaskIds = tasks.stream()
                .map(TaskEntity::getId)
                .collect(Collectors.toList());

        Set<TaskRelationEntity> allRelations = new HashSet<>();
        allTaskIds.forEach(currentTaskId -> {
            List<TaskRelationEntity> currentTaskRelations = relationsRepo.findAllWhereParentOrChildId(currentTaskId);
            allRelations.addAll(currentTaskRelations);
        });

        return new ArrayList<>(allRelations);
    }

    @Override
    public List<TreeNode<TaskEntity>> findActiveTasksBetween(LocalDate since, LocalDate until) {
        List<TaskEntity> tasks = taskQueries.findActiveTasksBetween(since, until);
        return buildResultTreesFromEntities(tasks);
    }

    private List<TaskEntity> findCompletedSubtasksIfPresent(List<TaskEntity> tasks) {
        List<TaskRelationEntity> relations = findRelationsForTasks(tasks);
        List<TaskEntity> completedSubTasks = new ArrayList<>();
        for (TaskRelationEntity relation : relations) {
            int childId = relation.getChildId();
            boolean isTaskMissing = !containsTaskWithId(tasks, childId);
            if (isTaskMissing) {
                try {
                    TaskEntity missingTask = taskQueries.findById(childId);
                    completedSubTasks.add(missingTask);
                } catch (TaskNotFoundException tnfe) {
                    log.warn("Failed to load missing task with id: " + childId, tnfe);
                }
            }
        }

        if (!completedSubTasks.isEmpty()) {
            List<TaskEntity> otherMissingTasks = findCompletedSubtasksIfPresent(completedSubTasks);
            completedSubTasks.addAll(otherMissingTasks);
        }

        return completedSubTasks;
    }

    private boolean containsTaskWithId(List<TaskEntity> tasks, int id) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findAny()
                .isPresent();
    }

    @Override
    public TreeNode<TaskEntity> saveOrUpdate(TreeNode<TaskEntity> taskTree)
            throws TaskPersistenceException {
        TreeNode<TaskEntity> savedTree = saveOrUpdateTaskTree(taskTree);

        TaskRelations treeRelations = TaskEntityTreeBuilder.collapseTaskTrees(Arrays.asList(savedTree));
        List<TaskRelationEntity> relations = treeRelations.getRelations();
        for (TaskRelationEntity relation : relations) {
            try {
                relationsRepo.save(relation);
            } catch (TaskRelationPersistenceException trpe) {
                // Ignore because in case of updateting a task tree, already
                // existing relations will be sent to the save method too,
                // which causes this exception. Not saving them again is the normal behavour.
            }
        }

        return savedTree;
    }

    private TreeNode<TaskEntity> saveOrUpdateTaskTree(TreeNode<TaskEntity> tree) throws TaskPersistenceException {
        TaskEntity currentRootElement = taskUpdates.saveOrUpdate(tree.getElement());
        TreeNode<TaskEntity> currentRootNode = new SimpleTreeNode<>(currentRootElement);

        if (tree.hasChildren()) {
            for (TreeNode<TaskEntity> childNode : tree.getChildren()) {
                TreeNode<TaskEntity> savedOrUpdatedChildNode = saveOrUpdateTaskTree(childNode);
                savedOrUpdatedChildNode.setParent(currentRootNode);
                currentRootNode.addChild(savedOrUpdatedChildNode);
            }
        }

        return currentRootNode;
    }

    @Override
    public List<TreeNode<TaskEntity>> saveOrUpdateAll(List<TreeNode<TaskEntity>> taskTrees) throws TaskPersistenceException {
        List<TreeNode<TaskEntity>> savedTrees = new ArrayList<>(taskTrees.size());
        for (TreeNode<TaskEntity> currentTree : taskTrees) {
            TreeNode<TaskEntity> savedCurrentTree = saveOrUpdateTaskTree(currentTree);
            savedTrees.add(savedCurrentTree);
        }

        return savedTrees;
    }

    @Override
    public void remove(TreeNode<TaskEntity> taskTree) throws TaskRemovalException {
        TaskRelations treeRelations = TaskEntityTreeBuilder.collapseTaskTrees(Arrays.asList(taskTree));
        List<TaskEntity> tasks = treeRelations.getTasks();
        List<Integer> taskIds = tasks.stream()
                .map(TaskEntity::getId)
                .collect(Collectors.toList());

        taskIds.forEach(id -> relationsRepo.removeAllWhereParentOrChildIdIs(id));
        taskUpdates.removeAll(taskIds);
    }

    @Override
    public void removeAll(List<TreeNode<TaskEntity>> taskTrees) throws TaskRemovalException {
        for (TreeNode<TaskEntity> currentTree : taskTrees) {
            remove(currentTree);
        }
    }

    @Override
    public void setCategoryIdToNullWhere(int categoryId) throws RowModificationException {
        taskUpdates.setCategoryIdToNullWhere(categoryId);
    }

    @Override
    public void clear() {
        taskUpdates.clearTable();
        relationsRepo.clearTable();
    }

}
