package debrecen.university.pti.kovtamas.data.entity.todo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TodoEntity {

    private int id;
    private String taskDef;
    private int priority;
    private String deadline;
    private String category;
    private String subTaskIds;
    private boolean repeating;

}
