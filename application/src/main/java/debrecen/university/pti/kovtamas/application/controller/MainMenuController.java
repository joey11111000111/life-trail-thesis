package debrecen.university.pti.kovtamas.application.controller;

import debrecen.university.pti.kovtamas.display.utils.VoidNoArgMethod;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MainMenuController {

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

    public void setSwitchToTodoMethod(VoidNoArgMethod method) {
        this.switchToTodoMethod = method;
    }

    public void setSwitchLanguageMethod(Consumer<String> switchLanguageMethod) {
        if (switchLanguageMethod == null) {
            throw new IllegalArgumentException("Language method must not be null!");
        }
        this.switchLanguageMethod = switchLanguageMethod;
    }

}
