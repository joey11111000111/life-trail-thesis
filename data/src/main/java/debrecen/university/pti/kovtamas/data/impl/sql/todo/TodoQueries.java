package debrecen.university.pti.kovtamas.data.impl.sql.todo;

public class TodoQueries {

    private static final String TASK_TABLE_NAME = "LIFE_TRAIL.TASK";

    public static final String FIND_ALL = "SELECT * FROM " + TASK_TABLE_NAME;
    public static final String FIND_BY_ID = "SELECT * FROM " + TASK_TABLE_NAME + " WHERE ID = ?";
    public static final String FIND_BY_CATEGORY = "SELECT * FROM " + TASK_TABLE_NAME + " WHERE CATEGORY = ?";
    public static final String FIND_BY_NOT_CATEGORY = "SELECT * FROM " + TASK_TABLE_NAME + " WHERE CATEGORY <> ?";
    public static final String FIND_TODAY_TASKS = "SELECT * FROM " + TASK_TABLE_NAME
            + " WHERE DEADLINE = ? OR REPEATING = 'TRUE'";
    public static final String FIND_TASKS_BETWEEN_DATE = "SELECT * FROM " + TASK_TABLE_NAME
            + " WHERE DEADLINE BETWEEN ? AND ?";

    public static final String INSERT = "INSERT INTO " + TASK_TABLE_NAME
            + " (TASK_DEF, PRIORITY, DEADLINE, CATEGORY, SUB_TASK_IDS, REPEATING)"
            + " VALUES(?, ?, ?, ?, ?, ?)";
    public static final String UPDATE = "UPDATE " + TASK_TABLE_NAME
            + " SET TASK_DEF = ?, PRIORITY = ?, DEADLINE = ?, CATEGORY = ?, SUB_TASK_IDS = ?, REPEATING = ?"
            + " WHERE ID = ?";

    public static final String REMOVE_BY_ID = "DELETE FROM " + TASK_TABLE_NAME + " WHERE ID = ?";
    public static final String CLEAN_TABLE = "TRUNCATE " + TASK_TABLE_NAME;

    public static final String GET_ROW_COUNT = "SELECT COUNT(1) AS ROW_COUNT FROM " + TASK_TABLE_NAME;

    public static String buildIdCollectionQuery(int num) {
        StringBuilder sb = new StringBuilder("SELECT * FROM " + TASK_TABLE_NAME + " WHERE");

        for (int i = 0; i < num; i++) {
            sb.append(" ID = ?");
            if (i == num - 1) {
                break;
            }
            sb.append(" OR");
        }

        return sb.toString();
    }
}
