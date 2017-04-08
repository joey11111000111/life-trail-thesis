package debrecen.university.pti.kovtamas.data.test.refactored.util;

import debrecen.university.pti.kovtamas.data.impl.sql.datasource.DataSourceManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;

import static org.junit.Assert.fail;

@Slf4j
public final class JdbcTestUtils {

    private JdbcTestUtils() {
    }

    static public void switchToTestTables() {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            renameProductionAndCreateTestTables(conn);
        } catch (SQLException sqle) {
            log.error("Could not switch to test tables!", sqle);
            fail();
        }
    }

    static private void renameProductionAndCreateTestTables(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        String[] renameStatements = getAllRenameToBackupStatementsOrdered();
        String[] createStatements = getAllCreateStatementsOrdered();

        final int tableCount = 3;
        for (int i = 0; i < tableCount; i++) {
            statement.executeUpdate(renameStatements[i]);
            statement.executeUpdate(createStatements[i]);
        }
    }

    static private String[] getAllRenameToBackupStatementsOrdered() {
        return new String[]{
            JdbcTestQueries.RENAME_TO_BACKUP_CATEGORY_TABLE,
            JdbcTestQueries.RENAME_TO_BACKUP_TASK_TABLE,
            JdbcTestQueries.RENAME_TO_BACKUP_RELATIONS_TABLE
        };
    }

    static private String[] getAllCreateStatementsOrdered() {
        return new String[]{
            JdbcTestQueries.CREATE_TEST_TABLE_CATEGORY,
            JdbcTestQueries.CREATE_TEST_TABLE_TASK,
            JdbcTestQueries.CREATE_TEST_TABLE_RELATIONS
        };
    }

    static public void switchToProductionTables() {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            dropTestAndRenameProductionTables(conn);
        } catch (SQLException sqle) {
            log.error("Could not switch to production tables!", sqle);
            fail();
        }
    }

    static private void dropTestAndRenameProductionTables(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
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
            JdbcTestQueries.RENAME_TO_ORIGINAL_RELATIONS_TABLE,
            JdbcTestQueries.RENAME_TO_ORIGINAL_TASK_TABLE,
            JdbcTestQueries.RENAME_TO_ORIGINAL_CATEGORY_TABLE
        };
    }

    static private String[] getAllDropTableStatementsOrdered() {
        return new String[]{
            JdbcTestQueries.DROP_TABLE_RELATIONS,
            JdbcTestQueries.DROP_TABLE_TASK,
            JdbcTestQueries.DROP_TABLE_CATEGORY
        };
    }

}
