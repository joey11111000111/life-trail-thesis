package debrecen.university.pti.kovtamas.todo.service.integration;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.test.JdbcTestUtils;
import debrecen.university.pti.kovtamas.todo.service.mapper.TaskEntityVoMapper;
import debrecen.university.pti.kovtamas.todo.service.vo.Priority;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestDataGenerator {

    private final JdbcTestUtils jdbcTestUtils;

    public final String TEST_CATEGORY = "personal";

    public TestDataGenerator(JdbcTestUtils jdbcTestUtils) {
        this.jdbcTestUtils = jdbcTestUtils;
    }

    // byCategoryTest ------------------------------------------------
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
    // /byCategoryTest -----------------------------------------------

    // save and find all test ----------------------------------------
    public List<TaskVo> generateVosToSave() {
        List<TaskVo> standaloneVos = new ArrayList<>();
        standaloneVos.add(
                TaskVo.builder()
                        .taskDef("Task1")
                        .category("category1")
                        .priority(Priority.NONE)
                        .deadline(LocalDate.now().plusMonths(2))
                        .repeating(false)
                        .subTasks(null)
                        .build()
        );
        standaloneVos.add(
                TaskVo.builder()
                        .taskDef("Task2")
                        .category("category1")
                        .priority(Priority.NONE)
                        .deadline(LocalDate.now().plusMonths(2))
                        .repeating(false)
                        .subTasks(null)
                        .build()
        );
        standaloneVos.add(
                TaskVo.builder()
                        .taskDef("Task3")
                        .category("category1")
                        .priority(Priority.NONE)
                        .deadline(LocalDate.now().plusMonths(2))
                        .repeating(false)
                        .subTasks(null)
                        .build()
        );
        standaloneVos.add(
                TaskVo.builder()
                        .taskDef("Task5")
                        .category("category1")
                        .priority(Priority.NONE)
                        .deadline(LocalDate.now().plusMonths(2))
                        .repeating(false)
                        .subTasks(null)
                        .build()
        );

        standaloneVos.add(
                TaskVo.builder()
                        .taskDef("Task4")
                        .category("category2")
                        .priority(Priority.MEDIUM)
                        .deadline(LocalDate.now())
                        .repeating(true)
                        .subTasks(null)
                        .build()
        );

        List<TaskVo> expectedVos = new ArrayList<>();
        expectedVos.add(standaloneVos.get(0));
        List<TaskVo> subTasks = new ArrayList<>();
        subTasks.add(standaloneVos.get(1));
        subTasks.add(standaloneVos.get(2));
        subTasks.get(1).setSubTasks(Arrays.asList(standaloneVos.get(3)));
        expectedVos.get(0).setSubTasks(subTasks);

        expectedVos.add(standaloneVos.get(4));
        return expectedVos;
    }
    // /save and find all test ---------------------------------------

    // Today tasks test ----------------------------------------------
    public List<TaskVo> generateVosForTodayTest() {
        List<TaskVo> standaloneVos = new ArrayList<>();
        standaloneVos.add(
                TaskVo.builder()
                        .taskDef("Task1")
                        .category("category1")
                        .priority(Priority.HIGH)
                        .deadline(LocalDate.now())
                        .repeating(false)
                        .subTasks(null)
                        .build()
        );
        standaloneVos.add(
                TaskVo.builder()
                        .taskDef("Task2")
                        .category("category1")
                        .priority(Priority.HIGH)
                        .deadline(LocalDate.now())
                        .repeating(false)
                        .subTasks(null)
                        .build()
        );
        standaloneVos.add(
                TaskVo.builder()
                        .taskDef("Task1")
                        .category("category1")
                        .priority(Priority.HIGH)
                        .deadline(LocalDate.now().minusYears(2))
                        .repeating(true)
                        .subTasks(null)
                        .build()
        );
        standaloneVos.add(
                TaskVo.builder()
                        .taskDef("Task1")
                        .category("uncategorized")
                        .priority(Priority.LOW)
                        .deadline(LocalDate.now().plusWeeks(3))
                        .repeating(false)
                        .subTasks(null)
                        .build()
        );

        List<TaskVo> allVos = new ArrayList<>();
        allVos.add(standaloneVos.get(0));
        allVos.get(0).setSubTasks(Arrays.asList(standaloneVos.get(1)));
        allVos.add(standaloneVos.get(2));
        allVos.add(standaloneVos.get(3));

        return allVos;
    }
    // /Today tasks test ---------------------------------------------
}
