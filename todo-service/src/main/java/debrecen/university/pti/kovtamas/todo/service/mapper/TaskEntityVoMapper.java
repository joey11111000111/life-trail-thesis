package debrecen.university.pti.kovtamas.todo.service.mapper;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.CategoryNotFoundException;
import debrecen.university.pti.kovtamas.general.util.SimpleTreeNode;
import debrecen.university.pti.kovtamas.general.util.TreeNode;
import debrecen.university.pti.kovtamas.todo.service.vo.CategoryVo;
import debrecen.university.pti.kovtamas.todo.service.vo.Priority;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.util.ArrayList;
import java.util.List;

public final class TaskEntityVoMapper {

    static public List<TaskVo> toVoAll(List<TreeNode<TaskEntity>> entityTrees) throws MappingException {
        List<TaskVo> vos = new ArrayList<>(entityTrees.size());
        for (TreeNode<TaskEntity> currentTree : entityTrees) {
            vos.add(toVo(currentTree));
        }

        return vos;
    }

    static public TaskVo toVo(TreeNode<TaskEntity> entityTree) throws MappingException {
        try {
            return toVoRecursive(entityTree);
        } catch (CategoryNotFoundException cnfe) {
            throw new MappingException("Failed to map category entity to vo, category was not found", cnfe);
        }
    }

    static private TaskVo toVoRecursive(TreeNode<TaskEntity> entityTree) throws CategoryNotFoundException {
        TaskVo currentRootVo = mapSingleEntityToSingleVo(entityTree.getElement());

        if (entityTree.hasChildren()) {
            List<TaskVo> subTasks = new ArrayList<>();
            for (TreeNode<TaskEntity> childNode : entityTree.getChildren()) {
                TaskVo childVo = toVoRecursive(childNode);
                subTasks.add(childVo);
            }

            currentRootVo.setSubTasks(subTasks);
        }

        return currentRootVo;
    }

    static private TaskVo mapSingleEntityToSingleVo(TaskEntity entity) throws CategoryNotFoundException {
        CategoryVo category = getCategoryVo(entity);
        return TaskVo.builder()
                .id(entity.getId())
                .priority(Priority.ofInteger(entity.getPriority()))
                .deadline(entity.getDeadline())
                .taskDef(entity.getTaskDef())
                .completed(entity.isCompleted())
                .category(category)
                .build();
    }

    static public List<TreeNode<TaskEntity>> toEntityTreeAll(List<TaskVo> tasks) {
        List<TreeNode<TaskEntity>> trees = new ArrayList<>(tasks.size());
        for (TaskVo currentTask : tasks) {
            trees.add(toEntityTree(currentTask));
        }

        return trees;
    }

    static public TreeNode<TaskEntity> toEntityTree(TaskVo vo) {
        TaskEntity currentRootElement = mapSingleVoToSingleEntity(vo);
        TreeNode<TaskEntity> currentRootNode = new SimpleTreeNode<>(currentRootElement);

        if (vo.hasSubTasks()) {
            vo.getSubTasks().forEach(subTask -> {
                TreeNode<TaskEntity> childNode = toEntityTree(subTask);
                currentRootNode.addChild(childNode);
                childNode.setParent(currentRootNode);
            });
        }

        return currentRootNode;
    }

    static private TaskEntity mapSingleVoToSingleEntity(TaskVo vo) {
        CategoryVo category = vo.getCategory();
        Integer categoryId = (category == null) ? null : category.getId();
        return TaskEntity.builder()
                .id(vo.getId())
                .priority(vo.getPriority().intValue())
                .deadline(vo.getDeadline())
                .taskDef(vo.getTaskDef())
                .completed(vo.isCompleted())
                .categoryId(categoryId)
                .build();
    }

    static private CategoryVo getCategoryVo(TaskEntity entity) throws CategoryNotFoundException {
        Integer categoryId = entity.getCategoryId();
        return (categoryId == null) ? null : CategoryEntityVoMapper.voFromId(categoryId);
    }

    private TaskEntityVoMapper() {
    }

}
