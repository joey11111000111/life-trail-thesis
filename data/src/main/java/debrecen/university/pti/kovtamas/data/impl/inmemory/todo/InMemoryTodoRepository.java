package debrecen.university.pti.kovtamas.data.impl.inmemory.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.DatabaseIntegrityException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TodoRepository;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryTodoRepository /*implements TodoRepository*/ {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryTodoRepository.class);

    private final BerkeleyView data;

    public InMemoryTodoRepository(String saveDir) throws IOException {
        BerkeleyDatabase db = new BerkeleyDatabase(saveDir);
        data = new BerkeleyView(db);
        LOG.info("InMemoryTodoRepository was created");
    }

//    @Override
    public Set<TaskEntity> findAll() {
        return data.getAllEntities();
    }

//    @Override
    public Set<TaskEntity> findByCategory(@NonNull String category) {
        return data.getAllEntities().stream()
                .filter(entity -> category.equals(entity.getCategory()))
                .collect(Collectors.toSet());
    }

//    @Override
    public TaskEntity findById(int id) throws TaskNotFoundException {
        Set<TaskEntity> results = data.getAllEntities().stream()
                .filter(entity -> entity.getId() == id)
                .collect(Collectors.toSet());
        if (results.isEmpty()) {
            String exceptionMessage = "The id " + id + " doesn't belong to any object in the database!";
            LOG.warn(exceptionMessage);
            throw new TaskNotFoundException(exceptionMessage);
        }

        if (results.size() > 1) {
            throw new DatabaseIntegrityException("Multiple todo items has the same " + id + " id in the database!");
        }

        return results.iterator().next();
    }

//    @Override
    public Set<TaskEntity> findByIds(@NonNull Set<Integer> ids) throws TaskNotFoundException {
        if (ids.isEmpty()) {
            return new HashSet<>();
        }

        Set<TaskEntity> results = data.getAllEntities().stream()
                .filter(entity -> ids.contains(entity.getId()))
                .collect(Collectors.toSet());

        if (results.size() < ids.size()) {
            String exceptionMessage = buildByIdsNotFoundExceptionMessage(ids, results);
            LOG.warn(exceptionMessage);
            throw new TaskNotFoundException(exceptionMessage);
        }

        if (results.size() > 1) {
            String exceptionMessage = "Multiple todo items has the same ids in the database!";
            LOG.error(exceptionMessage);
            throw new DatabaseIntegrityException(exceptionMessage);
        }

        return results;
    }

    private String buildByIdsNotFoundExceptionMessage(Set<Integer> ids, Set<TaskEntity> results) {
        Set<Integer> foundIds = results.stream()
                .map(entity -> entity.getId())
                .collect(Collectors.toSet());
        Set<Integer> missingIds = ids.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toSet());

        StringBuilder sb = new StringBuilder();
        missingIds.forEach(id -> sb.append(id).append(", "));
        sb.delete(sb.length() - 2, sb.length());
        String missingIdsString = sb.toString();

        return "The ids " + missingIdsString + " do not belong to any objects in the database!";
    }

//    @Override
    public void save(TaskEntity entity) {
        data.add(entity);
    }

//    @Override
    public void saveAll(Collection<TaskEntity> entities) {
        data.addAll(entities);
    }

}
