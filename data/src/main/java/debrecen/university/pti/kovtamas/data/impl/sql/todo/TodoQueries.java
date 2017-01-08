package debrecen.university.pti.kovtamas.data.impl.sql.todo;

public class TodoQueries {

    public static final String FIND_ALL = "SELECT * FROM TASK";
    public static final String FIND_BY_ID = "SELECT * FROM TASK WHERE ID = ?";
    public static final String FIND_BY_CATEGORY = "SELECT * FROM TASK WHERE CATEGORY = ?";
    public static final String FIND_BY_NOT_CATEGORY = "SELECT * FROM TASK WHERE CATEGORY <> ?";
    public static final String INSERT_FULL = "INSERT INTO TASK "
            + "(TASK_DEF, PRIORITY, DEADLINE, CATEGORY, SUB_TASK_IDS, REPEATING) "
            + "VALUES(?, ?, ?, ?, ?, ?)";
    public static final String INSERT_NO_REPEATING = "INSERT INTO TASK "
            + "(TASK_DEF, PRIORITY, DEADLINE, CATEGORY, SUB_TASK_IDS) "
            + "VALUES(?, ?, ?, ?, ?)";

    public static String buildIdCollectionQuery(int num) {
        StringBuilder sb = new StringBuilder("SELECT * FROM TASK WHERE");

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
