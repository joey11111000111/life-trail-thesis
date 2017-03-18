package debrecen.university.pti.kovtamas.todo.display.controller;

import debrecen.university.pti.kovtamas.display.utils.VoidNoArgMethod;
import debrecen.university.pti.kovtamas.todo.service.api.TodoService;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class TodoController {

    private TodoService service;
    private VoidNoArgMethod backToMenuMethod;
    private Consumer<String> switchLanguageMethod;

    @FXML
    private VBox progressContainer;

    @FXML
    private Rectangle progressRect;

    @FXML
    private Text motivationText;

    @FXML
    private VBox taskBox;

    @FXML
    private ListView<?> categoryListView;

    @FXML
    void addNewCategory(ActionEvent event) {

    }

    @FXML
    void addNewTask(ActionEvent event) {

    }

    @FXML
    void editSelectedTask(ActionEvent event) {

    }

    @FXML
    void goBack(ActionEvent event) {
        backToMenuMethod.execute();
    }

    @FXML
    void moveCategoryDown(ActionEvent event) {

    }

    @FXML
    void moveCategoryUp(ActionEvent event) {

    }

    @FXML
    void removeCategory(ActionEvent event) {

    }

    @FXML
    void removeSelectedTask(ActionEvent event) {

    }

    @FXML
    void setEnLocale(ActionEvent event) {
        switchLanguageMethod.accept("en");
    }

    @FXML
    void setHuLocale(ActionEvent event) {
        switchLanguageMethod.accept("hu");
    }

    public void setBackToMenuMethod(VoidNoArgMethod method) {
        backToMenuMethod = method;
    }

    public void setService(TodoService service) {
        this.service = service;
    }

    public void setSwitchLanguageMethod(Consumer<String> switchLanguageMethod) {
        if (switchLanguageMethod == null) {
            throw new IllegalArgumentException("Language method must not be null!");
        }
        this.switchLanguageMethod = switchLanguageMethod;
    }

}
