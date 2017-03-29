package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task;

import debrecen.university.pti.kovtamas.display.utils.load.DisplayLoadException;
import debrecen.university.pti.kovtamas.display.utils.load.DisplayLoader;
import debrecen.university.pti.kovtamas.display.utils.load.DisplayVo;
import debrecen.university.pti.kovtamas.todo.display.controller.TaskRowController;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.NonNull;

public class TaskDisplayer {

    private VBox taskBox;
    private List<String> customCategories;

    private TaskSelectionSubController taskSelection;

    private List<TaskRepresentations> displayedTasks;
    private Set<Consumer<TaskVo>> registeredTaskChangedActions;

    public TaskDisplayer(VBox taskBox, Collection<String> customCategories) {
        initFields(taskBox, customCategories);
        setupRowModificationAction();
    }

    private void initFields(VBox taskBox, Collection<String> customCategories) {
        this.taskBox = taskBox;
        this.customCategories = new ArrayList<>(customCategories);
        this.displayedTasks = new ArrayList<>();
        this.taskSelection = new TaskSelectionSubController();
        this.registeredTaskChangedActions = new HashSet<>();
    }

    /// TODO
    private void setupRowModificationAction() {
        taskSelection.registerRowModificationAction(modifiedRowController -> {
            // find related task representaion
            TaskRepresentations selectedTaskRepresentation = displayedTasks.stream()
                    .filter(taskRep -> areSameControllers(taskRep.getRowController(), modifiedRowController))
                    .findFirst()
                    .get();

            selectedTaskRepresentation.updateVo();
            executeTaskChangeActions(selectedTaskRepresentation.getVo());
        });
    }

    private boolean areSameControllers(TaskRowController controller, TaskRowController otherController) {
        return controller.getRowId() == otherController.getRowId();
    }

    private void executeTaskChangeActions(TaskVo changedVo) {
        registeredTaskChangedActions.forEach(action -> action.accept(changedVo));
    }

    public void registerTaskChangeAction(@NonNull final Consumer<TaskVo> action) {
        registeredTaskChangedActions.add(action);
    }

    public void newCategoryAddedAction(String newCategory) {
        customCategories.add(newCategory);
        Collections.sort(customCategories);
        updateCategoryComboBoxes();
    }

    public void categoryRemovedAction(String removedCategory) {
        System.out.println("Task Displayer: category removed - " + removedCategory);
        customCategories.remove(removedCategory);
        updateCategoryComboBoxes();
    }

    public void displayAllTasks(@NonNull final Collection<TaskVo> allTopLevelTasks) throws DisplayLoadException {
        for (TaskVo topLevelTask : allTopLevelTasks) {
            displayTaskTree(topLevelTask);
        }
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

    public void toggleDisableForSelectedRow() {
        taskSelection.toggleDisableForSelectedRow();
    }

    public boolean finisedEditing() {
        return taskSelection.finisedEditing();
    }

    private void updateCategoryComboBoxes() {
        displayedTasks.stream()
                .map(TaskRepresentations::getRowController)
                .forEach(controller -> {
                    TaskDisplayState currentTaskState = controller.getTaskStateDetached();
                    currentTaskState.setSelectableCategories(customCategories);
                    controller.setDisplayedTaskState(currentTaskState);
                });
    }

    private void displayTaskTree(TaskVo taskVo, int currentIndentWidth) throws DisplayLoadException {
        displayTask(taskVo, currentIndentWidth);
        displaySubTasksIfPresent(taskVo, currentIndentWidth);
    }

    private void displaySubTasksIfPresent(TaskVo taskData, int indentWidth) throws DisplayLoadException {
        final int additionalIndentWidth = 30;
        if (taskData.hasSubTasks()) {
            for (TaskVo subTask : taskData.getSubTasks()) {
                displayTaskTree(subTask, indentWidth + additionalIndentWidth);
            }
        }
    }

    private void displayTask(TaskVo taskVo, int indentWidth) throws DisplayLoadException {
        TaskDisplayState taskDisplayState = createTaskDisplayState(taskVo, indentWidth);
        TaskRowController taskController = getNewTaskController();

        taskController.setup();
        taskController.setDisplayedTaskState(taskDisplayState);
        taskController.setDisable(true);

        displayedTasks.add(new TaskRepresentations(taskController, taskVo));
        taskSelection.registerTaskRow(taskController);

        putControllerToScreen(taskController);
    }

    private void putControllerToScreen(TaskRowController taskController) {
        taskBox.getChildren().add(taskController.getRootViewComponent());
    }

    private TaskDisplayState createTaskDisplayState(TaskVo task, int indentWidth) {
        return TaskDisplayState.builder()
                .indentWidth(indentWidth)
                .completed(task.isCompleted())
                .priorityColor(PriorityColors.ofPriority(task.getPriority()))
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
