package debrecen.university.pti.kovtamas.todo.service.impl;

import debrecen.university.pti.kovtamas.todo.service.api.TodoService;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.util.List;

public class CachingTodoService implements TodoService {

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
    public void save(TaskVo task) {
        throw new UnsupportedOperationException("Not supported yet.");
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
