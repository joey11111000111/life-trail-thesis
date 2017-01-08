package debrecen.university.pti.kovtamas.data.impl.inmemory.todo;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import debrecen.university.pti.kovtamas.data.impl.inmemory.BerkeleyConfig;
import java.io.IOException;
import lombok.Getter;

class BerkeleyDatabase {

    private static final String TASK_STORE = "todo_store";

    @Getter
    private final BerkeleyConfig config;
    @Getter
    private final Database todoEntityDb;

    public BerkeleyDatabase(String saveDir) throws IOException {
        config = BerkeleyConfig.createConfig(saveDir);

        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setTransactionalVoid(true);
        dbConfig.setAllowCreateVoid(true);

        todoEntityDb = config.getEnvironment().openDatabase(null, TASK_STORE, dbConfig);
    }

    public void close() {
        todoEntityDb.close();
        config.close();
    }

}
