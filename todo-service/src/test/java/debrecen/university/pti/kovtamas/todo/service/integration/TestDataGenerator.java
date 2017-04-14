package debrecen.university.pti.kovtamas.todo.service.integration;

public class TestDataGenerator {
//
//    private final JdbcTestUtils jdbcTestUtils;
//
//    public final String TEST_CATEGORY = "personal";
//
//    public TestDataGenerator(JdbcTestUtils jdbcTestUtils) {
//        this.jdbcTestUtils = jdbcTestUtils;
//    }
//
//    // byCategoryTest ------------------------------------------------
//    private List<TaskEntity> generateEntitiesForCategoryTest() {
//        List<TaskEntity> entities = new ArrayList<>();
//        entities.add(TaskEntity.builder()
//                .taskDef("Task1")
//                .priority(1)
//                .deadline("2017.01.10")
//                .category(TEST_CATEGORY)
//                .subTaskIds(null)
//                .repeating(false)
//                .build()
//        );
//        entities.add(TaskEntity.builder()
//                .taskDef("Task2")
//                .priority(2)
//                .deadline("2017.01.10")
//                .category(TEST_CATEGORY)
//                .subTaskIds(null)
//                .repeating(true)
//                .build()
//        );
//        entities.add(TaskEntity.builder()
//                .taskDef("Task3")
//                .priority(0)
//                .deadline("2017.02.10")
//                .category(TEST_CATEGORY)
//                .subTaskIds(null)
//                .repeating(false)
//                .build()
//        );
//        entities.add(TaskEntity.builder()
//                .taskDef("Task4")
//                .priority(1)
//                .deadline("2017.01.20")
//                .category("fun")
//                .subTaskIds(null)
//                .repeating(true)
//                .build()
//        );
//        entities.add(TaskEntity.builder()
//                .taskDef("Task5")
//                .priority(1)
//                .deadline("2017.01.20")
//                .category("uncategorized")
//                .subTaskIds(null)
//                .repeating(false)
//                .build()
//        );
//
//        return entities;
//    }
//
//    private List<TaskEntity> generateAndSaveEntitiesForCategoryTest() {
//        List<TaskEntity> entities = generateEntitiesForCategoryTest();
//        // Save entities and after they get a generated id, set relations
//        jdbcTestUtils.populateDatabase(entities);
//        entities.get(1).setSubTaskIds(Integer.toString(entities.get(2).getId()));
//        entities.get(0).setSubTaskIds(Integer.toString(entities.get(1).getId()));
//        jdbcTestUtils.populateDatabase(entities);       // Update records so two entities have sub tasks
//
//        return entities;
//    }
//
//    public List<TaskVo> generateAndPersistExpectedVosForCategoryTest() {
//        List<TaskEntity> entities = generateAndSaveEntitiesForCategoryTest();
//        // Create TaskVos out of the entities, including the relations
//        List<TaskVo> expectedVos = new ArrayList<>();
//        TaskVo mainTask = OldTaskEntityVoMapper.toStandaloneVo(entities.get(0));
//        TaskVo subTask = OldTaskEntityVoMapper.toStandaloneVo(entities.get(1));
//        mainTask.setSubTasks(Arrays.asList(subTask));
//        expectedVos.add(mainTask);
//
//        mainTask = subTask;
//        subTask = OldTaskEntityVoMapper.toStandaloneVo(entities.get(2));
//        mainTask.setSubTasks(Arrays.asList(subTask));
//
//        return expectedVos;
//    }
//    // /byCategoryTest -----------------------------------------------
//
//    // save and find all test ----------------------------------------
//    public List<TaskVo> generateVosToSave() {
//        List<TaskVo> standaloneVos = new ArrayList<>();
//        standaloneVos.add(
//                TaskVo.builder()
//                        .taskDef("Task1")
//                        .category("category1")
//                        .priority(Priority.NONE)
//                        .deadline(LocalDate.now().plusMonths(2))
//                        .repeating(false)
//                        .subTasks(null)
//                        .build()
//        );
//        standaloneVos.add(
//                TaskVo.builder()
//                        .taskDef("Task2")
//                        .category("category1")
//                        .priority(Priority.NONE)
//                        .deadline(LocalDate.now().plusMonths(2))
//                        .repeating(false)
//                        .subTasks(null)
//                        .build()
//        );
//        standaloneVos.add(
//                TaskVo.builder()
//                        .taskDef("Task3")
//                        .category("category1")
//                        .priority(Priority.NONE)
//                        .deadline(LocalDate.now().plusMonths(2))
//                        .repeating(false)
//                        .subTasks(null)
//                        .build()
//        );
//        standaloneVos.add(
//                TaskVo.builder()
//                        .taskDef("Task5")
//                        .category("category1")
//                        .priority(Priority.NONE)
//                        .deadline(LocalDate.now().plusMonths(2))
//                        .repeating(false)
//                        .subTasks(null)
//                        .build()
//        );
//
//        standaloneVos.add(
//                TaskVo.builder()
//                        .taskDef("Task4")
//                        .category("category2")
//                        .priority(Priority.MEDIUM)
//                        .deadline(LocalDate.now())
//                        .repeating(true)
//                        .subTasks(null)
//                        .build()
//        );
//
//        List<TaskVo> expectedVos = new ArrayList<>();
//        expectedVos.add(standaloneVos.get(0));
//        List<TaskVo> subTasks = new ArrayList<>();
//        subTasks.add(standaloneVos.get(1));
//        subTasks.add(standaloneVos.get(2));
//        subTasks.get(1).setSubTasks(Arrays.asList(standaloneVos.get(3)));
//        expectedVos.get(0).setSubTasks(subTasks);
//
//        expectedVos.add(standaloneVos.get(4));
//        return expectedVos;
//    }
//    // /save and find all test ---------------------------------------
//
//    // Today tasks test ----------------------------------------------
//    public List<TaskVo> generateVosForTodayTest() {
//        List<TaskVo> standaloneVos = new ArrayList<>();
//        standaloneVos.add(
//                TaskVo.builder()
//                        .taskDef("Task1")
//                        .category("category1")
//                        .priority(Priority.HIGH)
//                        .deadline(LocalDate.now())
//                        .repeating(false)
//                        .subTasks(null)
//                        .build()
//        );
//        standaloneVos.add(
//                TaskVo.builder()
//                        .taskDef("Task2")
//                        .category("category1")
//                        .priority(Priority.HIGH)
//                        .deadline(LocalDate.now())
//                        .repeating(false)
//                        .subTasks(null)
//                        .build()
//        );
//        standaloneVos.add(
//                TaskVo.builder()
//                        .taskDef("Task1")
//                        .category("category1")
//                        .priority(Priority.HIGH)
//                        .deadline(LocalDate.now().minusYears(2))
//                        .repeating(true)
//                        .subTasks(null)
//                        .build()
//        );
//        standaloneVos.add(
//                TaskVo.builder()
//                        .taskDef("Task1")
//                        .category("uncategorized")
//                        .priority(Priority.LOW)
//                        .deadline(LocalDate.now().plusWeeks(3))
//                        .repeating(false)
//                        .subTasks(null)
//                        .build()
//        );
//
//        List<TaskVo> allVos = new ArrayList<>();
//        allVos.add(standaloneVos.get(0));
//        allVos.get(0).setSubTasks(Arrays.asList(standaloneVos.get(1)));
//        allVos.add(standaloneVos.get(2));
//        allVos.add(standaloneVos.get(3));
//
//        return allVos;
//    }
//    // /Today tasks test ---------------------------------------------
//
//    // following days test -------------------------------------------
//    public List<TaskVo> generateVosForFollowingDaysTest() {
//        List<TaskVo> vos = new ArrayList<>();
//        vos.add(
//                TaskVo.builder()
//                        .taskDef("Task0")
//                        .category("in")
//                        .priority(Priority.HIGH)
//                        .deadline(LocalDate.now())
//                        .repeating(false)
//                        .subTasks(null)
//                        .build()
//        );
//        vos.add(
//                TaskVo.builder()
//                        .taskDef("Task1")
//                        .category("in-sub")
//                        .priority(Priority.HIGH)
//                        .deadline(LocalDate.now())
//                        .repeating(false)
//                        .subTasks(null)
//                        .build()
//        );
//        vos.add(
//                TaskVo.builder()
//                        .taskDef("Task2")
//                        .category("in")
//                        .priority(Priority.LOW)
//                        .deadline(LocalDate.now().minusDays(5))
//                        .repeating(true)
//                        .subTasks(null)
//                        .build()
//        );
//        vos.add(
//                TaskVo.builder()
//                        .taskDef("Task3")
//                        .category("in")
//                        .priority(Priority.HIGH)
//                        .deadline(LocalDate.now().plusDays(1))
//                        .repeating(false)
//                        .subTasks(null)
//                        .build()
//        );
//        vos.add(
//                TaskVo.builder()
//                        .taskDef("Task4")
//                        .category("in")
//                        .priority(Priority.NONE)
//                        .deadline(LocalDate.now().plusDays(7))
//                        .repeating(false)
//                        .subTasks(null)
//                        .build()
//        );
//        vos.add(
//                TaskVo.builder()
//                        .taskDef("Task5")
//                        .category("in")
//                        .priority(Priority.HIGH)
//                        .deadline(LocalDate.now().plusDays(10))
//                        .repeating(true)
//                        .subTasks(null)
//                        .build()
//        );
//        vos.add(
//                TaskVo.builder()
//                        .taskDef("Task6")
//                        .category("in")
//                        .priority(Priority.MEDIUM)
//                        .deadline(LocalDate.now().plusDays(7))
//                        .repeating(false)
//                        .subTasks(null)
//                        .build()
//        );
//        vos.add(
//                TaskVo.builder()
//                        .taskDef("Task7")
//                        .category("out")
//                        .priority(Priority.MEDIUM)
//                        .deadline(LocalDate.now().plusDays(8))
//                        .repeating(false)
//                        .subTasks(null)
//                        .build()
//        );
//        vos.add(
//                TaskVo.builder()
//                        .taskDef("Task8")
//                        .category("out")
//                        .priority(Priority.LOW)
//                        .deadline(LocalDate.now().plusDays(10))
//                        .repeating(false)
//                        .subTasks(null)
//                        .build()
//        );
//
//        vos.get(0).setSubTasks(Arrays.asList(vos.get(1)));
//        vos.remove(1);
//
//        return vos;
//    }
//    // /following days test ------------------------------------------
}
