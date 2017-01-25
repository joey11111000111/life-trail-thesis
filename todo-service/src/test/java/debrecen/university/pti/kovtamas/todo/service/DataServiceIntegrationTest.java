package debrecen.university.pti.kovtamas.todo.service;

import debrecen.university.pti.kovtamas.data.impl.sql.todo.JdbcTodoRepository;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TodoRepository;
import debrecen.university.pti.kovtamas.data.test.JdbcTestUtils;
import java.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataServiceIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(DataServiceIntegrationTest.class);

    private static final TodoRepository REPO;
    private static final DateTimeFormatter DATE_FORMAT;
    private static final JdbcTestUtils JDBC_TEST_UTILS;

    static {
        DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        REPO = new JdbcTodoRepository(DATE_FORMAT);
        JDBC_TEST_UTILS = new JdbcTestUtils(REPO);
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

}
