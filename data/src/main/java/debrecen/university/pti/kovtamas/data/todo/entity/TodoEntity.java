package debrecen.university.pti.kovtamas.data.todo.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
