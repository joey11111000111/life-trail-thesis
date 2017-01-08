package debrecen.university.pti.kovtamas.data.test;

import debrecen.university.pti.kovtamas.data.entity.todo.TodoEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.JdbcTodoRepository;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TodoRepository;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TodoRepositoryTest {

    private static TodoRepository repo;
    private static String saveDir = "/home/joey/BerkeleyDatabase";

    public TodoRepositoryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        clearSaveDir();
        repo = new JdbcTodoRepository();
    }

    @AfterClass
    public static void tearDownClass() {
        clearSaveDir();
    }

    private static void clearSaveDir() {
        File saveDirectory = new File(saveDir);
        File[] createdFiles = saveDirectory.listFiles();
        Arrays.stream(createdFiles).forEach(f -> f.delete());
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void findAllTest() {
        Set<TodoEntity> entities = repo.findAll();
        System.out.println(entities.size());
        System.out.println(entities);
    }

    private List<TodoEntity> generateEntities() {
        List<TodoEntity> entities = new ArrayList<>(4);
        entities.add(
                TodoEntity.builder()
                        .id(1)
                        .taskDef("Go to the gym")
                        .priority(1)
                        .deadline("2017.1.10")
                        .category("personal")
                        .subTaskIds(null)
                        .repeating(false)
                        .build()
        );
        entities.add(
                TodoEntity.builder()
                        .id(2)
                        .taskDef("Go shopping")
                        .priority(2)
                        .deadline("2017.1.12")
                        .category("everyday life")
                        .subTaskIds("3, 4")
                        .repeating(true)
                        .build()
        );
        entities.add(
                TodoEntity.builder()
                        .id(3)
                        .taskDef("Prepare the bike")
                        .priority(2)
                        .deadline("2017.1.12")
                        .category("everyday life")
                        .subTaskIds(null)
                        .repeating(null)
                        .build()
        );
        entities.add(
                TodoEntity.builder()
                        .id(4)
                        .taskDef("Lock the door")
                        .priority(1)
                        .deadline("2017.1.12")
                        .category("everyday life")
                        .subTaskIds(null)
                        .repeating(null)
                        .build()
        );

        return entities;
    }

}
