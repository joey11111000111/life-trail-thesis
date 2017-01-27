package debrecen.university.pti.kovtamas.todo.service.mapper;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.todo.service.vo.Priority;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;

public class TaskEntityVoMapper {

    private TaskEntityVoMapper() {
    }

    static DateTimeFormatter dateFormat = null;

    static {
        dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    }

    public static DateTimeFormatter getDateFormat() {
        return dateFormat;
    }

    public static TaskEntity toStandaloneEntity(@NonNull TaskVo vo) {
        return TaskEntity.builder()
                .id(vo.getId())
                .taskDef(vo.getTaskDef())
                .priority(vo.getPriority().intValue())
                .deadline(vo.getDeadline().format(dateFormat))
                .category(vo.getCategory())
                .subTaskIds(null)
                .repeating(vo.isRepeating())
                .build();
    }

    public static TaskEntity toEntity(@NonNull TaskVo vo) {
        TaskEntity entity = toStandaloneEntity(vo);
        if (vo.hasSubTasks()) {
            entity.setSubTaskIds(buildSubTaskIdsString(vo));
        }

        return entity;
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

    public static List<TaskVo> toVo(@NonNull Collection<TaskEntity> entities) {
        List<TaskVo> vos = entities.stream()
                .map(TaskEntityVoMapper::toStandaloneVo)
                .collect(Collectors.toList());

        setRelations(vos, entities);
        return vos;
    }

    private static void setRelations(Collection<TaskVo> vos, Collection<TaskEntity> entities) {
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
        Set<Integer> allSubTaskIds = new HashSet<>();
        for (int i = 0; i < sortedEntities.size(); i++) {
            TaskEntity entity = sortedEntities.get(i);

            // If there are sub tasks of the current entity
            if (entity.getSubTaskIds() != null) {
                List<Integer> subIds = extractSubIds(entity.getSubTaskIds());
                List<TaskVo> subTasks = vos.stream()
                        .filter(vo -> subIds.contains(vo.getId()))
                        .collect(Collectors.toList());

                allSubTaskIds.addAll(
                        subTasks.stream()
                                .map(TaskVo::getId)
                                .collect(Collectors.toSet())
                );
                sortedVos.get(i).setSubTasks(subTasks);
            }
        }

        removeNestedDuplications(allSubTaskIds, vos);
    }

    private static void removeNestedDuplications(Collection<Integer> allSubIds, Collection<TaskVo> vos) {
        List<TaskVo> vosToRemove = vos.stream()
                .filter(vo -> allSubIds.contains(vo.getId()))
                .collect(Collectors.toList());

        vos.removeAll(vosToRemove);
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

    private static String buildSubTaskIdsString(TaskVo vo) {
        List<TaskVo> subTasks = vo.getSubTasks();

        if (subTasks.isEmpty()) {
            return null;
        }

        subTasks.forEach(sub -> {
            if (sub == null) {
                throw new IllegalArgumentException("Sub task is null in sub task list!");
            }
            if (!sub.hasId()) {
                throw new IllegalArgumentException("TaskVo has a subtask whose id is null!");
            }
        });

        StringBuilder sb = new StringBuilder();
        subTasks.forEach(subTask -> sb.append(subTask.getId()).append(','));
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }
}
