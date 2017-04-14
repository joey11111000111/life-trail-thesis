package debrecen.university.pti.kovtamas.todo.service.integration;

public class DataServiceIntegrationTest {
//
//    private static final Logger LOG = LoggerFactory.getLogger(DataServiceIntegrationTest.class);
//
//    private static final TodoRepository REPO;
//    private static final DateTimeFormatter DATE_FORMAT;
//    private static final JdbcTestUtils JDBC_TEST_UTILS;
//    private static final TestDataGenerator DATA_GENERATOR;
//
//    private static final TodoService SERVICE;
//
//    static {
//        SERVICE = new TodoServiceImpl();
//        DATE_FORMAT = OldTaskEntityVoMapper.getDateFormat();
//        REPO = new JdbcTodoRepository(DATE_FORMAT);
//        JDBC_TEST_UTILS = new JdbcTestUtils(REPO);
//        DATA_GENERATOR = new TestDataGenerator(JDBC_TEST_UTILS);
//    }
//
//    public DataServiceIntegrationTest() {
//    }
//
//    @BeforeClass
//    public static void switchToTestTable() {
//        JDBC_TEST_UTILS.switchToTestTable();
//    }
//
//    @AfterClass
//    public static void switchToOriginalTable() {
//        JDBC_TEST_UTILS.switchToOriginalTable();
//    }
//
//    @After
//    public void cleanTestTable() {
//        JDBC_TEST_UTILS.cleanTestTable();
//    }
//
//    @Test
//    public void getByCategoryTest() {
//        List<TaskVo> expectedVos = DATA_GENERATOR.generateAndPersistExpectedVosForCategoryTest();
//        List<TaskVo> results = SERVICE.getActiveByCategory("personal");
//
//        voListEquals(expectedVos, results);
//    }
//
////    @Test
////    public void saveAllAndFindAllTest() {
////        List<TaskVo> expectedVos = DATA_GENERATOR.generateVosToSave();
////        populateDatabase(expectedVos);
////
////        List<TaskVo> results = SERVICE.getAll();
////        expectedVos.forEach(System.out::println);
////        System.out.println();
////        results.forEach(System.out::println);
////        System.out.println();
////        voListEquals(expectedVos, results);
////    }
//    @Test
//    public void deleteAllTest() {
//        List<TaskVo> allVos = DATA_GENERATOR.generateVosToSave();
//        populateDatabase(allVos);
//
//        try {
//            SERVICE.deleteAll(allVos);
//        } catch (TaskDeletionException tde) {
//            String message = "Could not delete tasks!";
//            LOG.error(message, tde);
//            fail(message);
//        }
//    }
//
//    @Test
//    public void getTodayTasksTest() {
//        List<TaskVo> allVos = DATA_GENERATOR.generateVosForTodayTest();
//        populateDatabase(allVos);
//
//        List<TaskVo> expectedVos = allVos.stream()
//                .filter(vo -> vo.getDeadline().equals(LocalDate.now()) || vo.isRepeating())
//                .collect(Collectors.toList());
//
//        List<TaskVo> results = SERVICE.getTodayTasks();
//        voListEquals(expectedVos, results);
//    }
//
//    @Test
//    public void ofFollowingDaysTest() {
//        List<TaskVo> vos = DATA_GENERATOR.generateVosForFollowingDaysTest();
//        populateDatabase(vos);
//
//        List<TaskVo> expectedVos = vos.stream()
//                .filter(vo -> !"out".equals(vo.getCategory()))
//                .collect(Collectors.toList());
//
//        List<TaskVo> results = SERVICE.getTasksOfFollowingDays(7);
//        voListEquals(expectedVos, results);
//    }
//
//    private void populateDatabase(List<TaskVo> vos) {
//        try {
//            SERVICE.saveAll(vos);
//        } catch (TaskSaveFailureException tsfe) {
//            String message = "Could not save all tasks!";
//            LOG.error(message, tsfe);
//            fail(message);
//        }
//    }
//
}
