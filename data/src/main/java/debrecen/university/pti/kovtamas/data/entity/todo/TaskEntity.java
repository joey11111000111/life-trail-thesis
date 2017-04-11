package debrecen.university.pti.kovtamas.data.entity.todo;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskEntity implements Serializable {

    static private final long serialVersionUID = 200L;

    private Integer id = null;
    private Integer categoryId = null;
    private String taskDef = null;
    private Integer priority = null;
    private LocalDate deadline = null;
    private boolean completed;

    static public TaskEntity copy(TaskEntity source) {
        // Every field is unmutable or primitive so a shallow copy is just as good as a deep copy
        return builder()
                .id(source.getId())
                .categoryId(source.getCategoryId())
                .taskDef(source.getTaskDef())
                .priority(source.getPriority())
                .deadline(source.getDeadline())
                .completed(source.isCompleted())
                .build();
    }

    public boolean hasId() {
        return id != null;
    }

    public boolean hasCategoryId() {
        return categoryId != null;
    }

}
