package debrecen.university.pti.kovtamas.data.impl.sql.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class JdbcTaskUtils {

    private final DateTimeFormatter requiredDateFormat;

    public JdbcTaskUtils(DateTimeFormatter requiredDateFormat) {
        this.requiredDateFormat = requiredDateFormat;
    }

    public TaskEntity convertRecordToEntity(ResultSet record) throws SQLException {
        String deadlineString = formatToRequiredDateFormat(record.getDate("DEADLINE"));
        return TaskEntity.builder()
                .id(record.getInt("ID"))
                .taskDef(record.getString("TASK_DEF"))
                .priority(record.getInt("PRIORITY"))
                .deadline(deadlineString)
                .category(record.getString("CATEGORY"))
                .subTaskIds(record.getString("SUB_TASK_IDS"))
                .repeating(Boolean.parseBoolean(record.getString("REPEATING")))
                .build();
    }

    public Set<TaskEntity> extractEntities(ResultSet results) throws SQLException {
        Set<TaskEntity> entities = new HashSet<>();
        while (results.next()) {
            entities.add(convertRecordToEntity(results));
        }
        return entities;
    }

    public Integer extractGeneratedId(PreparedStatement prStatement) throws SQLException, TaskPersistenceException {
        ResultSet result = prStatement.getGeneratedKeys();
        if (result.next()) {
            return result.getInt(1);
        }
        throw new TaskPersistenceException("Possible save error, generated ID could not be retrived!");
    }

    public Date parseToSqlDate(String dateString) {
        if (dateString == null) {
            return null;
        }

        LocalDate localDate = LocalDate.parse(dateString, requiredDateFormat);
        return Date.valueOf(localDate);
    }

    private String formatToRequiredDateFormat(Date date) {
        if (date == null) {
            return null;
        }

        LocalDate localDate = date.toLocalDate();
        return localDate.format(requiredDateFormat);
    }

}
