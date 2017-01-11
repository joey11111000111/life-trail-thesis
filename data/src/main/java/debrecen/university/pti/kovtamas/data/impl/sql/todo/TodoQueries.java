package debrecen.university.pti.kovtamas.data.impl.sql.todo;

public class TodoQueries {

    private static final String TASK_TABLE_NAME = "LIFE_TRAIL.TASK";

    public static final String FIND_ALL = "SELECT * FROM " + TASK_TABLE_NAME;
    public static final String FIND_BY_ID = "SELECT * FROM " + TASK_TABLE_NAME + " WHERE ID = ?";
    public static final String FIND_BY_CATEGORY = "SELECT * FROM " + TASK_TABLE_NAME + " WHERE CATEGORY = ?";
    public static final String FIND_BY_NOT_CATEGORY = "SELECT * FROM " + TASK_TABLE_NAME + " WHERE CATEGORY <> ?";
    public static final String INSERT_FULL = "INSERT INTO " + TASK_TABLE_NAME
            + " (TASK_DEF, PRIORITY, DEADLINE, CATEGORY, SUB_TASK_IDS, REPEATING)"
            + " VALUES(?, ?, ?, ?, ?, ?)";
    public static final String INSERT_NO_REPEATING = "INSERT INTO " + TASK_TABLE_NAME
            + " (TASK_DEF, PRIORITY, DEADLINE, CATEGORY, SUB_TASK_IDS)"
            + " VALUES(?, ?, ?, ?, ?)";

    public static final String REMOVE_BY_ID = "DELETE FROM " + TASK_TABLE_NAME + " WHERE ID = ?";

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
