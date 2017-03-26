package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task;

import debrecen.university.pti.kovtamas.display.utils.display.DisplayLoadException;
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

    public void selectedCategoryChangedAction(String fromCategory, String toCategory) {
        switchCategory(toCategory);
    }

    public void switchCategory(@NonNull final String category) {
        taskDisplayer.clear();
        List<TaskVo> activeTasksOfCategory = service.getActiveByCategory(category);

        activeTasksOfCategory.forEach(task -> {
            try {
                taskDisplayer.displayTaskTree(task);
            } catch (DisplayLoadException dle) {
                handleTaskRowCreationException(dle);
            }
        });
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
