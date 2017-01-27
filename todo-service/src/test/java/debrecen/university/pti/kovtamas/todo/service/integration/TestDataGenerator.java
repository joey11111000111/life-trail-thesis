package debrecen.university.pti.kovtamas.todo.service.integration;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.test.JdbcTestUtils;
import debrecen.university.pti.kovtamas.todo.service.mapper.TaskEntityVoMapper;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestDataGenerator {

    private final JdbcTestUtils jdbcTestUtils;

    public final String TEST_CATEGORY = "personal";

    public TestDataGenerator(JdbcTestUtils jdbcTestUtils) {
        this.jdbcTestUtils = jdbcTestUtils;
    }

    private List<TaskEntity> generateEntitiesForCategoryTest() {
        List<TaskEntity> entities = new ArrayList<>();
        entities.add(TaskEntity.builder()
                .taskDef("Task1")
                .priority(1)
                .deadline("2017.01.10")
                .category(TEST_CATEGORY)
                .subTaskIds(null)
                .repeating(false)
                .build()
        );
        entities.add(TaskEntity.builder()
                .taskDef("Task2")
                .priority(2)
                .deadline("2017.01.10")
                .category(TEST_CATEGORY)
                .subTaskIds(null)
                .repeating(true)
                .build()
        );
        entities.add(TaskEntity.builder()
                .taskDef("Task3")
                .priority(0)
                .deadline("2017.02.10")
                .category(TEST_CATEGORY)
                .subTaskIds(null)
                .repeating(false)
                .build()
        );
        entities.add(TaskEntity.builder()
                .taskDef("Task4")
                .priority(1)
                .deadline("2017.01.20")
                .category("fun")
                .subTaskIds(null)
                .repeating(true)
                .build()
        );
        entities.add(TaskEntity.builder()
                .taskDef("Task5")
                .priority(1)
                .deadline("2017.01.20")
                .category("uncategorized")
                .subTaskIds(null)
                .repeating(false)
                .build()
        );

        return entities;
    }

    private List<TaskEntity> generateAndSaveEntitiesForCategoryTest() {
        List<TaskEntity> entities = generateEntitiesForCategoryTest();
        // Save entities and after they get a generated id, set relations
        jdbcTestUtils.populateDatabase(entities);
        entities.get(1).setSubTaskIds(Integer.toString(entities.get(2).getId()));
        entities.get(0).setSubTaskIds(Integer.toString(entities.get(1).getId()));
        jdbcTestUtils.populateDatabase(entities);       // Update records so two entities have sub tasks

        return entities;
    }

    public List<TaskVo> generateAndPersistExpectedVosForCategoryTest() {
        List<TaskEntity> entities = generateAndSaveEntitiesForCategoryTest();
        // Create TaskVos out of the entities, including the relations
        List<TaskVo> expectedVos = new ArrayList<>();
        TaskVo mainTask = TaskEntityVoMapper.toStandaloneVo(entities.get(0));
        TaskVo subTask = TaskEntityVoMapper.toStandaloneVo(entities.get(1));
        mainTask.setSubTasks(Arrays.asList(subTask));
        expectedVos.add(mainTask);

        mainTask = subTask;
        subTask = TaskEntityVoMapper.toStandaloneVo(entities.get(2));
        mainTask.setSubTasks(Arrays.asList(subTask));

        return expectedVos;
    }

}
