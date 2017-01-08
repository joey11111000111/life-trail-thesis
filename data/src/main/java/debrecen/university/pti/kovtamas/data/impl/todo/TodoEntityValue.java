package debrecen.university.pti.kovtamas.data.impl.todo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class TodoEntityValue implements Serializable {

    private static final long serialVersionUID = 30000L;

    private String taskDef;
    private int priority;
    private String deadline;
    private String category;
    private String subTaskIds;
    private boolean repeating;

}
