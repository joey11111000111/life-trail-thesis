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
        List<TaskVo> expected = generateVos();
        expected.forEach(vo -> vo.setSubTasks(null));
        List<TaskVo> results = generateEntities().stream()
                .map(TaskEntityVoMapper::toStandaloneVo)
                .collect(Collectors.toList());

        voListEquals(expected, results);
    }

    @Test
    public void setRelationsTest() {
        List<TaskVo> expectedVos = generateVos();

        List<TaskEntity> entities = generateEntities();
        List<TaskVo> vos = generateVos();
        vos.forEach(vo -> vo.setSubTasks(null));

        TaskEntityVoMapper.setRelations(vos, entities);

        voListEquals(expectedVos, vos);
    }

    @Test
    public void toStandaloneEntityTest() {
        List<TaskEntity> expectedEntities = generateEntities();
        expectedEntities.forEach(entity -> entity.setSubTaskIds(null));

        List<TaskVo> vos = generateVos();
        List<TaskEntity> results = vos.stream()
                .map(TaskEntityVoMapper::toStandaloneEntity)
                .collect(Collectors.toList());

        entityListEquals(expectedEntities, results);
    }

    @Test
    public void toCompleteEntityTest() {
        List<TaskEntity> expectedEntities = generateEntities();

        List<TaskVo> vos = generateVos();
        List<TaskEntity> results = vos.stream()
                .map(TaskEntityVoMapper::toCompleteEntity)
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

        return entities;
    }

    private List<TaskVo> generateVos() {
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

        List<TaskVo> subs = new ArrayList<>();
        vos.forEach(vo -> subs.add(vo));
        vos.add(TaskVo.builder()
                .id(3)
                .taskDef("Manage you health")
                .priority(Priority.ofInteger(2))
                .deadline(LocalDate.of(2017, 1, 10))
                .category("personal")
                .subTasks(subs)
                .repeating(false)
                .build()
        );

        List<TaskVo> subsOfLast = new ArrayList<>();
        vos.stream()
                .filter(vo -> vo.getId() == 3)
                .forEach(vo -> subsOfLast.add(vo));
        vos.add(TaskVo.builder()
                .id(4)
                .taskDef("Live happily")
                .priority(Priority.ofInteger(3))
                .deadline(LocalDate.of(2017, 1, 10))
                .category("personal")
                .subTasks(subsOfLast)
                .repeating(true)
                .build()
        );

        return vos;
    }

}
