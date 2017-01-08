package debrecen.university.pti.kovtamas.data.impl;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BerkeleyConfig {

    private static final Logger LOG = LoggerFactory.getLogger(BerkeleyConfig.class);

    private static final Set<String> ALL_BERKELEY_SAVE_DIRS;
    private static final String CLASS_CATALOG_STORE = "java_class_catalog";

    static {
        ALL_BERKELEY_SAVE_DIRS = new HashSet<>();
    }

    public static BerkeleyConfig createConfig(@NonNull String saveDir) throws IOException {
        if (ALL_BERKELEY_SAVE_DIRS.contains(saveDir)) {
            String exceptionMessage = "The save directory " + saveDir + " is already in use!";
            LOG.warn(exceptionMessage);
            throw new IllegalArgumentException(exceptionMessage);
        }

        File saveDirectory = new File(saveDir);
        if (!saveDirectory.exists() || saveDirectory.isFile()) {
            String exceptionMessage = "The save directory " + saveDir + " does not exist or is a file!";
            LOG.error(exceptionMessage);
            throw new IOException(exceptionMessage);
        }

        ALL_BERKELEY_SAVE_DIRS.add(saveDir);
        return new BerkeleyConfig(saveDirectory);
    }

    @Getter
    private final Environment environment;
    @Getter
    private final StoredClassCatalog javaCatalog;

    private BerkeleyConfig(File saveDirectory) {
        // init environment
        EnvironmentConfig config = new EnvironmentConfig();
        config.setTransactionalVoid(true);
        config.setAllowCreateVoid(true);

        environment = new Environment(saveDirectory, config);
        LOG.info("Environment was successfully created");

        // init catalog
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setTransactionalVoid(true);
        dbConfig.setAllowCreateVoid(true);

        Database javaDb = environment.openDatabase(null, CLASS_CATALOG_STORE, dbConfig);
        javaCatalog = new StoredClassCatalog(javaDb);
        LOG.info("Java catalog was successfully created");
    }

    public void close() {
        javaCatalog.close();
        environment.close();
        LOG.debug("The java catalog and the environment was closed");
    }

}
