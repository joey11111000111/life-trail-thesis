package debrecen.university.pti.kovtamas.todo.service.mapper;

import debrecen.university.pti.kovtamas.data.entity.todo.CategoryEntity;
import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.category.JdbcCategoryRepository;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.CategorySaveFailureException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.CategoryRepository;
import debrecen.university.pti.kovtamas.general.util.TreeNode;
import debrecen.university.pti.kovtamas.todo.service.vo.CategoryVo;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TaskEntityVoMapperTest {

    static private CategoryRepository categoryRepo;
    static private TestDataGenerator testDataGenerator;
    static private CategoryVo tempCategoryVo;

    @BeforeClass
    static public void setup() throws CategorySaveFailureException {
        categoryRepo = JdbcCategoryRepository.getInstance();
        tempCategoryVo = createAndSaveTempCategoryVo();
        testDataGenerator = new TestDataGenerator(tempCategoryVo);
    }

    static private CategoryVo createAndSaveTempCategoryVo() throws CategorySaveFailureException {
        CategoryEntity categoryEntity = new CategoryEntity("TempCategory,WillBeRemovedAfterTest", 999999);
        categoryEntity = categoryRepo.saveOrUpdate(categoryEntity);

        return CategoryVo.builder()
                .id(categoryEntity.getId())
                .name(categoryEntity.getName())
                .displayIndex(categoryEntity.getDisplayIndex())
                .build();
    }

    @AfterClass
    static public void deleteTestCategory() {
        categoryRepo.remove(tempCategoryVo.getId());
    }

    // TODO make test normal
    @Test
    public void testToVo() throws MappingException {
        TreeNode<TaskEntity> entityTree = testDataGenerator.generateEntityTree();
        TaskVo expectedVo = testDataGenerator.generateVo();
        TaskVo actualVo = TaskEntityVoMapper.toVo(entityTree);

        System.out.println("--- expected ---");
        System.out.print(expectedVo.getId() + ", ");
        System.out.print(expectedVo.getSubTasks().get(0).getId() + ", ");
        System.out.print(expectedVo.getSubTasks().get(0).getSubTasks().get(0).getId() + ", ");
        System.out.print(expectedVo.getSubTasks().get(0).getSubTasks().get(0).getSubTasks().get(0).getId());
        System.out.println();
        System.out.println("--- actual ---");
        System.out.print(actualVo.getId() + ", ");
        System.out.print(actualVo.getSubTasks().get(0).getId() + ", ");
        System.out.print(actualVo.getSubTasks().get(0).getSubTasks().get(0).getId() + ", ");
        System.out.print(actualVo.getSubTasks().get(0).getSubTasks().get(0).getSubTasks().get(0).getId());
        System.out.println();
    }

    @Test
    public void testToEntityTree() {
        TreeNode<TaskEntity> expectedEntityTree = testDataGenerator.generateEntityTree();
        TaskVo vo = testDataGenerator.generateVo();
        TreeNode<TaskEntity> actualEntityTree = TaskEntityVoMapper.toEntityTree(vo);

        System.out.println("------ expected ------");
        System.out.println(expectedEntityTree);
        System.out.println("------- actual -------");
        System.out.println(actualEntityTree);
    }

}
