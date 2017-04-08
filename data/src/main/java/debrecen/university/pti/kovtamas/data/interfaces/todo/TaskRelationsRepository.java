package debrecen.university.pti.kovtamas.data.interfaces.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.RefactoredTaskEntity;
import java.util.List;

public interface TaskRelationsRepository {

    List<RefactoredTaskEntity> findAllChildrenOf(RefactoredTaskEntity parentEntity);

    void addNewRelation(int parentId, int childId);

    void deleteRelation(int relationId);

    void deleteAllWhereParentOrChildIdIs(int id);

    void clearTable();

}
