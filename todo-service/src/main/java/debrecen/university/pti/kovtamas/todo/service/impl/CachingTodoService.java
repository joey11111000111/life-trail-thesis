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
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachingTodoService implements TodoService {

    private static final Logger LOG = LoggerFactory.getLogger(CachingTodoService.class);

    private final TodoRepository repo;

    public CachingTodoService() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        TaskEntityVoMapper.setDateFormat(dateFormat);
        repo = new JdbcTodoRepository(dateFormat);
    }

    @Override
    public List<TaskVo> getByCategory(String category) {
        return TaskEntityVoMapper.toVo(repo.findByCategory(category));

    }

    @Override
    public List<TaskVo> getTodayTasks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<TaskVo> getTomorrowTasks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<TaskVo> getTasksOfFollowingDays(int days) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void save(TaskVo task) throws TaskSaveFailureException {
        try {
            saveTaskTree(task);
        } catch (TaskPersistenceException tpe) {
            throw new TaskSaveFailureException("Could not save task: "
                    + System.getProperty("line.separator") + task.toString(),
                    tpe);
        }
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

    @Override
    public void saveAll(Collection<TaskVo> tasks) throws TaskSaveFailureException {
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
    public void delete(TaskVo task) throws TaskDeletionException {
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

    @Override
    public void deleteAll(Collection<TaskVo> tasks) throws TaskDeletionException {
        Set<Integer> allTaskIds = new HashSet<>();
        tasks.forEach(task -> allTaskIds.addAll(extractAllTaskIdsOf(task)));

        String lnSep = System.getProperty("line.separator");
        try {
            repo.removeAll(allTaskIds);
        } catch (TaskRemovalException tre) {

            throw new TaskDeletionException("Could not remove task specified in the task collection!", tre);

        } catch (TaskNotFoundException tnfe) {
            LOG.warn("Tried to delete a task which was not present in the database!", tnfe);
        }

    }

}
