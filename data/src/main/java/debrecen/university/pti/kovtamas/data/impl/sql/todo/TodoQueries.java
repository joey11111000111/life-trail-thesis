package debrecen.university.pti.kovtamas.data.impl.sql.todo;

public class TodoQueries {

    public static final String FIND_ALL = "SELECT * FROM TASK";
    public static final String FIND_BY_ID = "SELECT * FROM TASK WHERE ID = ?";
    public static final String FIND_BY_ID_LIST = "SELECT * FROM TASK WHERE ID IN ?";
    public static final String FIND_BY_CATEGORY = "SELECT * FROM TASK WHERE CATEGORY = ?";
    public static final String INSERT = "INSERT INTO TASK "
            + "(TASK_DEF, PRIORITY, DEADLINE, CATEGORY, SUB_TASK_IDS, REPEATING) "
            + "VALUES(?, ?, ?, ?, ?, ?)";

}
