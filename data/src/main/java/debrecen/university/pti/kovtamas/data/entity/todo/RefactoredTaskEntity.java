package debrecen.university.pti.kovtamas.data.entity.todo;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefactoredTaskEntity {

    private Integer id = null;
    private String taskDef = null;
    private Integer priority = null;
    private String deadline = null;
    private String category = null;
    private List<RefactoredTaskEntity> subTasks = null;
    private boolean repeating;
    private boolean completed;

    public boolean hasId() {
        return id != null;
    }

    public boolean hasSubTasks() {
        return subTasks != null && !subTasks.isEmpty();
    }

}
