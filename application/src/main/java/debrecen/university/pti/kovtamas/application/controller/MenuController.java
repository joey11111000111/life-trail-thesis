package debrecen.university.pti.kovtamas.application.controller;

import debrecen.university.pti.kovtamas.display.utils.VoidNoArgMethod;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MenuController {

    private VoidNoArgMethod switchToTodoMethod;
    private Consumer<String> switchLanguageMethod;

    @FXML
    void setEnLocale(ActionEvent event) {
        switchLanguageMethod.accept("en");
    }

    @FXML
    void setHuLocale(ActionEvent event) {
        switchLanguageMethod.accept("hu");
    }

    @FXML
    void switchToTodo(ActionEvent event) {
        switchToTodoMethod.execute();
    }

    @FXML
    void swtichToJournal(ActionEvent event) {
        System.out.println("Journal feature not implemented yet");
    }

    public VoidNoArgMethod getSwitchToTodoMethod() {
        return switchToTodoMethod;
    }

    public void setSwitchToTodoMethod(VoidNoArgMethod switchToTodoMethod) {
        this.switchToTodoMethod = switchToTodoMethod;
    }

    public Consumer<String> getSwitchLanguageMethod() {
        return switchLanguageMethod;
    }

    public void setSwitchLanguageMethod(Consumer<String> switchLanguageMethod) {
        this.switchLanguageMethod = switchLanguageMethod;
    }

}
