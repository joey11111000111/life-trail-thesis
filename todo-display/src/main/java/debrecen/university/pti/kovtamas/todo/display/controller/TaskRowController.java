package debrecen.university.pti.kovtamas.todo.display.controller;

import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task.display.TaskDisplayState;
import java.time.LocalDate;
import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;

public class TaskRowController {

    private static long nextId = 0;

    private final long rowId;
    private boolean isDisabled;

    public TaskRowController() {
        this.rowId = nextId++;
        isDisabled = false;
    }

    @FXML
    private HBox taskRow;

    @FXML
    private Region indentRegion;

    @FXML
    private CheckBox doneCheckBox;

    @FXML
    private Circle priorityIndicator;

    @FXML
    private ComboBox<String> categoryBox;

    @FXML
    private TextField taskDefText;

    @FXML
    private DatePicker datePicker;

    private TaskDisplayState currentTaskState;

    public void setup() {
        taskDefText.textProperty().addListener((observable, oldText, newText) -> {
            currentTaskState.setTaskDef(newText);
        });
        categoryBox.getSelectionModel().selectedItemProperty().addListener((observable, oldCategory, newCategory) -> {
            currentTaskState.setSelectedCategory(newCategory);
        });
        datePicker.valueProperty().addListener((observable, oldDate, newDate) -> {
            currentTaskState.setDeadline(newDate);
        });
    }

    public Parent getRootViewComponent() {
        return taskRow;
    }

    public void setDisplayedTaskState(TaskDisplayState taskState) {
        this.currentTaskState = taskState;
        updateTaskDisplay();
    }

    public TaskDisplayState getTaskStateDetached() {
        return currentTaskState;
    }

    public void toggleDisabled() {
        setDisable(!isDisabled);
    }

    public void setDisable(boolean isDisabled) {
        if (this.isDisabled != isDisabled) {
            this.isDisabled = isDisabled;
            categoryBox.setDisable(isDisabled);
            taskDefText.setDisable(isDisabled);
            datePicker.setDisable(isDisabled);
            // Using the checkbox is not part of editing a task.
            // The state of an under-editing task should not be changed.
            doneCheckBox.setDisable(!isDisabled);
        }
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public boolean isEditable() {
        return !isDisabled;
    }

    public long getRowId() {
        return rowId;
    }

    private void setPriorityColor(String colorStyle) {
        priorityIndicator.setStyle(colorStyle);
    }

    private void setSelectableCategories(Collection<String> selectableCategories) {
        categoryBox.setItems(FXCollections.observableArrayList(selectableCategories));
    }

    private void setSelectedCategory(String selectedCategory) {
        categoryBox.getSelectionModel().select(selectedCategory);
    }

    private void setDeadline(LocalDate deadline) {
        datePicker.setValue(deadline);
    }

    private void setTaskDef(String taskDef) {
        taskDefText.setText(taskDef);
    }

    private void setIndentWidth(int width) {
        indentRegion.setMinWidth(width);
        indentRegion.setPrefWidth(width);
        indentRegion.setMaxWidth(width);
    }

    private void setCompleted(boolean done) {
        doneCheckBox.setSelected(done);
    }

    private void updateTaskDisplay() {
        setIndentWidth(currentTaskState.getIndentWidth());
        setCompleted(currentTaskState.isCompleted());
        setPriorityColor(currentTaskState.getPriorityColor().getColorStyle());
        setSelectableCategories(currentTaskState.getSelectableCategories());
        setSelectedCategory(currentTaskState.getSelectedCategory());
        setTaskDef(currentTaskState.getTaskDef());
        setDeadline(currentTaskState.getDeadline());
    }

    @Override
    public String toString() {
        return "TaskController{" + "taskRow=" + taskRow + ", indentRegion=" + indentRegion + ", doneCheckBox=" + doneCheckBox + ", priorityIndicator=" + priorityIndicator + ", categoryBox=" + categoryBox + ", taskDefText=" + taskDefText + ", datePicker=" + datePicker + '}';
    }

}
