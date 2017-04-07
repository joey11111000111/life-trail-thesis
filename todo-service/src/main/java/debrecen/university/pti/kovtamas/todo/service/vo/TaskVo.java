package debrecen.university.pti.kovtamas.todo.service.vo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class TaskVo {

    private Integer id = null;
    private String taskDef;
    private Priority priority;
    private LocalDate deadline;
    private String category;
    private List<TaskVo> subTasks;
    private boolean repeating;
    private boolean completed;

    static public int numOfActiveSubTasks(@NonNull TaskVo parent) {
        return numOfSubTasksWithCompletion(parent, false);
    }

    static public int numOfCompletedSubTasks(@NonNull TaskVo parent) {
        return numOfSubTasksWithCompletion(parent, true);
    }

    static private int numOfSubTasksWithCompletion(@NonNull final TaskVo parent, final boolean shouldBeCompleted) {
        int numOfSubTasks = 0;
        if (parent.hasSubTasks()) {
            List<TaskVo> subTasks = parent.getSubTasks();
            for (TaskVo subTask : subTasks) {
                numOfSubTasks += getSubTaskNumOfWithCompletion(subTask, shouldBeCompleted);
            }
        }

        return numOfSubTasks;
    }

    static private int getSubTaskNumOfWithCompletion(TaskVo task, boolean shouldBeCompleted) {
        int numOfSubTasks = 0;
        if (task.isCompleted() == shouldBeCompleted) {
            numOfSubTasks++;
        }
        if (task.hasSubTasks()) {
            numOfSubTasks += numOfSubTasksWithCompletion(task, shouldBeCompleted);
        }

        return numOfSubTasks;
    }

    static public TaskVo deepCopy(TaskVo source) {
        TaskVo copyVo = deepCopyStandaloneVo(source);

        if (source.hasSubTasks()) {
            List<TaskVo> copySubTasks = copySubTaskCollection(source);
            copyVo.setSubTasks(copySubTasks);
        }

        return copyVo;
    }

    static private TaskVo deepCopyStandaloneVo(TaskVo source) {
        return TaskVo.builder()
                .id(source.getId())
                .taskDef(source.getTaskDef())
                .priority(source.getPriority())
                .deadline(source.getDeadline())
                .category(source.getCategory())
                .subTasks(null)
                .repeating(source.isRepeating())
                .completed(source.isCompleted())
                .build();
    }

    static private List<TaskVo> copySubTaskCollection(TaskVo parent) {
        final List<TaskVo> sourceList = parent.getSubTasks();
        final List<TaskVo> copyList = new ArrayList<>();

        sourceList.forEach(sourceTask -> {
            TaskVo copyTask = deepCopy(sourceTask);
            copyList.add(copyTask);
        });

        return copyList;
    }

    public boolean hasId() {
        return id != null;
    }

    public boolean hasSubTasks() {
        return subTasks != null && !subTasks.isEmpty();
    }

}
