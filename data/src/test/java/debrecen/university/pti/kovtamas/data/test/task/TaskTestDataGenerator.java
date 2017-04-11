package debrecen.university.pti.kovtamas.data.test.task;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class TaskTestDataGenerator {

    private TaskTestDataGenerator() {
    }

    static public List<TaskEntity> generateEntitiesForQueryTests() {
        final Integer categoryId = 1;
        List<TaskEntity> generatedEntities = new ArrayList<>();

        generatedEntities.add(TaskEntity.builder()
                        .categoryId(null)
                        .deadline(LocalDate.now())
                        .priority(1)
                        .taskDef("TodayTask 1 priority")
                        .completed(false)
                        .build()
        );
        generatedEntities.add(TaskEntity.builder()
                        .categoryId(categoryId)
                        .deadline(LocalDate.now().plusDays(1))
                        .priority(2)
                        .taskDef("TomorrowTask 2 priority")
                        .completed(false)
                        .build()
        );
        generatedEntities.add(TaskEntity.builder()
                        .categoryId(null)
                        .deadline(LocalDate.now().plusDays(3))
                        .priority(3)
                        .taskDef("ThisWeekTask 3 priority")
                        .completed(false)
                        .build()
        );
        generatedEntities.add(TaskEntity.builder()
                        .categoryId(null)
                        .deadline(LocalDate.now().plusDays(16))
                        .priority(0)
                        .taskDef("ThisMonthTask 0 priority")
                        .completed(false)
                        .build()
        );
        generatedEntities.add(TaskEntity.builder()
                        .categoryId(categoryId)
                        .deadline(LocalDate.now())
                        .priority(1)
                        .taskDef("TodayTask completed")
                        .completed(true)
                        .build()
        );
        generatedEntities.add(TaskEntity.builder()
                        .categoryId(categoryId)
                        .deadline(LocalDate.now().minusDays(2))
                        .priority(1)
                        .taskDef("Past task not completed")
                        .completed(false)
                        .build()
        );
        generatedEntities.add(TaskEntity.builder()
                        .categoryId(categoryId)
                        .deadline(LocalDate.now().minusDays(1))
                        .priority(1)
                        .taskDef("Past task completed")
                        .completed(true)
                        .build()
        );

        return generatedEntities;
    }

    static public List<TaskEntity> generateEntities(int numOfEntities) {
        final Integer categoryId = 1;
        List<TaskEntity> generatedEntities = new ArrayList<>();

        for (int i = 0; i < numOfEntities; i++) {
            generatedEntities.add(TaskEntity.builder()
                            .categoryId((i % 2 == 0) ? categoryId : null)
                            .deadline(LocalDate.now().plusDays(i))
                            .priority(i % 4)
                            .taskDef("Generated task " + i)
                            .completed(i % 3 == 0)
                            .build()
            );
        }

        return generatedEntities;
    }

    static public TaskEntity generateOneEntity() {
        return TaskEntity.builder()
                .categoryId(1)
                .deadline(LocalDate.now())
                .priority(2)
                .taskDef("Single generated task")
                .completed(false)
                .build();
    }

}
