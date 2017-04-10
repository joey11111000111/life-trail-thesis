package debrecen.university.pti.kovtamas.data.test.refactored.task;

import debrecen.university.pti.kovtamas.data.entity.todo.RefactoredTaskEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.task.JdbcTaskRepositoryQueries;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.task.JdbcTaskRepositoryUpdates;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.CategorySaveFailureException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskNotFoundException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.TaskPersistenceException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepositoryQueries;
import debrecen.university.pti.kovtamas.data.interfaces.todo.TaskRepositoryUpdates;
import debrecen.university.pti.kovtamas.data.test.refactored.util.JdbcTestUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@Slf4j
public class JdbcTaskRepositoryQueriesTest {

    static private final TaskRepositoryUpdates REPO_UPDATES;
    static private List<RefactoredTaskEntity> allSavedEntities;

    private final TaskRepositoryQueries repoQueries;

    static {
        REPO_UPDATES = JdbcTaskRepositoryUpdates.getInstance();
    }

    @BeforeClass
    static public void switchToTestTables() throws CategorySaveFailureException, TaskPersistenceException {
        JdbcTestUtils.switchToTestTables(JdbcTestUtils.TestType.UNIT);
        setupTableForTests();
    }

    static private void setupTableForTests() throws CategorySaveFailureException, TaskPersistenceException {
        List<RefactoredTaskEntity> unsavedEntities = TaskTestDataGenerator.generateEntitiesForQueryTests();
        List<RefactoredTaskEntity> savedEntities = REPO_UPDATES.saveOrUpdateAll(unsavedEntities);
        allSavedEntities = Collections.unmodifiableList(savedEntities);
    }

    @AfterClass
    static public void switchToProductionTables() {
        JdbcTestUtils.switchToProductionTables();
    }

    public JdbcTaskRepositoryQueriesTest() throws CategorySaveFailureException, TaskPersistenceException {
        repoQueries = JdbcTaskRepositoryQueries.getInstance();
    }

    @Test(expected = TaskNotFoundException.class)
    public void findByIdShouldThrowExceptionWhenIdIsNotPresent() throws TaskNotFoundException {
        int notExistingId = getHighestExistingTaskId() + 1;
        repoQueries.findById(notExistingId);
    }

    @Test
    public void findByIdTest() throws TaskNotFoundException {
        for (RefactoredTaskEntity entity : allSavedEntities) {
            RefactoredTaskEntity loadedEntity = repoQueries.findById(entity.getId());
            assertEquals(entity, loadedEntity);
        }
    }

    @Test
    public void findByIdsShouldReturnEmptyListWhenNoneOfTheIdsArePresent() {
        int highestId = getHighestExistingTaskId();
        Set<Integer> notExistingIds = IntStream.range(highestId + 1, highestId + 8)
                .mapToObj(id -> new Integer(id))
                .collect(Collectors.toSet());

        List<RefactoredTaskEntity> expected = Collections.EMPTY_LIST;
        List<RefactoredTaskEntity> actual = repoQueries.findByIds(notExistingIds);
        assertEquals(expected, actual);
    }

    @Test
    public void findByIdsShouldReturnEntitiesForAllExistingIds() {
        int highestId = getHighestExistingTaskId();
        List<Integer> ids = new ArrayList<>();
        List<RefactoredTaskEntity> expected = new ArrayList<>();

        final int testEntityCount = 3;
        for (int i = 0; i < testEntityCount; i++) {
            expected.add(allSavedEntities.get(i));
            ids.add(allSavedEntities.get(i).getId());
            ids.add(++highestId);
        }

        List<RefactoredTaskEntity> actual = repoQueries.findByIds(ids);
        for (int i = 0; i < testEntityCount; i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    public void findTodayAndUnfinishedPastTasksTest() {
        List<RefactoredTaskEntity> expected = allSavedEntities.stream()
                .filter(this::isTodayOrActivePastTask)
                .collect(Collectors.toList());

        List<RefactoredTaskEntity> actual = repoQueries.findTodayAndUnfinishedPastTasks();
        listEqualsOrdered(expected, actual);
    }

    public boolean isTodayOrActivePastTask(RefactoredTaskEntity entity) {
        boolean isToday = LocalDate.now().equals(entity.getDeadline());
        boolean isActive = !entity.isCompleted();
        boolean isPastTask = LocalDate.now().isAfter(entity.getDeadline());

        return isToday || (isActive && isPastTask);
    }

    @Test
    public void findActiveByCategoryTest() {
        List<RefactoredTaskEntity> expected = allSavedEntities.stream()
                .filter(entity -> entity.getCategoryId() != null)
                .collect(Collectors.toList());

        final int categoryId = expected.get(0).getCategoryId();
        List<RefactoredTaskEntity> actual = repoQueries.findActiveByCategoryId(categoryId);

        listEqualsOrdered(expected, actual);
    }

    @Test
    public void findCompletedTasksTest() {
        List<RefactoredTaskEntity> expected = allSavedEntities.stream()
                .filter(RefactoredTaskEntity::isCompleted)
                .collect(Collectors.toList());

        List<RefactoredTaskEntity> actual = repoQueries.findCompletedTasks();
        listEqualsOrdered(expected, actual);
    }

    @Test
    public void findActiveTasksBetweenTest() {
        LocalDate since = LocalDate.now();
        LocalDate until = since.plusDays(16);
        List<RefactoredTaskEntity> expected = allSavedEntities.stream()
                .filter(entity -> isActiveTaskBetweenBothIncluded(entity, since, until))
                .collect(Collectors.toList());

        List<RefactoredTaskEntity> actual = repoQueries.findActiveTasksBetween(since, until);
        listEqualsOrdered(expected, actual);
    }

    private boolean isActiveTaskBetweenBothIncluded(RefactoredTaskEntity entity, LocalDate start, LocalDate end) {
        LocalDate deadline = entity.getDeadline();

        boolean isActiveBetween = deadline.equals(start) || deadline.equals(end);
        isActiveBetween |= deadline.isAfter(start) && deadline.isBefore(end);
        isActiveBetween &= !entity.isCompleted();
        return isActiveBetween;
    }

    @Test
    public void getRowCountShouldReturnNumberOfSavedTasks() {
        int expected = allSavedEntities.size();
        int actual = repoQueries.getRowCount();
        assertEquals(expected, actual);
    }

    private void listEqualsOrdered(List<RefactoredTaskEntity> expected, List<RefactoredTaskEntity> actual) {
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    private int getHighestExistingTaskId() {
        OptionalInt highestId = allSavedEntities.stream()
                .mapToInt(RefactoredTaskEntity::getId)
                .max();

        if (!highestId.isPresent()) {
            throw new RuntimeException("Unexpected event, could not find highest existing task ID");
        } else {
            return highestId.getAsInt();
        }
    }

}
