package debrecen.university.pti.kovtamas.todo.service.integration;

import debrecen.university.pti.kovtamas.data.impl.sql.todo.JdbcTodoRepository;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TodoRepository;
import debrecen.university.pti.kovtamas.data.test.JdbcTestUtils;
import debrecen.university.pti.kovtamas.todo.service.api.TodoService;
import debrecen.university.pti.kovtamas.todo.service.impl.CachingTodoService;
import debrecen.university.pti.kovtamas.todo.service.mapper.TaskEntityVoMapper;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static debrecen.university.pti.kovtamas.todo.service.CollectionAssert.voListEquals;

public class DataServiceIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(DataServiceIntegrationTest.class);

    private static final TodoRepository REPO;
    private static final DateTimeFormatter DATE_FORMAT;
    private static final JdbcTestUtils JDBC_TEST_UTILS;
    private static final TestDataGenerator DATA_GENERATOR;

    private static final TodoService SERVICE;

    static {
        SERVICE = new CachingTodoService();
        DATE_FORMAT = TaskEntityVoMapper.getDateFormat();
        REPO = new JdbcTodoRepository(DATE_FORMAT);
        JDBC_TEST_UTILS = new JdbcTestUtils(REPO);
        DATA_GENERATOR = new TestDataGenerator(JDBC_TEST_UTILS);
    }

    public DataServiceIntegrationTest() {
    }

    @BeforeClass
    public static void switchToTestTable() {
        JDBC_TEST_UTILS.switchToTestTable();
    }

    @AfterClass
    public static void switchToOriginalTable() {
        JDBC_TEST_UTILS.switchToOriginalTable();
    }

    @After
    public void cleanTestTable() {
        JDBC_TEST_UTILS.cleanTestTable();
    }

    @Test
    public void getByCategoryTest() {
        List<TaskVo> expectedVos = DATA_GENERATOR.generateAndPersistExpectedVosForCategoryTest();
        List<TaskVo> results = SERVICE.getByCategory("personal");

        voListEquals(expectedVos, results);
    }

}
