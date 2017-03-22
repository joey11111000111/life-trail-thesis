package debrecen.university.pti.kovtamas.todo.display.controller;

import java.time.LocalDate;
import java.util.Set;
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

public class TaskController {

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

    public void startup() {
        final int textLength = taskDefText.getText().length();
        final double multiplier = 27;

        taskDefText.setMinWidth(textLength * multiplier);
        taskDefText.setPrefWidth(textLength * multiplier);
        taskDefText.setMaxWidth(textLength * multiplier);
    }

    public void setPriorityColor(String colorStyle) {
        priorityIndicator.setStyle(colorStyle);
    }

    public void setCategories(Set<String> selectableCategories) {
        categoryBox.setItems(FXCollections.observableArrayList(selectableCategories));
    }

    public void selectCategory(String selectedCategory) {
        categoryBox.getSelectionModel().select(selectedCategory);
    }

    public void setDeadline(LocalDate deadline) {
        datePicker.setValue(deadline);
    }

    public void setTaskDef(String taskDef) {
        taskDefText.setText(taskDef);
    }

    public void setIndentWidth(int width) {
        indentRegion.setMinWidth(width);
        indentRegion.setPrefWidth(width);
        indentRegion.setMaxWidth(width);
    }

    public void setDone(boolean done) {
        doneCheckBox.setSelected(done);
    }

    public Parent getParent() {
        return taskRow;
    }

    @Override
    public String toString() {
        return "TaskController{" + "taskRow=" + taskRow + ", indentRegion=" + indentRegion + ", doneCheckBox=" + doneCheckBox + ", priorityIndicator=" + priorityIndicator + ", categoryBox=" + categoryBox + ", taskDefText=" + taskDefText + ", datePicker=" + datePicker + '}';
    }

}
