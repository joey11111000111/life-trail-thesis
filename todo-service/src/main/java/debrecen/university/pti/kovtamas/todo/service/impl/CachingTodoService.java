package debrecen.university.pti.kovtamas.todo.service.impl;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.JdbcTodoRepository;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TodoRepository;
import debrecen.university.pti.kovtamas.todo.service.api.TaskSaveFailureException;
import debrecen.university.pti.kovtamas.todo.service.api.TodoService;
import debrecen.university.pti.kovtamas.todo.service.mapper.TaskEntityVoMapper;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.util.List;

public class CachingTodoService implements TodoService {

    private final TodoRepository repo;

    public CachingTodoService() {
        repo = new JdbcTodoRepository();
    }

    @Override
    public List<TaskVo> getByCategory(String category) {
        throw new UnsupportedOperationException("Not supported yet.");
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
    public void saveAll(List<TaskVo> tasks) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(TaskVo task) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteAll(List<TaskVo> tasks) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
