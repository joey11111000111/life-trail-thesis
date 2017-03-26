package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task;

import debrecen.university.pti.kovtamas.display.utils.display.DisplayLoadException;
import debrecen.university.pti.kovtamas.display.utils.display.DisplayLoader;
import debrecen.university.pti.kovtamas.display.utils.display.DisplayVo;
import debrecen.university.pti.kovtamas.todo.display.controller.TaskRowController;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.scene.layout.VBox;

public class TaskDisplayer {

    private final VBox taskBox;
    private List<String> customCategories;

    public TaskDisplayer(VBox taskBox) {
        this.taskBox = taskBox;
    }

    public void setCustomCategories(Collection<String> customCategories) {
        this.customCategories = new ArrayList<>(customCategories);
    }

    public void displayTaskTree(TaskVo rootTask) throws DisplayLoadException {
        final int defaultIndentWidth = 0;
        displayTaskTree(rootTask, defaultIndentWidth);
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
