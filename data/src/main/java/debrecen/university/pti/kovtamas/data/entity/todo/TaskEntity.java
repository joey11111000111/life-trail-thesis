package debrecen.university.pti.kovtamas.data.entity.todo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskEntity {

    private Integer id = null;
    private String taskDef;
    private int priority;
    private String deadline;
    private String category;
    private String subTaskIds;
    private boolean repeating;
    private boolean completed;

    public boolean hasId() {
        return id != null;
    }

    public boolean hasSubTasks() {
        return subTaskIds != null && !subTaskIds.isEmpty();
    }

}
