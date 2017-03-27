package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task;

import debrecen.university.pti.kovtamas.display.utils.display.DisplayLoadException;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.CategoryVo;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.LogicalCategoryNames.LogicalCategories;
import debrecen.university.pti.kovtamas.todo.service.api.TodoService;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.util.List;
import javafx.scene.layout.VBox;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskSubController {

    private final TodoService service;
    private TaskDisplayer taskDisplayer;

    public TaskSubController(@NonNull final TodoService service, @NonNull final VBox taskBox) {
        this.service = service;
        initTaskDisplayer(taskBox);
    }

    public void newCategoryAddedAction(String newCategory) {
        taskDisplayer.newCategoryAddedAction(newCategory);
    }

    public void categoryRemovedAction(String removedCategory) {
        taskDisplayer.categoryRemovedAction(removedCategory);
    }

    public void selectedCategoryChangedAction(CategoryVo fromCategoryVo, CategoryVo toCategoryVo) {
        switchCategory(toCategoryVo);
    }

    public void switchCategory(@NonNull final CategoryVo categoryVo) {
        taskDisplayer.clear();
        List<TaskVo> tasksOfCategory = getCategoryTasks(categoryVo);
        try {
            taskDisplayer.displayAllTasks(tasksOfCategory);
        } catch (DisplayLoadException dle) {
            handleTaskRowCreationException(dle);
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
        switch (logicalCategory) {
            case TODAY:
                return service.getTodayTasks();
            case TOMORROW:
                return service.getTomorrowTasks();
            case THIS_WEEK:
                return service.getTasksOfFollowingDays(7);
            case UNCATEGORIZED:
                return service.getUncategorizedTasks();
            case COMPLETED:
                return service.getCompletedTasks();
            default:
                throwUnsupportedException(logicalCategory);
                // Never executed, but NetBeans 8.2 doesn't realize that this is not needed
                return null;
        }
    }

    private void throwUnsupportedException(LogicalCategories logicalCategory) {
        throw new UnsupportedOperationException("Logical category '"
                + logicalCategory.name() + "' is not supported yet");
    }

    private void handleTaskRowCreationException(DisplayLoadException dle) {
        String errorMessage = "Failed to load fxml resource for displaying tasks";
        log.error(errorMessage, dle);
        taskDisplayer.displayErrorMessage(errorMessage);
    }

    private void initTaskDisplayer(final VBox taskBox) {
        taskDisplayer = new TaskDisplayer(taskBox, service.getCustomCategories());
    }

}
