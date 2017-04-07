package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task;

import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task.TaskTreeSynchronizer.ValueChangeRecord;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

public class TodayProgressDisplayer {

    private final ProgressBarSubController progressBar;
    private final ProgressRatio currentRatio;

    public TodayProgressDisplayer(ProgressBarSubController progressBar, ProgressRatio initialRatio) {
        this.progressBar = progressBar;
        this.currentRatio = initialRatio;
        updateProgressBar();
    }

    public void newTaskAdded(TaskVo newTask) {
        if (isTodayTask(newTask)) {
            currentRatio.incrementActiveCount();
            updateProgressBar();
        }
    }

    public void taskRemoved(TaskVo removedTask) {
        if (isTodayTask(removedTask)) {
            if (removedTask.isCompleted()) {
                currentRatio.decrementCompletedCount();
            } else {
                currentRatio.decrementActiveCount();
            }

            updateProgressBar();
        }
    }

    public void multipleTasksChanged(Collection<ValueChangeRecord<TaskVo>> taskChanges) {
        taskChanges.forEach(taskChange -> taskChanged(
                taskChange.getFromValue(), taskChange.getToValue()
        ));
    }

    public void taskChanged(TaskVo oldVo, TaskVo newVo) {
        if (!isTodayTask(oldVo) && !isTodayTask(newVo)) {
            return;
        }

        newRatioBycompletionChange(oldVo, newVo);
        newRatioBydeadlineChange(oldVo, newVo);
    }

    private void newRatioBycompletionChange(TaskVo oldVo, TaskVo newVo) {
        boolean wasCompletedBefore = oldVo.isCompleted();
        boolean isCompletedNow = newVo.isCompleted();

        if (wasCompletedBefore != isCompletedNow) {
            currentRatio.completionChangedFromTo(wasCompletedBefore, isCompletedNow);
            updateProgressBar();
        }
    }

    private void newRatioBydeadlineChange(TaskVo oldVo, TaskVo newVo) {
        LocalDate oldDeadline = oldVo.getDeadline();
        LocalDate newDeadLine = newVo.getDeadline();
        if (!Objects.equals(oldDeadline, newDeadLine)) {

            LocalDate today = LocalDate.now();
            boolean isCompleted = oldVo.isCompleted();
            boolean isIncrement = today.equals(newDeadLine);

            setNewRatio(isCompleted, isIncrement);
        }
    }

    private void setNewRatio(boolean isCompleted, boolean isIncrement) {
        if (isCompleted) {
            if (isIncrement) {
                currentRatio.incrementCompletedCount();
            } else {
                currentRatio.decrementCompletedCount();
            }
        } else {
            if (isIncrement) {
                currentRatio.incrementActiveCount();
            } else {
                currentRatio.decrementActiveCount();
            }
        }

        updateProgressBar();
    }

    private boolean isTodayTask(TaskVo task) {
        LocalDate today = LocalDate.now();
        LocalDate taskDeadline = task.getDeadline();
        return today.equals(taskDeadline);
    }

    private void updateProgressBar() {
        progressBar.setRatio(currentRatio);
    }
}
