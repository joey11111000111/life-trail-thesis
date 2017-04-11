package debrecen.university.pti.kovtamas.data.test.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.entity.todo.TaskRelationEntity;
import debrecen.university.pti.kovtamas.general.util.SimpleTreeNode;
import debrecen.university.pti.kovtamas.general.util.TreeNode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TreeBuilderTestDataGenerator {

    static private List<TaskEntity> generateEntities(final int numOfEntities) {
        List<TaskEntity> entities = new ArrayList<>();
        for (int i = 0; i < numOfEntities; i++) {
            entities.add(TaskEntity.builder()
                    .id(i)
                    .categoryId(i % 4)
                    .completed(false)
                    .deadline(LocalDate.now())
                    .priority(2)
                    .taskDef("Task def")
                    .build()
            );
        }

        return entities;
    }

    static public TaskRelationsWithTrees notRelatedEntiities() {
        final int testEntityCount = 4;
        List<TaskEntity> tasks = generateEntities(testEntityCount);
        List<TaskRelationEntity> relations = Collections.EMPTY_LIST;
        List<TreeNode<TaskEntity>> trees = new ArrayList<>();

        for (TaskEntity entity : tasks) {
            SimpleTreeNode<TaskEntity> node = new SimpleTreeNode<>();
            node.setElement(entity);
            trees.add(node);
        }

        return TaskRelationsWithTrees.builder()
                .tasks(tasks)
                .relations(relations)
                .trees(trees)
                .build();
    }

    static public TaskRelationsWithTrees allEntitiesAreInOneTree() {
        final int testEntityCount = 10;
        List<TaskEntity> tasks = generateEntities(testEntityCount);
        List<TaskRelationEntity> relations = new ArrayList<>();
        List<TreeNode<TaskEntity>> allNodes = new ArrayList<>(testEntityCount);

        // Create nodes
        for (int i = 0; i < testEntityCount; i++) {
            TreeNode<TaskEntity> node = new SimpleTreeNode<>();
            node.setElement(tasks.get(i));
            allNodes.add(node);
        }

        // Set children relations for first task separatly because it has two children not only one like the others
        relations.add(
                TaskRelationEntity.builder()
                        .parentId(0)
                        .childId(testEntityCount - 1)
                        .build()
        );
        relations.add(
                TaskRelationEntity.builder()
                        .parentId(0)
                        .childId(1)
                        .build()
        );

        // Apply the relations to the first node
        TreeNode<TaskEntity> parent = allNodes.get(0);
        parent.addChild(allNodes.get(testEntityCount - 1));
        allNodes.get(testEntityCount - 1).setParent(parent);
        parent.addChild(allNodes.get(1));
        allNodes.get(1).setParent(parent);

        // Set and apply relations to all the remaining tasks
        for (int i = 1; i < testEntityCount - 2; i++) {
            relations.add(
                    TaskRelationEntity.builder()
                            .parentId(i)
                            .childId(i + 1)
                            .build()
            );

            allNodes.get(i).addChild(allNodes.get(i + 1));
            allNodes.get(i + 1).setParent(allNodes.get(i));
        }

        return TaskRelationsWithTrees.builder()
                .tasks(tasks)
                .relations(relations)
                .trees(Arrays.asList(allNodes.get(0)))
                .build();
    }

    static public TaskRelationsWithTrees tasksAreOnMultipleTrees() {
        final int testEntityCount = 10;
        List<TaskEntity> tasks = generateEntities(testEntityCount);
        List<TaskRelationEntity> relations = new ArrayList<>();
        List<TreeNode<TaskEntity>> allNodes = new ArrayList<>(testEntityCount);

        // Create nodes
        for (int i = 0; i < testEntityCount; i++) {
            TreeNode<TaskEntity> node = new SimpleTreeNode<>();
            node.setElement(tasks.get(i));
            allNodes.add(node);
        }

        // Set and apply relations
        final int lastIndex = testEntityCount - 1;
        // First tree
        relations.add(
                TaskRelationEntity.builder()
                        .parentId(0).childId(1).build()
        );
        allNodes.get(0).addChild(allNodes.get(1));
        allNodes.get(1).setParent(allNodes.get(1));
        relations.add(
                TaskRelationEntity.builder()
                        .parentId(0).childId(lastIndex).build()
        );
        allNodes.get(0).addChild(allNodes.get(lastIndex));
        allNodes.get(lastIndex).setParent(allNodes.get(0));
        relations.add(
                TaskRelationEntity.builder()
                        .parentId(1).childId(2).build()
        );
        allNodes.get(1).addChild(allNodes.get(2));
        allNodes.get(2).setParent(allNodes.get(1));
        relations.add(
                TaskRelationEntity.builder()
                        .parentId(2).childId(lastIndex - 1).build()
        );
        allNodes.get(2).addChild(allNodes.get(lastIndex - 1));
        allNodes.get(lastIndex - 1).setParent(allNodes.get(2));

        // Second tree
        relations.add(
                TaskRelationEntity.builder()
                        .parentId(3).childId(4).build()
        );
        allNodes.get(3).addChild(allNodes.get(4));
        allNodes.get(4).setParent(allNodes.get(3));
        relations.add(
                TaskRelationEntity.builder()
                        .parentId(3).childId(6).build()
        );
        allNodes.get(3).addChild(allNodes.get(6));
        allNodes.get(6).setParent(allNodes.get(3));
        relations.add(
                TaskRelationEntity.builder()
                        .parentId(6).childId(5).build()
        );
        allNodes.get(6).addChild(allNodes.get(5));
        allNodes.get(5).setParent(allNodes.get(6));

        // Thrid tree is only made of the 7th node
        List<TreeNode<TaskEntity>> trees = new ArrayList<>(3);
        trees.add(allNodes.get(0));
        trees.add(allNodes.get(3));
        trees.add(allNodes.get(7));

        return TaskRelationsWithTrees.builder()
                .tasks(tasks)
                .relations(relations)
                .trees(trees)
                .build();
    }

}
