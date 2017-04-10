package debrecen.university.pti.kovtamas.data.interfaces.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskRelationEntity;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskRelationPersistenceException;
import java.util.List;

public interface TaskRelationsRepository {

    List<TaskRelationEntity> findAll();

    List<TaskRelationEntity> findAllWhereParentOrChildId(int id);

    TaskRelationEntity save(TaskRelationEntity newRelation) throws TaskRelationPersistenceException;

    List<TaskRelationEntity> saveAll(List<TaskRelationEntity> newRelations) throws TaskRelationPersistenceException;

    void removeRelation(int relationId);

    void removeAllWhereParentOrChildIdIs(int id);

    void clearTable();

}
