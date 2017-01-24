package debrecen.university.pti.kovtamas.data.test;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class TestDataGenerator {

    public static Set<TaskEntity> generateOriginalEntitySet() {
        Set<TaskEntity> entities = new HashSet<>();
        entities.add(TaskEntity.builder()
                .taskDef("Go to the gym")
                .priority(1)
                .deadline("2017.01.10")
                .category("personal")
                .subTaskIds(null)
                .repeating(false)
                .build()
        );
        entities.add(TaskEntity.builder()
                .taskDef("Go shopping")
                .priority(2)
                .deadline("2017.01.12")
                .category("everyday life")
                .subTaskIds("3, 4")
                .repeating(true)
                .build()
        );
        entities.add(TaskEntity.builder()
                .taskDef("Prepare the bike")
                .priority(2)
                .deadline("2017.01.12")
                .category("everyday life")
                .subTaskIds(null)
                .build()
        );
        entities.add(TaskEntity.builder()
                .taskDef("Lock the door")
                .priority(1)
                .deadline("2017.01.12")
                .category("everyday life")
                .subTaskIds(null)
                .build()
        );

        return entities;
    }

    public static Set<TaskEntity> generateEntitiesForTodayTest(DateTimeFormatter formatter) {
        Set<TaskEntity> entities = new HashSet<>();
        String todayString = LocalDate.now().format(formatter);
        entities.add(TaskEntity.builder()
                .taskDef("Today1")
                .priority(1)
                .deadline(todayString)
                .category("personal")
                .subTaskIds(null)
                .repeating(false)
                .build()
        );
        entities.add(TaskEntity.builder()
                .taskDef("Today2")
                .priority(2)
                .deadline(todayString)
                .category("uncategorized")
                .subTaskIds(null)
                .repeating(false)
                .build()
        );
        String pastDayString = LocalDate.of(1000, 2, 2).format(formatter);
        entities.add(TaskEntity.builder()
                .taskDef("Not today but repeating")
                .priority(0)
                .deadline(pastDayString)
                .category("personal")
                .subTaskIds(null)
                .repeating(true)
                .build()
        );
        entities.add(TaskEntity.builder()
                .taskDef("Not today, not repeating, should not be found")
                .priority(1)
                .deadline(pastDayString)
                .category("exclude me")
                .subTaskIds(null)
                .repeating(false)
                .build()
        );

        return entities;
    }

    public static Set<TaskEntity> generateEntitiesForUntilTest(DateTimeFormatter formatter) {
        Set<TaskEntity> entities = new HashSet<>();
        final LocalDate NOW = LocalDate.now();
        String todayString = NOW.format(formatter);
        entities.add(TaskEntity.builder()
                .taskDef("Today")
                .priority(1)
                .deadline(todayString)
                .category("personal")
                .subTaskIds(null)
                .repeating(false)
                .build()
        );
        String pastDayString = LocalDate.of(1000, 2, 2).format(formatter);
        entities.add(TaskEntity.builder()
                .taskDef("Past day but repeating")
                .priority(2)
                .deadline(pastDayString)
                .category("uncategorized")
                .subTaskIds(null)
                .repeating(true)
                .build()
        );
        entities.add(TaskEntity.builder()
                .taskDef("Past day, not repeating")
                .priority(3)
                .deadline(pastDayString)
                .category("exclude me")
                .subTaskIds(null)
                .repeating(false)
                .build()
        );
        String twoDaysLater = NOW.plusDays(2).format(formatter);
        entities.add(TaskEntity.builder()
                .taskDef("Two days later")
                .priority(0)
                .deadline(twoDaysLater)
                .category("personal")
                .subTaskIds(null)
                .repeating(false)
                .build()
        );
        String oneWeekLaterString = NOW.plusWeeks(1).format(formatter);
        entities.add(TaskEntity.builder()
                .taskDef("One week later")
                .priority(0)
                .deadline(oneWeekLaterString)
                .category("personal")
                .subTaskIds(null)
                .repeating(false)
                .build()
        );
        String twoWeeksLaterString = NOW.plusWeeks(2).format(formatter);
        entities.add(TaskEntity.builder()
                .taskDef("Two weeks later")
                .priority(1)
                .deadline(twoWeeksLaterString)
                .category("still in")
                .subTaskIds(null)
                .repeating(false)
                .build()
        );
        String oneYearLaterString = NOW.plusYears(1).format(formatter);
        entities.add(TaskEntity.builder()
                .taskDef("One year later")
                .priority(1)
                .deadline(oneYearLaterString)
                .category("exclude me")
                .subTaskIds(null)
                .repeating(false)
                .build()
        );

        return entities;
    }
}
