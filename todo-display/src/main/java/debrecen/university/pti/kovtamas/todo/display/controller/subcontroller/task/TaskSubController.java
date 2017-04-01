package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task;

import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task.display.TaskDisplayer;
import debrecen.university.pti.kovtamas.display.utils.load.DisplayLoadException;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.CategoryVo;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.LogicalCategoryNames.LogicalCategories;
import debrecen.university.pti.kovtamas.todo.service.api.TaskSaveFailureException;
import debrecen.university.pti.kovtamas.todo.service.api.TodoService;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        TaskVo newTask = service.newMinimalTaskVo();
        // Setup new task state
        if (selectedCategory.isLogical() && selectedCategory.getLogicalCategory() == LogicalCategories.TOMORROW) {
            newTask.setDeadline(LocalDate.now().plusDays(1));
        }
        if (selectedCategory.isCustom()) {
            newTask.setCategory(selectedCategory.getCustomCategoryName());
        }

        try {
            service.save(newTask);
        } catch (TaskSaveFailureException tsfe) {
            log.warn("Failed to save new top level minimal task!", tsfe);
            return;
        }

        switchToCategory(selectedCategory);
    }

    private void addNewSubTask(TaskVo parent) {

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
