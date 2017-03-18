package debrecen.university.pti.kovtamas.application;

import debrecen.university.pti.kovtamas.application.controller.MainMenuController;
import debrecen.university.pti.kovtamas.display.utils.VoidNoArgMethod;
import debrecen.university.pti.kovtamas.display.utils.display.DisplayLoadException;
import debrecen.university.pti.kovtamas.display.utils.display.DisplayLoader;
import debrecen.university.pti.kovtamas.display.utils.locale.LocaleManager;
import debrecen.university.pti.kovtamas.display.utils.display.DisplayVo;
import debrecen.university.pti.kovtamas.todo.display.controller.TodoController;
import debrecen.university.pti.kovtamas.todo.service.api.TodoService;
import debrecen.university.pti.kovtamas.todo.service.impl.CachingTodoService;
import java.util.Locale;
import javafx.application.Application;
import javafx.stage.Stage;

public class ApplicationMain extends Application {

    private LocaleManager localeManager;

    private Stage primaryStage;
    private DisplayLoader.Modules activeModule;

    private DisplayVo menuDisplayVo;
    private DisplayVo todoDisplayVo;
    private DisplayVo journalDisplayVo;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        localeManager = new LocaleManager();
        initDisplayVos(localeManager.getLocale());
        injectControllerDependencies();

        primaryStage.setScene(menuDisplayVo.getDisplayScene());
        activeModule = DisplayLoader.Modules.MENU;
        primaryStage.show();
    }

    private void initDisplayVos(Locale locale) throws DisplayLoadException {
        menuDisplayVo = DisplayLoader.fromFxml(DisplayLoader.Modules.MENU, locale);
        todoDisplayVo = DisplayLoader.fromFxml(DisplayLoader.Modules.TODO, locale);
    }

    private void injectControllerDependencies() {
        // Inject module switch methods
        VoidNoArgMethod switchToMenu = () -> {
            switchScene(DisplayLoader.Modules.MENU);
        };
        VoidNoArgMethod switchToTodo = () -> {
            switchScene(DisplayLoader.Modules.TODO);
        };

        TodoController todoController = (TodoController) todoDisplayVo.getController();
        todoController.setBackToMenuMethod(switchToMenu);
        todoController.setSwitchLanguageMethod(this::switchLanguage);
        MainMenuController menuController = (MainMenuController) menuDisplayVo.getController();
        menuController.setSwitchToTodoMethod(switchToTodo);
        menuController.setSwitchLanguageMethod(this::switchLanguage);

        // Inject service dependencies
        TodoService todoService = new CachingTodoService();
        todoController.setService(todoService);
    }

    private void switchScene(DisplayLoader.Modules module) {
        switch (module) {
            case MENU:
                primaryStage.setScene(menuDisplayVo.getDisplayScene());
                primaryStage.setTitle("");
                activeModule = DisplayLoader.Modules.MENU;
                break;
            case TODO:
                primaryStage.setScene(todoDisplayVo.getDisplayScene());
                primaryStage.setTitle("Task ~ Manager");
                activeModule = DisplayLoader.Modules.TODO;
                break;
            default:
                throw new UnsupportedOperationException("Module not implemented yet!");

        }
    }

    private void switchLanguage(String lang) {
        if (lang == null || lang.isEmpty()) {
            throw new IllegalArgumentException("Language string must not be null or empty!(appmain)");
        }

        if (localeManager.setLanguage(lang)) {
            try {
                initDisplayVos(localeManager.getLocale());
                injectControllerDependencies();
            } catch (DisplayLoadException dle) {
                System.out.println("Failed to set language to " + lang);
                // TODO: handle exception with at least real logging
                return;
            }

            switchScene(activeModule);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
