package debrecen.university.pti.kovtamas.todo.service.mapper;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.todo.service.vo.Priority;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TaskEntityVoMapperTest {

    @Test
    public void toStandaloneVoTest() {
        List<TaskVo> expected = generateStandaloneVos();
        List<TaskVo> results = generateEntities().stream()
                .map(TaskEntityVoMapper::toStandaloneVo)
                .collect(Collectors.toList());

        voListEquals(expected, results);
    }

//    @Test
//    public void setRelationsTest() {
//        List<TaskVo> expectedVos = generateVos();
//
//        List<TaskEntity> entities = generateEntities();
//        List<TaskVo> vos = generateVos();
//        vos.forEach(vo -> vo.setSubTasks(null));
//
//        TaskEntityVoMapper.setRelations(vos, entities);
//
//        voListEquals(expectedVos, vos);
//    }
    @Test
    public void toStandaloneEntityTest() {
        List<TaskEntity> expectedEntities = generateStandaloneEntities();

        List<TaskVo> vos = generateStandaloneVos();
        List<TaskEntity> results = vos.stream()
                .map(TaskEntityVoMapper::toStandaloneEntity)
                .collect(Collectors.toList());

        entityListEquals(expectedEntities, results);
    }

    @Test
    public void toEntityTest() {
        List<TaskEntity> entities = generateEntities();
        List<TaskEntity> expectedEntities = new ArrayList<>(2);
        expectedEntities.add(entities.get(3));
        expectedEntities.add(entities.get(4));

        List<TaskVo> vos = generateVos();
        List<TaskEntity> results = vos.stream()
                .map(vo -> TaskEntityVoMapper.toEntity(vo))
                .collect(Collectors.toList());

        entityListEquals(expectedEntities, results);
    }

    private void voListEquals(List<TaskVo> l1, List<TaskVo> l2) {
        assertEquals(l1.size(), l2.size());

        Comparator<TaskVo> cmp = (e1, e2) -> e1.getId() - e2.getId();
        l1.sort(cmp);
        l2.sort(cmp);

        for (int i = 0; i < l1.size(); i++) {
            assertEquals(l1.get(i), l2.get(i));
        }
    }

    private void entityListEquals(List<TaskEntity> l1, List<TaskEntity> l2) {
        assertEquals(l1.size(), l2.size());

        Comparator<TaskEntity> cmp = (e1, e2) -> e1.getId() - e2.getId();
        l1.sort(cmp);
        l2.sort(cmp);

        for (int i = 0; i < l1.size(); i++) {
            assertEquals(l1.get(i), l2.get(i));
        }
    }

    private List<TaskEntity> generateEntities() {
        List<TaskEntity> entities = new ArrayList<>();
        entities.add(TaskEntity.builder()
                .id(1)
                .taskDef("Go to the gym")
                .priority(3)
                .deadline("2017.01.10")
                .category("self development")
                .subTaskIds(null)
                .repeating(false)
                .build()
        );

        entities.add(TaskEntity.builder()
                .id(2)
                .taskDef("Eat fruits")
                .priority(2)
                .deadline("2017.01.10")
                .category("personal")
                .subTaskIds(null)
                .repeating(false)
                .build()
        );

        entities.add(TaskEntity.builder()
                .id(3)
                .taskDef("Manage you health")
                .priority(2)
                .deadline("2017.01.10")
                .category("personal")
                .subTaskIds("1,2")
                .repeating(false)
                .build()
        );

        entities.add(TaskEntity.builder()
                .id(4)
                .taskDef("Live happily")
                .priority(3)
                .deadline("2017.01.10")
                .category("personal")
                .subTaskIds("3")
                .repeating(true)
                .build()
        );

        entities.add(TaskEntity.builder()
                .id(5)
                .taskDef("Learn for the exam")
                .priority(3)
                .deadline("2017.01.12")
                .category("School")
                .subTaskIds(null)
                .repeating(true)
                .build()
        );

        return entities;
    }

    private List<TaskEntity> generateStandaloneEntities() {
        List<TaskEntity> entities = generateEntities();
        entities.forEach(entity -> entity.setSubTaskIds(null));
        return entities;
    }

    private List<TaskVo> generateStandaloneVos() {
        List<TaskVo> vos = new ArrayList<>();
        vos.add(TaskVo.builder()
                .id(1)
                .taskDef("Go to the gym")
                .priority(Priority.ofInteger(3))
                .deadline(LocalDate.of(2017, 1, 10))
                .category("self development")
                .subTasks(null)
                .repeating(false)
                .build()
        );
        vos.add(TaskVo.builder()
                .id(2)
                .taskDef("Eat fruits")
                .priority(Priority.ofInteger(2))
                .deadline(LocalDate.of(2017, 1, 10))
                .category("personal")
                .subTasks(null)
                .repeating(false)
                .build()
        );
        vos.add(TaskVo.builder()
                .id(3)
                .taskDef("Manage you health")
                .priority(Priority.ofInteger(2))
                .deadline(LocalDate.of(2017, 1, 10))
                .category("personal")
                .subTasks(null)
                .repeating(false)
                .build()
        );
        vos.add(TaskVo.builder()
                .id(4)
                .taskDef("Live happily")
                .priority(Priority.ofInteger(3))
                .deadline(LocalDate.of(2017, 1, 10))
                .category("personal")
                .subTasks(null)
                .repeating(true)
                .build()
        );
        vos.add(TaskVo.builder()
                .id(5)
                .taskDef("Learn for the exam")
                .priority(Priority.ofInteger(3))
                .deadline(LocalDate.of(2017, 1, 12))
                .category("School")
                .subTasks(null)
                .repeating(true)
                .build()
        );

        return vos;
    }

    private List<TaskVo> generateVos() {
        List<TaskVo> standaloneVos = generateStandaloneVos();

        // Set relations
        List<TaskVo> subsOf2 = new ArrayList<>(2);
        subsOf2.add(standaloneVos.get(0));
        subsOf2.add(standaloneVos.get(1));
        standaloneVos.get(2).setSubTasks(subsOf2);

        List<TaskVo> subsOf3 = new ArrayList<>(1);
        subsOf3.add(standaloneVos.get(2));
        standaloneVos.get(3).setSubTasks(subsOf3);

        List<TaskVo> vos = new ArrayList<>();
        vos.add(standaloneVos.get(3));
        vos.add(standaloneVos.get(4));

        return vos;
    }

}
