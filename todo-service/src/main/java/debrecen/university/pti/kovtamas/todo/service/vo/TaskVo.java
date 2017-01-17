package debrecen.university.pti.kovtamas.todo.service.vo;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

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

    public boolean hasId() {
        return id != null;
    }

    public boolean hasSubTasks() {
        return subTasks != null && !subTasks.isEmpty();
    }

}
