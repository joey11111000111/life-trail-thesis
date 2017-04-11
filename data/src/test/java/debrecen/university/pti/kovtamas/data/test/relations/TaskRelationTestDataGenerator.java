package debrecen.university.pti.kovtamas.data.test.relations;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskRelationEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TaskRelationTestDataGenerator {

    static TaskRelationEntity generateOneEntity() {
        TaskRelationEntity entity = TaskRelationEntity.builder()
                .parentId(0)
                .childId(1)
                .build();
        return entity;
    }

    static List<TaskRelationEntity> generateTestEntities(int numOfEntities) {
        List<TaskRelationEntity> generatedEntities = new ArrayList<>();
        for (int i = 0; i < numOfEntities; i++) {
            generatedEntities.add(
                    TaskRelationEntity.builder()
                            .parentId(i)
                            .childId(1000 - i)
                            .build()
            );
        }

        return generatedEntities;
    }

    static List<TaskRelationEntity> generateCircularDependencyEntities() {
        TaskRelationEntity forwardRelation = generateOneEntity();
        TaskRelationEntity backwardsRelation = TaskRelationEntity.builder()
                .parentId(forwardRelation.getChildId())
                .childId(forwardRelation.getParentId())
                .build();

        return Arrays.asList(forwardRelation, backwardsRelation);
    }

    static List<TaskRelationEntity> generateDuplicatedChildIdEntities() {
        TaskRelationEntity e1 = TaskRelationEntity.builder()
                .parentId(0)
                .childId(1)
                .build();

        TaskRelationEntity e2 = TaskRelationEntity.builder()
                .parentId(22)
                .childId(e1.getChildId())
                .build();

        return Arrays.asList(e1, e2);
    }

    static List<TaskRelationEntity> generateEntitiesWhereParentIsChildToo() {
        List<TaskRelationEntity> generatedEntities = new ArrayList<>();
        generatedEntities.add(
                TaskRelationEntity.builder()
                        .parentId(1)
                        .childId(8)
                        .build()
        );
        generatedEntities.add(
                TaskRelationEntity.builder()
                        .parentId(1)
                        .childId(7)
                        .build()
        );
        generatedEntities.add(
                TaskRelationEntity.builder()
                        .parentId(2)
                        .childId(1)
                        .build()
        );
        generatedEntities.add(
                TaskRelationEntity.builder()
                        .parentId(2)
                        .childId(6)
                        .build()
        );

        return generatedEntities;
    }

}
