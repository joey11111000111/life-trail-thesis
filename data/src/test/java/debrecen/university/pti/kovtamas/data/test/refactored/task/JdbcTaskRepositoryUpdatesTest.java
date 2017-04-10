package debrecen.university.pti.kovtamas.data.test.refactored.task;

import debrecen.university.pti.kovtamas.data.impl.sql.todo.task.JdbcTaskRepositoryUpdates;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepositoryUpdates;
import debrecen.university.pti.kovtamas.data.test.refactored.util.JdbcTestUtils;

public class JdbcTaskRepositoryUpdatesTest {

    private final TaskRepositoryUpdates taskUpdates;

    public JdbcTaskRepositoryUpdatesTest() {
        taskUpdates = JdbcTaskRepositoryUpdates.getInstance();
    }

//    @BeforeClass
    static public void switchToTestTables() {
        JdbcTestUtils.switchToTestTables(JdbcTestUtils.TestType.UNIT);
    }

//    @AfterClass
    static public void switchToProductionTables() {
        JdbcTestUtils.switchToProductionTables();
    }

}
