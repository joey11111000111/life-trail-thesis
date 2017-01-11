package debrecen.university.pti.kovtamas.data.test;

public class JdbcTestQueries {

    // Table names
    private static final String NEW_NAME = "LIFE_TRAIL.TASK_BACKUP";
    private static final String ORIGINAL_NAME = "LIFE_TRAIL.TASK";

    // Create empty test table and drop test table
    public static final String CREATE_TEST_TABLE = "CREATE TABLE " + ORIGINAL_NAME
            + " AS SELECT * FROM " + NEW_NAME + " WHERE ID <> ID";
    public static final String DROP_TEST_TABLE = "DROP TABLE " + ORIGINAL_NAME;

    // Rename back-and-forth the original table
    public static final String RENAME_ORIGINAL_TABLE = "RENAME TABLE " + ORIGINAL_NAME + " TO " + NEW_NAME;
    public static final String RESTORE_ORIGINAL_TABLE = "RENAME TABLE " + NEW_NAME + " TO " + ORIGINAL_NAME;

}
