package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task;

import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task.display.TaskDisplayer;
import debrecen.university.pti.kovtamas.display.utils.load.DisplayLoadException;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.CategoryVo;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.LogicalCategoryNames.LogicalCategories;
import debrecen.university.pti.kovtamas.todo.service.api.TaskDeletionException;
import debrecen.university.pti.kovtamas.todo.service.api.TaskSaveFailureException;
import debrecen.university.pti.kovtamas.todo.service.api.TodoService;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javafx.scene.layout.VBox;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskSubController {

    private final TodoService service;
    private TaskDisplayer taskDisplayer;
    private Map<LogicalCategories, Supplier<List<TaskVo>>> logicalCategoryTaskQueries;
    private CategoryVo selectedCategory;

    public TaskSubController(@NonNull final TodoService service, @NonNull final VBox taskBox) {
        this.service = service;
        this.selectedCategory = null;
        initTaskDisplayer(taskBox);
        initLogicalCategoryTaskQueries();
        setupTaskChangeAction();
    }

    private void initTaskDisplayer(final VBox taskBox) {
        taskDisplayer = new TaskDisplayer(taskBox, service.getCustomCategories());
    }

    private void initLogicalCategoryTaskQueries() {
        logicalCategoryTaskQueries = new HashMap<>();
        logicalCategoryTaskQueries.put(LogicalCategories.TODAY, service::getTodayTasks);
        logicalCategoryTaskQueries.put(LogicalCategories.TOMORROW, service::getTomorrowTasks);
        logicalCategoryTaskQueries.put(LogicalCategories.THIS_WEEK, service::getOneWeekTasks);
        logicalCategoryTaskQueries.put(LogicalCategories.UNCATEGORIZED, service::getUncategorizedTasks);
        logicalCategoryTaskQueries.put(LogicalCategories.COMPLETED, service::getCompletedTasks);
    }

    private void setupTaskChangeAction() {
        taskDisplayer.registerTaskChangeAction((changedTaskVo) -> {
            try {
                service.save(changedTaskVo);
                log.info("Saved changes of task with id: " + changedTaskVo.getId());
            } catch (TaskSaveFailureException tsfe) {
                log.warn("Could not save changes of task with id: " + changedTaskVo.getId(), tsfe);
            }
        });
    }

    public void newCategoryAddedAction(String newCategory) {
        taskDisplayer.newCategoryAddedAction(newCategory);
    }

    public void categoryRemovedAction(String removedCategory) {
        taskDisplayer.categoryRemovedAction(removedCategory);
    }

    public void selectedCategoryChangedAction(CategoryVo fromCategoryVo, CategoryVo toCategoryVo) {
        switchToCategory(toCategoryVo);
    }

    public void switchToCategory(@NonNull final CategoryVo categoryVo) {
        taskDisplayer.clear();
        List<TaskVo> tasksOfCategory = getCategoryTasks(categoryVo);
        try {
            taskDisplayer.displayAllTasks(tasksOfCategory);
        } catch (DisplayLoadException dle) {
            handleTaskRowCreationException(dle);
        }

        selectedCategory = categoryVo;
    }

    public void toggleDisableForSelectedRow() {
        taskDisplayer.toggleDisableForSelectedRow();
    }

    public void addNewTask() {
        if (selectedCategory == null) {
            return;
        }

        TaskVo parent = taskDisplayer.getSelectedTask();
        if (parent == null) {
            addNewTopLevelTask();
        } else {
            addNewSubTask(parent);
        }
    }

    private void addNewTopLevelTask() {
        TaskVo newTask = setupNewTaskState(service.newMinimalTaskVo());
        updateTaskAndReloadCategory(newTask);
    }

    private TaskVo setupNewTaskState(TaskVo newMinimalTask) {
        if (selectedCategory.isLogical()) {
            // A minimal taskVo is perfectly set up by its nature for every logical category except for the TOMORROW
            if (selectedCategory.getLogicalCategory() == LogicalCategories.TOMORROW) {
                newMinimalTask.setDeadline(LocalDate.now().plusDays(1));
            }
        } else {
            newMinimalTask.setCategory(selectedCategory.getCustomCategoryName());
        }

        return newMinimalTask;
    }

    private void addNewSubTask(TaskVo parent) {
        service.addNewMinimalSubTaskTo(parent);
        updateTaskAndReloadCategory(parent);
    }

    private void updateTaskAndReloadCategory(TaskVo task) {
        try {
            service.save(task);
            switchToCategory(selectedCategory);
        } catch (TaskSaveFailureException tsfe) {
            log.warn("Failed to save new minimal sub task!", tsfe);
        }
    }

    public void removeSelectedTask() {
        if (!taskDisplayer.hasSelectedTask()) {
            return;
        }

        TaskVo selectedTask = taskDisplayer.getSelectedTask();
        TaskVo rootTask = taskDisplayer.getRootOfSelectedTaskTree();

        try {
            service.deleteTaskFromTaskTree(selectedTask, rootTask);
            switchToCategory(selectedCategory);
        } catch (TaskDeletionException tde) {
            log.warn("Failed to delete selected task!", tde);
        }
    }

    private List<TaskVo> getCategoryTasks(CategoryVo categoryVo) {
        if (categoryVo.isLogical()) {
            return getLogicalCategoryTasks(categoryVo.getLogicalCategory());
        }

        return getCustomCategoryTasks(categoryVo.getCustomCategoryName());
    }

    private List<TaskVo> getCustomCategoryTasks(String categoryName) {
        return service.getActiveByCategory(categoryName);
    }

    private List<TaskVo> getLogicalCategoryTasks(LogicalCategories logicalCategory) {
        return logicalCategoryTaskQueries.get(logicalCategory).get();
    }

    private void handleTaskRowCreationException(DisplayLoadException dle) {
        String errorMessage = "Failed to load fxml resource for displaying tasks";
        log.error(errorMessage, dle);
        taskDisplayer.displayErrorMessage(errorMessage);
    }

}
