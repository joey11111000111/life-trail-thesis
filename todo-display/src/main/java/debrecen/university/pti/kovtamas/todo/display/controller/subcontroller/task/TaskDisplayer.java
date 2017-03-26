package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task;

import debrecen.university.pti.kovtamas.display.utils.display.DisplayLoadException;
import debrecen.university.pti.kovtamas.display.utils.display.DisplayLoader;
import debrecen.university.pti.kovtamas.display.utils.display.DisplayVo;
import debrecen.university.pti.kovtamas.todo.display.controller.TaskRowController;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class TaskDisplayer {

    private final VBox taskBox;
    private final List<TaskRowController> displayedTaskControllers;
    private List<String> customCategories;

    public TaskDisplayer(VBox taskBox) {
        this.taskBox = taskBox;
        displayedTaskControllers = new ArrayList<>();
    }

    public void setCustomCategories(Collection<String> newCustomCategories) {
        customCategories = new ArrayList<>(newCustomCategories);
        Collections.sort(customCategories);
        updateCategoryComboBoxes();
    }

    public void addNewCategory(String newCategory) {
        customCategories.add(newCategory);
        Collections.sort(customCategories);
        updateCategoryComboBoxes();
    }

    public void displayTaskTree(TaskVo rootTask) throws DisplayLoadException {
        final int defaultIndentWidth = 0;
        displayTaskTree(rootTask, defaultIndentWidth);
    }

    public void clear() {
        taskBox.getChildren().clear();
    }

    public void displayErrorMessage(String errorMessage) {
        Text errorText = new Text(errorMessage);
        errorText.setFill(Color.RED);
        taskBox.getChildren().add(errorText);
    }

    private void updateCategoryComboBoxes() {
        displayedTaskControllers.forEach(controller -> {
            TaskDisplayState currentTaskState = controller.getTaskStateDetached();
            currentTaskState.setSelectableCategories(customCategories);
        });
    }

    private void displayTaskTree(TaskVo taskData, int currentIndentWidth) throws DisplayLoadException {
        TaskDisplayState taskDisplayState = createTaskDisplayState(taskData, currentIndentWidth);
        displayTask(taskDisplayState);
        displaySubTasksIfPresent(taskData, currentIndentWidth);
    }

    private void displaySubTasksIfPresent(TaskVo taskData, int indentWidth) throws DisplayLoadException {
        final int additionalIndentWidth = 30;
        if (taskData.hasSubTasks()) {
            for (TaskVo subTask : taskData.getSubTasks()) {
                displayTaskTree(subTask, indentWidth + additionalIndentWidth);
            }
        }
    }

    private void displayTask(TaskDisplayState taskDisplayState) throws DisplayLoadException {
        TaskRowController taskController = getNewTaskController();
        taskController.setDisplayedTaskState(taskDisplayState);
        displayedTaskControllers.add(taskController);
        putControllerToScreen(taskController);
    }

    private void putControllerToScreen(TaskRowController taskController) {
        taskBox.getChildren().add(taskController.getRootViewComponent());
    }

    private TaskDisplayState createTaskDisplayState(TaskVo task, int indentWidth) {
        return TaskDisplayState.builder()
                .indentWidth(indentWidth)
                .completed(task.isCompleted())
                .priorityColorStyle(PriorityColors.getColorStyleOfPriority(task.getPriority()))
                .selectableCategories(customCategories)
                .selectedCategory(task.getCategory())
                .taskDef(task.getTaskDef())
                .deadline(task.getDeadline())
                .build();
    }

    private TaskRowController getNewTaskController() throws DisplayLoadException {
        DisplayVo taskRowDisplayVo = DisplayLoader.loadFxmlWithResource(DisplayLoader.FxmlFiles.TASK_FXML, null);
        return (TaskRowController) taskRowDisplayVo.getController();
    }

}
