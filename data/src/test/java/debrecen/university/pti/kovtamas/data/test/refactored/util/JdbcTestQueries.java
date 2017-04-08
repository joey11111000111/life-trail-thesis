package debrecen.university.pti.kovtamas.data.test.refactored.util;

public class JdbcTestQueries {

    // Table names
    static public final String ORIGINAL_CATEGORY_TABLE_NAME = "LIFE_TRAIL.CATEGORY";
    static public final String ORIGINAL_TASK_TABLE_NAME = "LIFE_TRAIL.TASK";
    static public final String ORIGINAL_RELATIONS_TABLE_NAME = "LIFE_TRAIL.TASK_RELATIONS";

    static public final String TEMP_CATEGORY_TABLE_NAME = "LIFE_TRAIL.BACKUP_CATEGORY";
    static public final String TEMP_TASK_TABLE_NAME = "LIFE_TRAIL.BACKUP_TASK";
    static public final String TEMP_RELATIONS_TABLE_NAME = "LIFE_TRAIL.BACKUP_TASK_RELATIONS";

    // Create test tables
    static public final String CREATE_TEST_TABLE_CATEGORY = "CREATE TABLE LIFE_TRAIL.CATEGORY (\n"
            + "	ID SERIAL PRIMARY KEY,\n"
            + "	NAME VARCHAR(100) NOT NULL,\n"
            + "	UNIQUE (NAME)\n"
            + ");";

    static public final String CREATE_TEST_TABLE_TASK = "CREATE TABLE LIFE_TRAIL.TASK (\n"
            + "	ID SERIAL PRIMARY KEY,\n"
            + "	CATEGORY_ID BIGINT UNSIGNED DEFAULT NULL,\n"
            + "	TASK_DEF TEXT NOT NULL,\n"
            + "	PRIORITY INTEGER NOT NULL,\n"
            + "	DEADLINE DATE DEFAULT NULL,\n"
            + "	COMPLETED VARCHAR(5) DEFAULT \"FALSE\",\n"
            + "	FOREIGN KEY (CATEGORY_ID) REFERENCES LIFE_TRAIL.CATEGORY (ID)\n"
            + ");";

    static public final String CREATE_TEST_TABLE_RELATIONS = "CREATE TABLE LIFE_TRAIL.TASK_RELATIONS (\n"
            + "	ID SERIAL PRIMARY KEY,\n"
            + "	PARENT_ID BIGINT UNSIGNED NOT NULL,\n"
            + "	CHILD_ID BIGINT UNSIGNED NOT NULL,\n"
            + "	FOREIGN KEY (PARENT_ID) REFERENCES LIFE_TRAIL.TASK (ID),\n"
            + "	FOREIGN KEY (CHILD_ID) REFERENCES LIFE_TRAIL.TASK (ID),\n"
            + "	UNIQUE (CHILD_ID)\n"
            + ");";

    // Rename original tables for the tests
    static public final String RENAME_TO_BACKUP_CATEGORY_TABLE = "RENAME TABLE "
            + ORIGINAL_CATEGORY_TABLE_NAME + " TO " + TEMP_CATEGORY_TABLE_NAME;
    static public final String RENAME_TO_BACKUP_TASK_TABLE = "RENAME TABLE "
            + ORIGINAL_TASK_TABLE_NAME + " TO " + TEMP_TASK_TABLE_NAME;
    static public final String RENAME_TO_BACKUP_RELATIONS_TABLE = "RENAME TABLE "
            + ORIGINAL_RELATIONS_TABLE_NAME + " TO " + TEMP_RELATIONS_TABLE_NAME;

    static public final String RENAME_TO_ORIGINAL_CATEGORY_TABLE = "RENAME TABLE "
            + TEMP_CATEGORY_TABLE_NAME + " TO " + ORIGINAL_CATEGORY_TABLE_NAME;
    static public final String RENAME_TO_ORIGINAL_TASK_TABLE = "RENAME TABLE "
            + TEMP_TASK_TABLE_NAME + " TO " + ORIGINAL_TASK_TABLE_NAME;
    static public final String RENAME_TO_ORIGINAL_RELATIONS_TABLE = "RENAME TABLE "
            + TEMP_RELATIONS_TABLE_NAME + " TO " + ORIGINAL_RELATIONS_TABLE_NAME;

    // Drop test tables
    static public final String DROP_TABLE_RELATIONS = "DROP TABLE " + ORIGINAL_RELATIONS_TABLE_NAME;
    static public final String DROP_TABLE_TASK = "DROP TABLE " + ORIGINAL_TASK_TABLE_NAME;
    static public final String DROP_TABLE_CATEGORY = "DROP TABLE " + ORIGINAL_CATEGORY_TABLE_NAME;

}
