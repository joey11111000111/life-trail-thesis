package debrecen.university.pti.kovtamas.data.impl.sql.datasource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import javax.sql.DataSource;

public class DataSourceManager {

    private static final DataSource dataSource;

    static {
        MysqlDataSource ds = new MysqlDataSource();

        ds.setServerName("localhost");
        ds.setDatabaseName("LIFE_TRAIL");
        ds.setPortNumber(3306);
        ds.setUser("life-trail");
        ds.setPassword("life-trail");

        dataSource = ds;
    }

    private DataSourceManager() {
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

}
