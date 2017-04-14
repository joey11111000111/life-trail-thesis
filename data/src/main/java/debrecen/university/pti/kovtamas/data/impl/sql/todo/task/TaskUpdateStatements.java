package debrecen.university.pti.kovtamas.data.impl.sql.todo.task;

public final class TaskUpdateStatements {

    private TaskUpdateStatements() {
    }

    static private final String TABLE_NAME = "LIFE_TRAIL.TASK";

    static public final String INSERT = "INSERT INTO " + TABLE_NAME
            + " (CATEGORY_ID, TASK_DEF, PRIORITY, DEADLINE, COMPLETED)"
            + " VALUES(?, ?, ?, ?, ?)";
    static public final String UPDATE = "UPDATE " + TABLE_NAME
            + " SET CATEGORY_ID = ?, TASK_DEF = ?, PRIORITY = ?, DEADLINE = ?, COMPLETED = ?"
            + " WHERE ID = ?";
    static public final String REMOVE_BY_ID = "DELETE FROM " + TABLE_NAME + " WHERE ID = ?";
    static public final String SET_NULL_CATEGORY_WHERE = "UPDATE " + TABLE_NAME
            + " SET CATEGORY_ID = NULL WHERE CATEGORY_ID = ?";
    static public final String CLEAR = "DELETE FROM " + TABLE_NAME;
}
