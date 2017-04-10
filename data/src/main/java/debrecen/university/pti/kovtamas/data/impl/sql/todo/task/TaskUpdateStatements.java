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
}
