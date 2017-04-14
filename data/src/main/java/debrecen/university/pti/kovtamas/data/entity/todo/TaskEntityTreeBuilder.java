package debrecen.university.pti.kovtamas.data.entity.todo;

import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.MissingTaskException;
import debrecen.university.pti.kovtamas.general.util.SimpleTreeNode;
import debrecen.university.pti.kovtamas.general.util.TreeNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;

public final class TaskEntityTreeBuilder {

    @EqualsAndHashCode
    static public class TaskRelations {

        private final List<TaskEntity> tasks;
        private final List<TaskRelationEntity> relations;

        public TaskRelations(Collection<TaskEntity> tasks, Collection<TaskRelationEntity> relations) {
            this.tasks = new ArrayList<>(tasks);
            this.relations = new ArrayList<>(relations);
        }

        public List<TaskEntity> getTasks() {
            return Collections.unmodifiableList(tasks);
        }

        public List<TaskRelationEntity> getRelations() {
            return Collections.unmodifiableList(relations);
        }

        @Override
        public String toString() {
            String lnSep = System.getProperty("line.separator");
            String str = "TaskRelations:" + lnSep;
            str += "Tasks:" + lnSep;
            for (TaskEntity entity : tasks) {
                str += entity.toString() + lnSep;
            }
            str += "Relations:" + lnSep;
            for (TaskRelationEntity relation : relations) {
                str += relation.toString() + lnSep;
            }

            return str;
        }

    }

    static public List<TreeNode<TaskEntity>> buildTaskTrees(TaskRelations taskRelations) {
        List<TaskEntity> tasks = taskRelations.getTasks();
        List<TreeNode<TaskEntity>> nodes = createIndependentNodesInOrderFrom(tasks);
        List<TaskRelationEntity> relations = taskRelations.getRelations();

        // Apply relations to nodes
        for (TaskRelationEntity relation : relations) {
            int parentId = relation.getParentId();
            int childId = relation.getChildId();

            TreeNode<TaskEntity> parent = null;
            TreeNode<TaskEntity> child = null;

            for (TreeNode<TaskEntity> node : nodes) {
                int elementId = node.getElement().getId();
                if (elementId == parentId) {
                    parent = node;
                } else if (elementId == childId) {
                    child = node;
                }

                if (parent != null && child != null) {
                    break;
                }
            }

            if (parent == null || child == null) {
                throw new MissingTaskException("Cannot build task entity tree because at least one task "
                        + "for the relation '" + parentId + " -> " + childId + "' is missing");
            }

            child.setParent(parent);
            parent.addChild(child);
        }

        List<TreeNode<TaskEntity>> taskTrees = nodes.stream()
                .filter(node -> !node.hasParent())
                .collect(Collectors.toList());

        return taskTrees;
    }

    static private List<TreeNode<TaskEntity>> createIndependentNodesInOrderFrom(List<TaskEntity> entities) {
        return entities.stream()
                .map(SimpleTreeNode<TaskEntity>::new)
                .collect(Collectors.toList());
    }

    static public TaskRelations collapseTaskTrees(TreeNode<TaskEntity> taskTree) {
        return collapseTaskTrees(Arrays.asList(taskTree));
    }

    static public TaskRelations collapseTaskTrees(List<TreeNode<TaskEntity>> taskTrees) {
        List<TaskEntity> tasks = new ArrayList<>();
        List<TaskRelationEntity> relations = new ArrayList<>();

        final TreeNode<TaskEntity> parent = null;
        taskTrees.forEach(node -> {
            TaskRelations taskRelations = collapseSingleTaskTree(node, parent);
            tasks.addAll(taskRelations.getTasks());
            relations.addAll(taskRelations.getRelations());
        });

        return new TaskRelations(tasks, relations);
    }

    static private TaskRelations collapseSingleTaskTree(TreeNode<TaskEntity> node, TreeNode<TaskEntity> parent) {
        List<TaskEntity> tasks = new ArrayList<>();
        List<TaskRelationEntity> relations = new ArrayList<>();

        tasks.add(node.getElement());
        if (parent != null) {
            relations.add(
                    TaskRelationEntity.builder()
                            .parentId(parent.getElement().getId())
                            .childId(node.getElement().getId())
                            .build()
            );
        }

        if (node.hasChildren()) {
            node.getChildren().forEach(childNode -> {
                TaskRelations childRelations = collapseSingleTaskTree(childNode, node);
                tasks.addAll(childRelations.getTasks());
                relations.addAll(childRelations.getRelations());
            });
        }

        return new TaskRelations(tasks, relations);
    }

    private TaskEntityTreeBuilder() {
    }

}
