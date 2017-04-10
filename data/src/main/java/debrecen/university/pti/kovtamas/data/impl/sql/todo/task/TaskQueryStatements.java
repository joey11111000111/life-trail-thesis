package debrecen.university.pti.kovtamas.data.impl.sql.todo.task;

public final class TaskQueryStatements {

    private TaskQueryStatements() {
    }

    static private final String TABLE_NAME = "LIFE_TRAIL.TASK";

    static public final String FIND_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE ID = ?";
    static public final String FIND_TODAY_AND_ACTIVE_PAST = "SELECT * FROM " + TABLE_NAME
            + " WHERE DEADLINE = ? OR (DEADLINE < ? AND COMPLETED = \"FALSE\")";
    static public final String FIND_ACTIVE_BY_CATEGORY = "SELECT * FROM " + TABLE_NAME
            + " WHERE CATEGORY_ID = ? AND COMPLETED = FALSE";
    static public final String FIND_COMPLETED = "SELECT * FROM " + TABLE_NAME + " WHERE COMPLETED = \"TRUE\"";
    static public final String FIND_ACTIVE_BETWEEN = "SELECT * FROM " + TABLE_NAME
            + " WHERE DEADLINE BETWEEN ? AND ? AND COMPLETED = \"FALSE\"";
    static public final String ROW_COUNT = "SELECT COUNT(*) FROM " + TABLE_NAME;
}
