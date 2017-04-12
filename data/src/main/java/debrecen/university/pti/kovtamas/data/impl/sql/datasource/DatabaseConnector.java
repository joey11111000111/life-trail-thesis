package debrecen.university.pti.kovtamas.data.impl.sql.datasource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseConnector {

    static public final boolean RETURN_GENERATED_KEYS = true;
    static private final DatabaseConnector INSTANCE;

    private final Connection connection;
    private final List<AutoCloseable> operationBuffer;

    static {
        INSTANCE = new DatabaseConnector();
    }

    static public DatabaseConnector getInstance() {
        return INSTANCE;
    }

    private DatabaseConnector() {
        MysqlDataSource ds = createDateSource();
        connection = createConnection(ds);
        operationBuffer = new ArrayList<>();
    }

    private MysqlDataSource createDateSource() {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName("localhost");
        ds.setDatabaseName("LIFE_TRAIL");
        ds.setPortNumber(3306);
        ds.setUser("life-trail");
        ds.setPassword("life-trail");

        return ds;
    }

    private Connection createConnection(MysqlDataSource ds) {
        try {
            return ds.getConnection();
        } catch (SQLException sqle) {
            throw new IllegalStateException("Failed to create database connection", sqle);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        connection.close();
    }

    public Statement createStatement() throws SQLException {
        Statement statement = connection.createStatement();
        operationBuffer.add(statement);
        return statement;
    }

    public PreparedStatement prepareStatement(String sqlStatement) throws SQLException {
        return prepareStatement(sqlStatement, false);
    }

    public PreparedStatement prepareStatement(String sqlStatement, boolean returnKeys) throws SQLException {
        PreparedStatement statement = (returnKeys)
                ? connection.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS)
                : connection.prepareStatement(sqlStatement);
        operationBuffer.add(statement);
        return statement;
    }

    public ResultSet executeQuery(Statement statement, String sqlStatement) throws SQLException {
        ResultSet resultSet = statement.executeQuery(sqlStatement);
        operationBuffer.add(resultSet);
        return resultSet;
    }

    public ResultSet executeQuery(PreparedStatement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery();
        operationBuffer.add(resultSet);
        return resultSet;
    }

    public ResultSet getGeneratedKeys(PreparedStatement statement) throws SQLException {
        ResultSet keys = statement.getGeneratedKeys();
        operationBuffer.add(keys);
        return keys;
    }

    public void finishedOperations() {
        if (!operationBuffer.isEmpty()) {
            closeAllBufferedOperations();
            operationBuffer.clear();
        }
    }

    private void closeAllBufferedOperations() {
        for (AutoCloseable closeable : operationBuffer) {
            try {
                closeable.close();
            } catch (Exception e) {
                log.warn("Failed to close autoclosable database operation", e);
            }
        }
    }

}
