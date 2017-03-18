package debrecen.university.pti.kovtamas.todo.display.vo;

import debrecen.university.pti.kovtamas.todo.display.controller.TodoController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class TodoDisplayVo {

    private Parent root;
    private TodoController controller;
    private FXMLLoader fXMLLoader;

    public TodoDisplayVo() {
    }

    public TodoDisplayVo(Parent root, TodoController controller, FXMLLoader fXMLLoader) {
        this.root = root;
        this.controller = controller;
        this.fXMLLoader = fXMLLoader;
    }

    public Parent getRoot() {
        return root;
    }

    public void setRoot(Parent root) {
        this.root = root;
    }

    public TodoController getController() {
        return controller;
    }

    public void setController(TodoController controller) {
        this.controller = controller;
    }

    public FXMLLoader getfXMLLoader() {
        return fXMLLoader;
    }

    public void setfXMLLoader(FXMLLoader fXMLLoader) {
        this.fXMLLoader = fXMLLoader;
    }

}
