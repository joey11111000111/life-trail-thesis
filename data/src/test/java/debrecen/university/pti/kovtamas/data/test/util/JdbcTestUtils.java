package debrecen.university.pti.kovtamas.data.test.util;

import debrecen.university.pti.kovtamas.data.impl.sql.datasource.DatabaseConnector;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;

import static org.junit.Assert.fail;

@Slf4j
public final class JdbcTestUtils {

    static private final DatabaseConnector CONNECTOR;

    static public enum TestType {
        UNIT, INTEGRATION
    }

    static {
        CONNECTOR = DatabaseConnector.getInstance();
    }

    private JdbcTestUtils() {
    }

    static public void switchToTestTables(TestType testType) {
        try {
            renameProductionAndCreateTestTables(testType);
        } catch (SQLException sqle) {
            log.error("Could not switch to test tables!", sqle);
            fail();
        }
    }

    static private void renameProductionAndCreateTestTables(TestType testType) throws SQLException {
        Statement statement = CONNECTOR.createStatement();
        String[] renameStatements = getAllRenameToBackupStatementsOrdered();
        String[] createStatements = getAllCreateStatementsOrdered(testType);

        final int tableCount = 3;
        for (int i = 0; i < tableCount; i++) {
            statement.executeUpdate(renameStatements[i]);
            statement.executeUpdate(createStatements[i]);
        }
    }

    static private String[] getAllRenameToBackupStatementsOrdered() {
        return new String[]{
            JdbcTestStatements.RENAME_TO_BACKUP_CATEGORY_TABLE,
            JdbcTestStatements.RENAME_TO_BACKUP_TASK_TABLE,
            JdbcTestStatements.RENAME_TO_BACKUP_RELATIONS_TABLE
        };
    }

    static private String[] getAllCreateStatementsOrdered(TestType testType) {
        if (testType == TestType.INTEGRATION) {
            return new String[]{
                JdbcTestStatements.CREATE_TEST_TABLE_CATEGORY,
                JdbcTestStatements.CREATE_INTEGRATION_TEST_TABLE_TASK,
                JdbcTestStatements.CREATE_INTEGRATION_TEST_TABLE_RELATIONS
            };

        } else {
            return new String[]{
                JdbcTestStatements.CREATE_TEST_TABLE_CATEGORY,
                JdbcTestStatements.CREATE_UNIT_TEST_TABLE_TASK,
                JdbcTestStatements.CREATE_UNIT_TEST_TABLE_RELATIONS
            };

        }
    }

    static public void switchToProductionTables() {
        try {
            dropTestAndRenameProductionTables();
        } catch (SQLException sqle) {
            log.error("Could not switch to production tables!", sqle);
            fail();
        }
    }

    static private void dropTestAndRenameProductionTables() throws SQLException {
        Statement statement = CONNECTOR.createStatement();
        String[] dropStatements = getAllDropTableStatementsOrdered();
        String[] renameStatements = getAllRenameToOriginalStatementsOrdered();

        final int tableCount = 3;
        for (int i = 0; i < tableCount; i++) {
            statement.executeUpdate(dropStatements[i]);
            statement.executeUpdate(renameStatements[i]);
        }
    }

    static private String[] getAllRenameToOriginalStatementsOrdered() {
        return new String[]{
            JdbcTestStatements.RENAME_TO_ORIGINAL_RELATIONS_TABLE,
            JdbcTestStatements.RENAME_TO_ORIGINAL_TASK_TABLE,
            JdbcTestStatements.RENAME_TO_ORIGINAL_CATEGORY_TABLE
        };
    }

    static private String[] getAllDropTableStatementsOrdered() {
        return new String[]{
            JdbcTestStatements.DROP_TABLE_RELATIONS,
            JdbcTestStatements.DROP_TABLE_TASK,
            JdbcTestStatements.DROP_TABLE_CATEGORY
        };
    }

}
