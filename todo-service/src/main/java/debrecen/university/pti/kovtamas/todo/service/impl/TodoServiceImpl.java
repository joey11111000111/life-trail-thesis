package debrecen.university.pti.kovtamas.todo.service.impl;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.JdbcTodoRepository;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRemovalException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TodoRepository;
import debrecen.university.pti.kovtamas.todo.service.api.TaskDeletionException;
import debrecen.university.pti.kovtamas.todo.service.api.TaskSaveFailureException;
import debrecen.university.pti.kovtamas.todo.service.api.TodoService;
import debrecen.university.pti.kovtamas.todo.service.mapper.TaskEntityVoMapper;
import debrecen.university.pti.kovtamas.todo.service.vo.Priority;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TodoServiceImpl implements TodoService {

    private final TodoRepository repo;
    private final DateTimeFormatter dateFormat;

    public TodoServiceImpl() {
        dateFormat = TaskEntityVoMapper.getDateFormat();
        repo = new JdbcTodoRepository(dateFormat);
    }

    @Override
    public List<TaskVo> getAll() {
        Set<TaskEntity> allEntities = repo.findAll();
        return TaskEntityVoMapper.toVo(allEntities);
    }

    @Override
    public Set<String> getCustomCategories() {
        Set<String> allCategories = repo.findAllCategories();
        // Empty string means uncategorized. It will be handled in a different way.
        allCategories.remove("");

        return allCategories;
    }

    @Override
    public List<TaskVo> getActiveByCategory(String category) {
        return TaskEntityVoMapper.toVo(repo.findByCategory(category));
    }

    // Methods for fixed categories ---------------------------------------
    @Override
    public List<TaskVo> getTodayTasks() {
        return TaskEntityVoMapper.toVo(repo.findTodayTasks());
    }

    @Override
    public List<TaskVo> getTomorrowTasks() {
        return getTasksOfFollowingDays(1);
    }

    @Override
    public List<TaskVo> getOneWeekTasks() {
        return getTasksOfFollowingDays(7);
    }

    @Override
    public List<TaskVo> getTasksOfFollowingDays(int days) {
        if (days < 1) {
            throw new IllegalArgumentException("The given day count, as following days must be at least 1");
        }
        LocalDate until = LocalDate.now().plusDays(days);
        return TaskEntityVoMapper.toVo(repo.findTasksUntil(until.format(dateFormat), dateFormat));
    }

    @Override
    public List<TaskVo> getCompletedTasks() {
        return TaskEntityVoMapper.toVo(repo.findCompletedTasks());
    }

    @Override
    public List<TaskVo> getUncategorizedTasks() {
        return getActiveByCategory("");
    }
    // /Methods for fixed categories --------------------------------------

    @Override
    public void save(@NonNull TaskVo task) throws TaskSaveFailureException {
        try {
            saveTaskTree(task);
        } catch (TaskPersistenceException tpe) {
            throw new TaskSaveFailureException("Could not save task: "
                    + System.getProperty("line.separator") + task.toString(),
                    tpe);
        }
    }

    @Override
    public void saveAll(@NonNull Collection<TaskVo> tasks) throws TaskSaveFailureException {
        if (tasks.isEmpty()) {
            return;
        }

        final boolean STANDALONE = true;
        final boolean NESTED = false;
        Predicate<TaskVo> isStandalone = (task) -> !task.hasSubTasks();

        Map<Boolean, List<TaskVo>> separatedTasks = tasks.stream()
                .collect(Collectors.partitioningBy(isStandalone));

        List<TaskEntity> standaloneEntities = separatedTasks.get(STANDALONE).stream()
                .map(TaskEntityVoMapper::toStandaloneEntity)
                .collect(Collectors.toList());

        try {
            // First save all the standalone tasks
            repo.saveAll(standaloneEntities);
            for (int i = 0; i < standaloneEntities.size(); i++) {
                List<TaskVo> standaloneVos = separatedTasks.get(STANDALONE);
                standaloneVos.get(i).setId(standaloneEntities.get(i).getId());
            }

            // Then save all the nested tasks
            for (TaskVo rootVo : separatedTasks.get(NESTED)) {
                saveTaskTree(rootVo);
            }
        } catch (TaskPersistenceException tpe) {
            throw new TaskSaveFailureException("Could not save given task list!", tpe);
        }
    }

    @Override
    public void delete(@NonNull TaskVo task) throws TaskDeletionException {
        Set<Integer> allTaskIds = extractAllTaskIdsOf(task);
        String lnSep = System.getProperty("line.separator");
        try {
            repo.removeAll(allTaskIds);
        } catch (TaskRemovalException tre) {

            throw new TaskDeletionException("Could not remove task:"
                    + lnSep
                    + task.toString(),
                    tre);

        } catch (TaskNotFoundException tnfe) {
            log.warn("Tried to delete a task which was not present in the database!{}{}",
                    lnSep, task.toString(), tnfe);
        }
    }

    @Override
    public void deleteAll(@NonNull Collection<TaskVo> tasks) throws TaskDeletionException {
        Set<Integer> allTaskIds = new HashSet<>();
        tasks.forEach(task -> allTaskIds.addAll(extractAllTaskIdsOf(task)));

        try {
            repo.removeAll(allTaskIds);
        } catch (TaskRemovalException tre) {
            throw new TaskDeletionException("Could not remove task specified in the task collection!", tre);
        } catch (TaskNotFoundException tnfe) {
            log.warn("Tried to delete a task which was not present in the database!", tnfe);
        }
    }

    @Override
    public void deleteTaskFromTaskTree(@NonNull TaskVo taskToDelete, @NonNull TaskVo taskTree) throws TaskDeletionException {
        if (taskToDelete.equals(taskTree)) {
            delete(taskTree);
            return;
        }

        if (taskTree.hasSubTasks()) {
            for (TaskVo subTask : taskTree.getSubTasks()) {
                deleteTaskFromTaskTree(taskToDelete, subTask);
            }
        }
    }

    @Override
    public void addSubTask(@NonNull TaskVo mainTask, @NonNull TaskVo subTask) {
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

    public DateTimeFormatter getDateFormat() {
        return dateFormat;
    }

    @Override
    public TaskVo newMinimalTaskVo() {
        return TaskVo.builder()
                .taskDef("")
                .priority(Priority.NONE)
                .deadline(LocalDate.now())
                .category("")
                .repeating(false)
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

    private void saveTaskTree(TaskVo rootTask) throws TaskPersistenceException {
        if (!rootTask.hasSubTasks()) {
            saveStandaloneTask(rootTask);
        }

        if (rootTask.hasSubTasks()) {
            for (TaskVo subTask : rootTask.getSubTasks()) {
                saveTaskTree(subTask);
            }
        }

        TaskEntity rootEntity = TaskEntityVoMapper.toEntity(rootTask);
        repo.save(rootEntity);
        if (!rootTask.hasId()) {
            rootTask.setId(rootEntity.getId());
        }
    }

    private void saveStandaloneTask(TaskVo task) throws TaskPersistenceException {
        TaskEntity entity = TaskEntityVoMapper.toStandaloneEntity(task);
        repo.save(entity);
        if (!task.hasId()) {
            task.setId(entity.getId());
        }
    }

    private Set<Integer> extractAllTaskIdsOf(TaskVo rootTask) {
        Set<Integer> ids = new HashSet<>();
        if (!rootTask.hasId()) {
            return ids;
        }
        ids.add(rootTask.getId());

        if (rootTask.hasSubTasks()) {
            rootTask.getSubTasks()
                    .forEach(sub -> ids.addAll(extractAllTaskIdsOf(sub)));
        }

        return ids;
    }

}
