package debrecen.university.pti.kovtamas.todo.display.controller;

import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task.TaskDisplayState;
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
        setPriorityColor(currentTaskState.getPriorityColorStyle());
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
