package debrecen.university.pti.kovtamas.todo.display.component;

import debrecen.university.pti.kovtamas.display.utils.display.DisplayLoadException;
import debrecen.university.pti.kovtamas.display.utils.display.DisplayLoader;
import debrecen.university.pti.kovtamas.todo.display.controller.TaskController;
import debrecen.university.pti.kovtamas.todo.service.api.TodoService;
import debrecen.university.pti.kovtamas.todo.service.vo.Priority;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskManager {

    private static final Map<Priority, String> PRIORITY_COLOR_MAP;

    static {
        PRIORITY_COLOR_MAP = new HashMap<>();
        PRIORITY_COLOR_MAP.put(Priority.NONE, "-fx-fill: radial-gradient(radius 180%, burlywood,"
                + "derive(rgb(100,100,60), -30%), derive(rgb(100,100,60), 30%));");
        PRIORITY_COLOR_MAP.put(Priority.LOW, "-fx-fill: radial-gradient(radius 180%, silver,"
                + "derive(cadetblue, -30%), derive(cadetblue, 30%));");
        PRIORITY_COLOR_MAP.put(Priority.MEDIUM, "-fx-fill: radial-gradient(radius 180%, burlywood,"
                + "derive(blue, -30%), derive(blue, 30%));");
        PRIORITY_COLOR_MAP.put(Priority.HIGH, "-fx-fill: radial-gradient(radius 180%, burlywood,"
                + "derive(red, -30%), derive(red, 30%));");
    }

    private final TodoService service;
    private final ObservableList<Node> displayList;
    private final ObservableList<TaskController> taskList;

    private final StringProperty chosenCategory;

    public TaskManager(TodoService service, ObservableList<Node> displayList) {
        this.service = service;
        this.displayList = displayList;
        taskList = FXCollections.observableArrayList();

        chosenCategory = new SimpleStringProperty();
        chosenCategory.addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                switchCategory(newValue);
            }
        });
        chosenCategory.setValue("personal");
    }

    public StringProperty chosenCategoryProperty() {
        return chosenCategory;
    }

    private void switchCategory(String category) {
        clear();
        List<TaskVo> tasks = service.getByCategory(category);
        for (TaskVo task : tasks) {
            try {
                setStateAndDisplay(task);
            } catch (DisplayLoadException dle) {
                log.error("Failed to read task fxml!");
                Text errorText = new Text("Failed to load resource for displaying tasks");
                errorText.setFill(Color.RED);
                displayList.add(errorText);
            }
        }
    }

    private void setStateAndDisplay(TaskVo task, int indentWidth) throws DisplayLoadException {
        TaskController controller = (TaskController) DisplayLoader.loadTaskDisplay();
        controller.startup();

        // Set state
        controller.setIndentWidth(indentWidth);
        controller.setDone(false);
        controller.setPriorityColor(PRIORITY_COLOR_MAP.get(task.getPriority()));
        controller.setCategories(service.getAllCategories());
        controller.selectCategory(task.getCategory());
        controller.setTaskDef(task.getTaskDef());
        controller.setDeadline(task.getDeadline());

        displayList.add(controller.getParent());

        // Do the same with subtasks if there are any
        if (task.hasSubTasks()) {
            for (TaskVo subTask : task.getSubTasks()) {
                setStateAndDisplay(subTask, indentWidth + 30);
            }
        }
    }

    private void setStateAndDisplay(TaskVo task) throws DisplayLoadException {
        setStateAndDisplay(task, 0);
    }

    private void clear() {
        displayList.clear();
        taskList.clear();
    }

}
