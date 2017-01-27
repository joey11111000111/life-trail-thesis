package debrecen.university.pti.kovtamas.todo.service;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CollectionAssert {

    private CollectionAssert() {
    }

    public static void voListEquals(List<TaskVo> l1, List<TaskVo> l2) {
        assertEquals(l1.size(), l2.size());

        Comparator<TaskVo> cmp = (e1, e2) -> e1.getId() - e2.getId();
        l1.sort(cmp);
        l2.sort(cmp);

        for (int i = 0; i < l1.size(); i++) {
            assertEquals(l1.get(i), l2.get(i));
        }
    }

    public static void entityListEquals(List<TaskEntity> l1, List<TaskEntity> l2) {
        assertEquals(l1.size(), l2.size());

        Comparator<TaskEntity> cmp = (e1, e2) -> e1.getId() - e2.getId();
        l1.sort(cmp);
        l2.sort(cmp);

        for (int i = 0; i < l1.size(); i++) {
            assertEquals(l1.get(i), l2.get(i));
        }
    }

}
