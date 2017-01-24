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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachingTodoService implements TodoService {

    private static final Logger LOG = LoggerFactory.getLogger(CachingTodoService.class);

    private final TodoRepository repo;
    private final DateTimeFormatter dateFormat;

    public CachingTodoService() {
        dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        TaskEntityVoMapper.setDateFormat(dateFormat);
        repo = new JdbcTodoRepository(dateFormat);
    }

    @Override
    public List<TaskVo> getByCategory(@NonNull String category) {
        return TaskEntityVoMapper.toVo(repo.findByCategory(category));
    }

    @Override
    public List<TaskVo> getTodayTasks() {
        return TaskEntityVoMapper.toVo(repo.findTodayTasks());
    }

    @Override
    public List<TaskVo> getTomorrowTasks() {
        return getTasksOfFollowingDays(1);
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
            LOG.warn("Tried to delete a task which was not present in the database!{}{}",
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
            LOG.warn("Tried to delete a task which was not present in the database!", tnfe);
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

    private void saveTaskTree(TaskVo rootTask) throws TaskPersistenceException {
        if (!rootTask.hasSubTasks()) {
            saveStandaloneTask(rootTask);
        }

        for (TaskVo subTask : rootTask.getSubTasks()) {
            saveTaskTree(subTask);
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
