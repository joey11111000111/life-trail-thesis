package debrecen.university.pti.kovtamas.data.test.refactored.task;

import debrecen.university.pti.kovtamas.data.test.refactored.util.JdbcTestUtils;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.task.JdbcTaskRepository;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

@Slf4j
public class JdbcTaskRepositoryQueriesTest {

    private final TaskRepository taskRepo;

    @BeforeClass
    static public void switchToTestTables() {
        JdbcTestUtils.switchToTestTables();
    }

    @AfterClass
    static public void switchToProductionTables() {
        JdbcTestUtils.switchToProductionTables();
    }

    public JdbcTaskRepositoryQueriesTest() {
        taskRepo = JdbcTaskRepository.getInstance();
    }

//    @Test
//    public void findAllTest() {
//    }
}
