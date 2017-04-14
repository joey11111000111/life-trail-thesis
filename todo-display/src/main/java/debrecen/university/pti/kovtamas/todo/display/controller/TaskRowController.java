package debrecen.university.pti.kovtamas.todo.display.controller;

import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task.display.TaskDisplayState;
import debrecen.university.pti.kovtamas.todo.service.vo.CategoryVo;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
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
import lombok.NonNull;

public class TaskRowController {

    static private long nextId = 0;

    private final long rowId;
    private boolean isDisabled;
    private TaskDisplayState currentTaskState;
    private List<CategoryVo> selectableCategories;

    public TaskRowController() {
        this.rowId = nextId++;
        isDisabled = false;
        currentTaskState = null;
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

    public void setup() {
        taskDefText.textProperty().addListener((observable, oldText, newText) -> {
            currentTaskState.setTaskDef(newText);
        });
        categoryBox.getSelectionModel().selectedItemProperty().addListener((observable, oldCategory, newCategory) -> {
            CategoryVo newCategoryVo = getVoByName(newCategory);
            currentTaskState.setSelectedCategory(newCategoryVo);
        });
        datePicker.valueProperty().addListener((observable, oldDate, newDate) -> {
            currentTaskState.setDeadline(newDate);
        });
        doneCheckBox.selectedProperty().addListener((observable, wasSelectedBefore, isSelectedNow) -> {
            currentTaskState.setCompleted(isSelectedNow);
        });
    }

    private CategoryVo getVoByName(String categoryName) {
        return selectableCategories.stream()
                .filter(cusCat -> Objects.equals(cusCat.getName(), categoryName))
                .findAny()
                .get();
    }

    public void registerTaskCompletionChangeAction(@NonNull final Consumer<TaskRowController> action) {
        doneCheckBox.selectedProperty().addListener(
                (observable, wasSelectedBefore, isSelectedNow) -> {
                    action.accept(this);
                }
        );
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

    private void setSelectableCategories(List<CategoryVo> selectableCategories) {
        this.selectableCategories = selectableCategories;
        List<String> selectableCategoryNames = selectableCategories.stream()
                .map(CategoryVo::getName)
                .collect(Collectors.toList());

        categoryBox.setItems(FXCollections.observableArrayList(selectableCategoryNames));
    }

    private void setSelectedCategory(CategoryVo selectedCategory) {
        if (selectedCategory != null) {
            categoryBox.getSelectionModel().select(selectedCategory.getName());
        } else {
            categoryBox.getSelectionModel().select("");
        }
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
