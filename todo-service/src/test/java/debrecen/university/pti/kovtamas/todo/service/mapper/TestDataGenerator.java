package debrecen.university.pti.kovtamas.todo.service.mapper;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.general.util.SimpleTreeNode;
import debrecen.university.pti.kovtamas.general.util.TreeNode;
import debrecen.university.pti.kovtamas.todo.service.vo.CategoryVo;
import debrecen.university.pti.kovtamas.todo.service.vo.Priority;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestDataGenerator {

    private final int testObjectCount;

    private final int startId;
    private final String taskDefBase;
    private final int priorityAsInt;
    private final Priority priorityAsEnum;
    private final CategoryVo categoryVo;
    private final int categoryId;
    private final boolean isCompleted;
    private final LocalDate deadline;

    public TestDataGenerator(CategoryVo givenCategoryVo) {
        testObjectCount = 4;
        startId = 0;
        taskDefBase = "generatedTaskDef";
        priorityAsEnum = Priority.LOW;
        priorityAsInt = priorityAsEnum.intValue();
        categoryVo = givenCategoryVo;
        categoryId = givenCategoryVo.getId();
        isCompleted = false;
        deadline = LocalDate.now();
    }

    public TreeNode<TaskEntity> generateEntityTree() {
        List<TaskEntity> tasks = generateSingleEntities(testObjectCount);
        List<TreeNode<TaskEntity>> nodes = new ArrayList<>(testObjectCount);
        for (int i = 0; i < testObjectCount; i++) {
            TreeNode<TaskEntity> currentNode = new SimpleTreeNode<>(tasks.get(i));
            nodes.add(currentNode);
        }

        for (int i = 0; i < testObjectCount - 1; i++) {
            nodes.get(i).addChild(nodes.get(i + 1));
            nodes.get(i + 1).setParent(nodes.get(i));
        }

        return nodes.get(0);
    }

    private List<TaskEntity> generateSingleEntities(int testObjectCount) {
        List<TaskEntity> testEntities = new ArrayList<>(testObjectCount);
        for (int i = 0; i < testObjectCount; i++) {
            testEntities.add(
                    TaskEntity.builder()
                            .id(startId + i)
                            .taskDef(taskDefBase + i)
                            .priority(priorityAsInt)
                            .categoryId(categoryId)
                            .completed(isCompleted)
                            .deadline(deadline)
                            .build()
            );
        }

        return testEntities;
    }

    public TaskVo generateVo() {
        List<TaskVo> vos = generateSingleVos();
        for (int i = 0; i < testObjectCount - 1; i++) {
            List<TaskVo> subTasks = new ArrayList<>();
            subTasks.add(vos.get(i + 1));
            vos.get(i).setSubTasks(subTasks);
        }

        return vos.get(0);
    }

    private List<TaskVo> generateSingleVos() {
        List<TaskVo> vos = new ArrayList<>(testObjectCount);
        for (int i = 0; i < testObjectCount; i++) {
            vos.add(
                    TaskVo.builder()
                            .id(startId + i)
                            .taskDef(taskDefBase + i)
                            .priority(priorityAsEnum)
                            .category(categoryVo)
                            .completed(isCompleted)
                            .deadline(deadline)
                            .build()
            );
        }

        return vos;
    }

}
