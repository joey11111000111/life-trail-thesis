package debrecen.university.pti.kovtamas.application.controller;

import debrecen.university.pti.kovtamas.display.utils.VoidNoArgMethod;
import debrecen.university.pti.kovtamas.display.utils.locale.Localizer;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MenuController {

    private VoidNoArgMethod switchToTodoMethod;
    private Consumer<Localizer.SupportedLanguages> switchLanguageMethod;

    public void startup(VoidNoArgMethod switchToTodoMethod,
            Consumer<Localizer.SupportedLanguages> switchLanguageMethod) {

        this.switchToTodoMethod = switchToTodoMethod;
        this.switchLanguageMethod = switchLanguageMethod;
    }

    @FXML
    void switchToSpanishLanguage(ActionEvent event) {
        switchLanguageMethod.accept(Localizer.SupportedLanguages.SPANISH);
    }

    @FXML
    void switchToEnglishLanguage(ActionEvent event) {
        switchLanguageMethod.accept(Localizer.SupportedLanguages.ENGLISH);
    }

    @FXML
    void switchToHungarianLanguage(ActionEvent event) {
        switchLanguageMethod.accept(Localizer.SupportedLanguages.HUNGARIAN);
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

}
