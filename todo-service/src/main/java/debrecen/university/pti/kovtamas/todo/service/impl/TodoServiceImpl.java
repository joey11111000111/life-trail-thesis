package debrecen.university.pti.kovtamas.todo.service.impl;

import debrecen.university.pti.kovtamas.data.entity.todo.CategoryEntity;
import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.category.JdbcCategoryRepository;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.task.JdbcTaskRepository;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.CategorySaveFailureException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.RowModificationException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRemovalException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.CategoryRepository;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepository;
import debrecen.university.pti.kovtamas.general.util.TreeNode;
import debrecen.university.pti.kovtamas.todo.service.api.TaskDeletionException;
import debrecen.university.pti.kovtamas.todo.service.api.TaskSaveFailureException;
import debrecen.university.pti.kovtamas.todo.service.api.TodoService;
import debrecen.university.pti.kovtamas.todo.service.mapper.CategoryEntityVoMapper;
import debrecen.university.pti.kovtamas.todo.service.mapper.MappingException;
import debrecen.university.pti.kovtamas.todo.service.mapper.TaskEntityVoMapper;
import debrecen.university.pti.kovtamas.todo.service.vo.CategoryVo;
import debrecen.university.pti.kovtamas.todo.service.vo.Priority;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TodoServiceImpl implements TodoService {

    private final TaskRepository taskRepo;
    private final CategoryRepository categoryRepo;

    public TodoServiceImpl() {
        taskRepo = JdbcTaskRepository.getInstance();
        categoryRepo = JdbcCategoryRepository.getInstance();
    }

    @Override
    public List<CategoryVo> getAllCategoriesInDisplayOrder() {
        List<CategoryEntity> allCategoriesOrdered = categoryRepo.findAll();
        return CategoryEntityVoMapper.allToVo(allCategoriesOrdered);
    }

    @Override
    public List<TaskVo> getActiveByCategory(CategoryVo category) {
        CategoryEntity categoryEntity = CategoryEntityVoMapper.toEntity(category);
        List<TreeNode<TaskEntity>> tasks = taskRepo.findActiveByCategory(categoryEntity);
        return mappedOrEmptyTaskList(tasks);
    }

    @Override
    public List<TaskVo> getTodayTasks() {
        List<TreeNode<TaskEntity>> tasks = taskRepo.findTodayAndUnfinishedPastTasks();
        return mappedOrEmptyTaskList(tasks);
    }

    @Override
    public List<TaskVo> getTomorrowTasks() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return getTasksOfFollowingDays(tomorrow, tomorrow);
    }

    @Override
    public List<TaskVo> getOneWeekTasks() {
        LocalDate today = LocalDate.now();
        LocalDate oneWeekLater = today.plusWeeks(1);

        List<TreeNode<TaskEntity>> tasks = taskRepo.findActiveTasksBetween(today, oneWeekLater);
        return mappedOrEmptyTaskList(tasks);
    }

    @Override
    public List<TaskVo> getCompletedTasks() {
        List<TreeNode<TaskEntity>> tasks = taskRepo.findCompleted();
        return mappedOrEmptyTaskList(tasks);
    }

    @Override
    public List<TaskVo> getUncategorizedTasks() {
        List<TreeNode<TaskEntity>> tasks = taskRepo.findUncategorized();
        return mappedOrEmptyTaskList(tasks);
    }

    @Override
    public List<TaskVo> getTasksOfFollowingDays(LocalDate since, LocalDate until) {
        List<TreeNode<TaskEntity>> tasks = taskRepo.findActiveTasksBetween(since, until);
        return mappedOrEmptyTaskList(tasks);
    }

    private List<TaskVo> mappedOrEmptyTaskList(List<TreeNode<TaskEntity>> tasks) {
        try {
            return TaskEntityVoMapper.toVoAll(tasks);
        } catch (MappingException me) {
            log.warn("Failed to map task entity tree to task vo", me);
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public CategoryVo saveOrUpdateCategory(CategoryVo categoryVo) throws CategorySaveFailureException {
        CategoryEntity categoryEntity = CategoryEntityVoMapper.toEntity(categoryVo);
        categoryEntity = categoryRepo.saveOrUpdate(categoryEntity);
        return CategoryEntityVoMapper.toVo(categoryEntity);
    }

    @Override
    public void deleteCategory(CategoryVo category) {
        if (!category.hasId()) {
            return;
        }

        int categoryId = category.getId();
        try {
            taskRepo.setCategoryIdToNullWhere(categoryId);
            categoryRepo.remove(category.getName());
        } catch (RowModificationException rme) {
            log.warn("Failed to wire off category with id " + categoryId + " from tasks and delete that category", rme);
        }
    }

    @Override
    public List<TaskVo> saveAllTasks(Collection<TaskVo> tasks) throws TaskSaveFailureException {
        List<TaskVo> savedTasks = new ArrayList<>(tasks.size());
        for (TaskVo currentTask : tasks) {
            TaskVo savedTask = saveTask(currentTask);
            savedTasks.add(savedTask);
        }

        return savedTasks;
    }

    @Override
    public TaskVo saveTask(TaskVo task) throws TaskSaveFailureException {
        TreeNode<TaskEntity> taskTree = TaskEntityVoMapper.toEntityTree(task);
        return tryToSaveTaskTree(taskTree);
    }

    private TaskVo tryToSaveTaskTree(TreeNode<TaskEntity> taskTree) throws TaskSaveFailureException {
        try {
            TreeNode<TaskEntity> savedTree = taskRepo.saveOrUpdate(taskTree);
            return TaskEntityVoMapper.toVo(savedTree);
        } catch (TaskPersistenceException tpe) {
            throw new TaskSaveFailureException("Failed to save task tree", tpe);
        } catch (MappingException me) {
            throw new TaskSaveFailureException("Failed to map task tree to task vo", me);
        }
    }

    @Override
    public void deleteAllTasks(Collection<TaskVo> tasks) throws TaskDeletionException {
        for (TaskVo currentTask : tasks) {
            deleteTask(currentTask);
        }
    }

    @Override
    public void deleteTask(TaskVo task) throws TaskDeletionException {
        String lnSep = System.getProperty("line.separator");
        if (!task.hasId()) {
            log.warn("Trying to delete task with no id:" + lnSep + task);
            return;
        }

        TreeNode<TaskEntity> taskTree = TaskEntityVoMapper.toEntityTree(task);
        try {
            taskRepo.remove(taskTree);
        } catch (TaskRemovalException tre) {
            throw new TaskDeletionException("Failed to delete task tree:" + lnSep + taskTree, tre);
        }
    }

    @Override
    public void deleteTaskFromTaskTree(TaskVo taskToDelete, TaskVo taskTree) throws TaskDeletionException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addSubTaskToMainTask(@NonNull TaskVo subTask, @NonNull TaskVo mainTask) {
        // The "category, priority, deadline, repeating" fields are inherited to the sub task from the main task
        subTask.setCategory(mainTask.getCategory());
        subTask.setPriority(mainTask.getPriority());
        subTask.setDeadline(mainTask.getDeadline());
        subTask.setRepeating(mainTask.isRepeating());

        List<TaskVo> subsList = mainTask.getSubTasks();
        if (subsList == null) {
            subsList = new ArrayList<>();
            mainTask.setSubTasks(subsList);
        }

        subsList.add(subTask);
    }

    @Override
    public TaskVo newMinimalTaskVo() {
        return TaskVo.builder()
                .taskDef("")
                .priority(Priority.NONE)
                .deadline(LocalDate.now())
                .category(null)
                .completed(false)
                .subTasks(null)
                .build();
    }

    @Override
    public void addNewMinimalSubTaskTo(TaskVo parent) {
        if (parent.isCompleted()) {
            throw new IllegalArgumentException("Cannot add new sub task to a completed parent task!");
        }

        TaskVo minimalSubTask = createMinimalSubTaskFor(parent);
        addSubTaskToParent(minimalSubTask, parent);
    }

    private TaskVo createMinimalSubTaskFor(TaskVo parent) {
        TaskVo minimalVo = newMinimalTaskVo();
        minimalVo.setCategory(parent.getCategory());
        minimalVo.setDeadline(parent.getDeadline());
        minimalVo.setPriority(parent.getPriority());
        minimalVo.setRepeating(parent.isRepeating());

        return minimalVo;
    }

    private void addSubTaskToParent(TaskVo subTask, TaskVo parent) {
        List<TaskVo> subTasks = (parent.hasSubTasks()) ? parent.getSubTasks() : new ArrayList<>();
        subTasks.add(subTask);
        parent.setSubTasks(subTasks);
    }

}
