package debrecen.university.pti.kovtamas.data.impl.sql.todo.relations;

public final class TaskRelationStatements {

    private TaskRelationStatements() {
    }

    static private final String TABLE_NAME = "LIFE_TRAIL.TASK_RELATIONS";

    static public final String FIND_ALL = "SELECT * FROM " + TABLE_NAME;
    static public final String FIND_WHERE_PARENT_OR_CHILD = "SELECT * FROM " + TABLE_NAME
            + " WHERE PARENT_ID = ? OR CHILD_ID = ?";
    static public final String FIND_BY_PARENT_AND_CHILD = "SELECT * FROM " + TABLE_NAME
            + " WHERE PARENT_ID = ? AND CHILD_ID = ?";

    static public final String CLEAR = "DELETE FROM " + TABLE_NAME;
    static public final String REMOVE_BY_ID = "DELETE FROM " + TABLE_NAME + " WHERE ID = ?";
    static public final String REMOVE_WHERE_PARENT_OR_CHILD = "DELETE FROM " + TABLE_NAME
            + " WHERE PARENT_ID = ? OR CHILD_ID = ?";
    static public final String INSERT = "INSERT INTO " + TABLE_NAME
            + " (PARENT_ID, CHILD_ID) VALUES(?, ?)";

}
