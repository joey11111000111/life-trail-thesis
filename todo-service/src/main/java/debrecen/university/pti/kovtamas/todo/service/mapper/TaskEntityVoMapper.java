package debrecen.university.pti.kovtamas.todo.service.mapper;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.todo.service.vo.Priority;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.NonNull;

public class TaskEntityVoMapper {

    private TaskEntityVoMapper() {
    }

    private static DateTimeFormatter dateFormat;

    static {
        dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    }

    public static TaskVo toStandaloneVo(@NonNull TaskEntity entity) {
        return TaskVo.builder()
                .id(entity.getId())
                .taskDef(entity.getTaskDef())
                .priority(Priority.ofInteger(entity.getPriority()))
                .deadline(LocalDate.parse(entity.getDeadline(), dateFormat))
                .category(entity.getCategory())
                .subTasks(null)
                .repeating(entity.isRepeating())
                .build();
    }

    public static void setRelations(@NonNull Collection<TaskVo> vos, @NonNull Collection<TaskEntity> entities) {
        if (vos.size() != entities.size()) {
            throw new IllegalArgumentException("The vo and entity collections differ in size!");
        }

        // Create sorted lists in which the vo of a corresponding entity has the same index
        List<TaskVo> sortedVos = new ArrayList<>(vos);
        List<TaskEntity> sortedEntities = new ArrayList<>(entities);

        Comparator<TaskVo> cmpVo = (vo1, vo2) -> vo1.getId() - vo2.getId();
        Comparator<TaskEntity> cmpEntity = (e1, e2) -> e1.getId() - e2.getId();
        sortedVos.sort(cmpVo);
        sortedEntities.sort(cmpEntity);

        // Set the relations
        for (int i = 0; i < sortedEntities.size(); i++) {
            TaskEntity entity = sortedEntities.get(i);

            // If there are sub tasks of the current entity
            if (entity.getSubTaskIds() != null) {
                List<Integer> subIds = extractSubIds(entity.getSubTaskIds());
                List<TaskVo> subTasks = vos.stream()
                        .filter(vo -> subIds.contains(vo.getId()))
                        .collect(Collectors.toList());
                sortedVos.get(i).setSubTasks(subTasks);
            }
        }
    }

    private static List<Integer> extractSubIds(String ids) {
        if (ids.isEmpty()) {
            throw new IllegalArgumentException("Sub task id string is empty!");
        }

        List<Integer> extractedIds = new ArrayList<>();
        String[] parts = ids.split(",");
        try {
            for (String idString : parts) {
                extractedIds.add(Integer.parseInt(idString));
            }
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Malformed sub task id string!", nfe);
        }

        return extractedIds;
    }

    private static void setSubTasksOf(TaskVo parent, Collection<TaskVo> vos, Collection<Integer> subIds) {
        SortedSet<TaskVo> allSubTasks = new TreeSet<TaskVo>();
        for (Integer id : subIds) {
            Optional<TaskVo> subTask = vos.stream()
                    .filter(vo -> vo.getId() == id)
                    .findFirst();
            if (!subTask.isPresent()) {
                throw new IllegalArgumentException("Required subtask is not present in the collection!");
            }

            allSubTasks.add(subTask.get());
        }
    }

}
