package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task;

import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.util.Collection;

public class ProgressRatio {

    private Integer completedCount;
    private Integer activeCount;

    static public ProgressRatio fromTodayTasks(Collection<TaskVo> todayTasks) {
        int completed = 0;
        int active = 0;

        for (TaskVo task : todayTasks) {
            if (task.isCompleted()) {
                completed++;
            } else {
                active++;
            }

            completed += TaskVo.numOfCompletedSubTasks(task);
            active += TaskVo.numOfActiveSubTasks(task);
        }

        return new ProgressRatio(completed, active);
    }

    public ProgressRatio(Integer completedCount, Integer activeCount) {
        this.completedCount = completedCount;
        this.activeCount = activeCount;
    }

    public double getRatio() {
        double allCount = completedCount + activeCount;

        // Prevent division with 0
        if (allCount == 0) {
            return 0.0;
        }

        return completedCount / allCount;
    }

    public Integer getCompletedCount() {
        return completedCount;
    }

    public Integer getActiveCount() {
        return activeCount;
    }

    public void incrementActiveCount() {
        this.activeCount++;
    }

    public void incrementCompletedCount() {
        this.completedCount++;
    }

    public void decrementActiveCount() {
        this.activeCount--;
    }

    public void decrementCompletedCount() {
        this.completedCount--;
    }

    public void completionChangedFromTo(boolean fromCompletion, boolean toCompletion) {
        if (fromCompletion == toCompletion) {
            throw new IllegalArgumentException("From and to values are the same. This is no change.");
        }

        activeCount += (fromCompletion) ? 1 : -1;
        completedCount += (toCompletion) ? 1 : -1;
    }

    @Override
    public String toString() {
        return "ProgressRatio:\tCompleted: " + completedCount + "\tActive: " + activeCount + "\tRatio: " + getRatio();
    }

}
