package debrecen.university.pti.kovtamas.data.impl.sql.todo.category;

public class CategoryQueries {

    static private final String TABLE_NAME = "LIFE_TRAIL.CATEGORY";

    static public final String CLEAR_TABLE = "DELETE FROM " + TABLE_NAME;
    static public final String REMOVE_BY_ID = "DELETE FROM " + TABLE_NAME + " WHERE ID = ?";
    static public final String REMOVE_BY_NAME = "DELETE FROM " + TABLE_NAME + " WHERE NAME = ?";

    static public final String FIND_ALL = "SELECT * FROM " + TABLE_NAME;
    static public final String FIND_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE ID = ?";
    static public final String FIND_BY_NAME = "SELECT * FROM " + TABLE_NAME + " WHERE NAME = ?";

    static public final String INSERT = "INSERT INTO " + TABLE_NAME + " (NAME) VALUES(?)";
    static public final String UPDATE = "UPDATE " + TABLE_NAME + " SET NAME = ? WHERE ID = ?";

}
