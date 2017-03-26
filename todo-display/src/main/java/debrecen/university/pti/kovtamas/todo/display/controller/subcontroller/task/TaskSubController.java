package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task;

import debrecen.university.pti.kovtamas.display.utils.display.DisplayLoadException;
import debrecen.university.pti.kovtamas.todo.service.api.TodoService;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskSubController {

    private final TodoService service;
    private VBox taskBox;
    private TaskDisplayer taskDisplayer;
    private StringProperty chosenCategory;

    public TaskSubController(@NonNull final TodoService service, @NonNull final VBox taskBox) {
        this.service = service;
        this.taskBox = taskBox;
        initTaskDisplayer();
        initChosenCategoryProperty();
    }

    public void setTaskBox(@NonNull VBox newTaskBox) {
        List<Node> newTaskBoxNodes = newTaskBox.getChildren();
        newTaskBoxNodes.clear();
        newTaskBoxNodes.addAll(taskBox.getChildren());
        taskBox = newTaskBox;
    }

    public void switchCategory(@NonNull final String category) {
        clearTasks();
        List<TaskVo> activeTasksOfCategory = service.getActiveByCategory(category);

        activeTasksOfCategory.forEach(task -> {
            try {
                taskDisplayer.displayTaskTree(task);
            } catch (DisplayLoadException dle) {
                handleTaskRowCreationException(dle);
            }
        });
    }

    public StringProperty chosenCategoryProperty() {
        return chosenCategory;
    }

    private void clearTasks() {
        taskBox.getChildren().clear();
    }

    private void handleTaskRowCreationException(DisplayLoadException dle) {
        log.error("Failed to read task fxml!", dle);
        Text errorText = new Text("Failed to load resource for displaying tasks");
        errorText.setFill(Color.RED);
        taskBox.getChildren().add(errorText);
    }

    private void initTaskDisplayer() {
        taskDisplayer = new TaskDisplayer(taskBox);
        taskDisplayer.setCustomCategories(service.getCustomCategories());
    }

    private void initChosenCategoryProperty() {
        chosenCategory = new SimpleStringProperty();
        chosenCategory.addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                switchCategory(newValue);
            }
        });
    }

}
