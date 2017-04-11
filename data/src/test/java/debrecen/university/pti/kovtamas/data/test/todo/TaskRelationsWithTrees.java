package debrecen.university.pti.kovtamas.data.test.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.entity.todo.TaskRelationEntity;
import debrecen.university.pti.kovtamas.general.util.TreeNode;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRelationsWithTrees {

    private List<TaskEntity> tasks;
    private List<TaskRelationEntity> relations;
    private List<TreeNode<TaskEntity>> trees;

}
