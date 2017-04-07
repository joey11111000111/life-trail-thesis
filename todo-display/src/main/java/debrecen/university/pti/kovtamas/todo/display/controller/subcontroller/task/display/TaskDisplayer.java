package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task.display;

import debrecen.university.pti.kovtamas.display.utils.ValueChangeAction;
import debrecen.university.pti.kovtamas.display.utils.load.DisplayLoadException;
import debrecen.university.pti.kovtamas.display.utils.load.DisplayLoader;
import debrecen.university.pti.kovtamas.display.utils.load.DisplayVo;
import debrecen.university.pti.kovtamas.todo.display.controller.TaskRowController;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task.TaskNode;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.NonNull;

public class TaskDisplayer {

    private VBox taskBox;
    private List<String> customCategories;

    private TaskSelectionSubController taskSelection;

    private List<TaskRepresentations> displayedTasks;
    private Set<ValueChangeAction<TaskVo>> registeredTaskStateChangeActions;

    public TaskDisplayer(VBox taskBox, Collection<String> customCategories) {
        initFields(taskBox, customCategories);
        setupRowModificationAction();
    }

    private void initFields(VBox taskBox, Collection<String> customCategories) {
        this.taskBox = taskBox;
        this.customCategories = new ArrayList<>(customCategories);
        this.customCategories.add("");  // Represents uncategorized
        this.displayedTasks = new ArrayList<>();
        this.taskSelection = new TaskSelectionSubController();
        this.registeredTaskStateChangeActions = new HashSet<>();
    }

    private void setupRowModificationAction() {
        taskSelection.registerRowModificationAction(modifiedRowController -> {
            prepareAndExecuteTaskChangeActions(modifiedRowController);
        });
    }

    private void prepareAndExecuteTaskChangeActions(TaskRowController modifiedRowController) {
        System.out.println("prepare and execute method called");
        TaskRepresentations selectedTaskRepresentations = getRepresentationsByController(modifiedRowController);
        TaskVo selectedOldTaskVo = selectedTaskRepresentations.getUnupdatedDetachedVo();
        TaskVo selectedNewTaskVo = selectedTaskRepresentations.getUpdatedVo();
        executeTaskChangeActions(selectedOldTaskVo, selectedNewTaskVo);
    }

    private TaskRepresentations getRepresentationsByController(TaskRowController controller) {
        return displayedTasks.stream()
                .filter(taskRep -> areSameControllers(taskRep.getRowController(), controller))
                .findFirst()
                .get();
    }

    private boolean areSameControllers(TaskRowController controller, TaskRowController otherController) {
        return controller.getRowId() == otherController.getRowId();
    }

    private void executeTaskChangeActions(TaskVo oldVo, TaskVo newVo) {
        registeredTaskStateChangeActions.forEach(action -> action.accept(oldVo, newVo));
    }

    public void registerTaskStateChangeAction(@NonNull final ValueChangeAction<TaskVo> action) {
        registeredTaskStateChangeActions.add(action);
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

    public boolean hasSelectedTask() {
        return taskSelection.hasSelectedRow();
    }

    public TaskVo getSelectedTask() {
        TaskRowController selectedRow = taskSelection.getSelectedRow();
        if (selectedRow == null) {
            return null;
        }

        return displayedTasks.stream()
                .filter(taskRep -> areSameControllers(taskRep.getRowController(), selectedRow))
                .findFirst()
                .get()
                .getUpdatedVo();
    }

    public TaskVo getRootOfSelectedTaskTree() {
        if (!hasSelectedTask()) {
            throw new IllegalStateException("There isn't a selected item, cannot find root of selected task.");
        }

        TaskVo selectedTask = getSelectedTask();
        for (TaskRepresentations taskRep : displayedTasks) {
            if (doesTaskTreeContain(taskRep.getUpdatedVo(), selectedTask)) {
                return taskRep.getUpdatedVo();
            }
        }

        return null;
    }

    private boolean doesTaskTreeContain(TaskVo taskTree, TaskVo taskToFind) {
        if (taskTree == taskToFind) {
            return true;
        }

        if (taskTree.hasSubTasks()) {
            boolean contains = false;
            for (TaskVo subTask : taskTree.getSubTasks()) {
                contains |= doesTaskTreeContain(subTask, taskToFind);
            }

            return contains;
        }

        return false;
    }

    public void displayAllTasks(@NonNull final Collection<TaskVo> allTopLevelTasks) throws DisplayLoadException {
        for (TaskVo topLevelTask : allTopLevelTasks) {
            displayTaskTree(topLevelTask);
        }
    }

    public void displayTaskTree(TaskVo rootTask) throws DisplayLoadException {
        final int defaultIndentWidth = 0;
        displayTaskTree(new TaskNode(TaskNode.NO_PARENT, rootTask), defaultIndentWidth);
    }

    private void displayTaskTree(TaskNode taskNode, int currentIndentWidth) throws DisplayLoadException {
        displayTask(taskNode, currentIndentWidth);
        displaySubTasksIfPresent(taskNode, currentIndentWidth);
    }

    private void displayTask(TaskNode taskNode, int indentWidth) throws DisplayLoadException {
        TaskDisplayState taskDisplayState = createTaskDisplayState(taskNode.getVo(), indentWidth);
        TaskRowController taskController = getNewTaskController();

        taskController.setup();
        taskController.setDisplayedTaskState(taskDisplayState);
        taskController.setDisable(true);
        taskController.registerTaskCompletionChangeAction(this::prepareAndExecuteTaskChangeActions);

        displayedTasks.add(new TaskRepresentations(taskController, taskNode));
        taskSelection.registerTaskRow(taskController);

        putControllerToScreen(taskController);
    }

    private void displaySubTasksIfPresent(TaskNode taskNode, int indentWidth) throws DisplayLoadException {
        final int additionalIndentWidth = 30;
        final TaskVo nodeVo = taskNode.getVo();
        if (nodeVo.hasSubTasks()) {
            for (TaskVo subTask : nodeVo.getSubTasks()) {
                displayTaskTree(new TaskNode(taskNode, subTask), indentWidth + additionalIndentWidth);
            }
        }
    }

    private void putControllerToScreen(TaskRowController taskController) {
        taskBox.getChildren().add(taskController.getRootViewComponent());
    }

    public void clear() {
        taskBox.getChildren().clear();
        taskSelection.clearSelection();
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
        return taskSelection.hasFinisedEditing();
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
