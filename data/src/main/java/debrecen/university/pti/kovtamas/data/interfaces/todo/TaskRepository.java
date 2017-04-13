package debrecen.university.pti.kovtamas.data.interfaces.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.CategoryEntity;
import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRelationPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRemovalException;
import debrecen.university.pti.kovtamas.general.util.TreeNode;
import java.time.LocalDate;
import java.util.List;

public interface TaskRepository {

    // Queries
    List<TreeNode<TaskEntity>> findTodayAndUnfinishedPastTasks();

    List<TreeNode<TaskEntity>> findActiveByCategory(CategoryEntity category);

    List<TreeNode<TaskEntity>> findUncategorized();

    List<TreeNode<TaskEntity>> findCompleted();

    List<TreeNode<TaskEntity>> findActiveTasksBetween(LocalDate since, LocalDate until);

    // Updates
    TreeNode<TaskEntity> saveOrUpdate(TreeNode<TaskEntity> taskTree) throws TaskPersistenceException, TaskRelationPersistenceException;

    List<TreeNode<TaskEntity>> saveOrUpdateAll(List<TreeNode<TaskEntity>> taskTrees) throws TaskPersistenceException, TaskRelationPersistenceException;

    void remove(TreeNode<TaskEntity> taskTree) throws TaskRemovalException;

    void removeAll(List<TreeNode<TaskEntity>> taskTrees) throws TaskRemovalException;

    void clear();

}
