package debrecen.university.pti.kovtamas.application;

import debrecen.university.pti.kovtamas.display.utils.Modules;
import debrecen.university.pti.kovtamas.application.controller.MenuController;
import debrecen.university.pti.kovtamas.display.utils.VoidNoArgMethod;
import debrecen.university.pti.kovtamas.display.utils.display.DisplayLoadException;
import debrecen.university.pti.kovtamas.display.utils.display.DisplayLoader;
import debrecen.university.pti.kovtamas.display.utils.locale.LocaleManager;
import debrecen.university.pti.kovtamas.display.utils.display.DisplayVo;
import debrecen.university.pti.kovtamas.todo.display.component.TaskManager;
import debrecen.university.pti.kovtamas.todo.display.controller.TodoController;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.CategorySubController;
import debrecen.university.pti.kovtamas.todo.service.api.TodoService;
import debrecen.university.pti.kovtamas.todo.service.impl.CachingTodoService;
import java.util.Locale;
import java.util.Set;
import javafx.application.Application;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class ApplicationMain extends Application {

    private LocaleManager localeManager;

    private Stage primaryStage;
    private Modules activeModule;

    private DisplayVo menuDisplayVo;
    private DisplayVo todoDisplayVo;
    private DisplayVo journalDisplayVo;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        localeManager = LocaleManager.getInstance();
        initDisplayVos(localeManager.getLocale());
        injectControllerDependencies();

        primaryStage.setScene(menuDisplayVo.getDisplayScene());
        activeModule = Modules.MENU;
        primaryStage.show();
    }

    private void initDisplayVos(Locale locale) throws DisplayLoadException {
        menuDisplayVo = DisplayLoader.load(Modules.MENU, locale);
        todoDisplayVo = DisplayLoader.load(Modules.TODO, locale);
    }

    private void injectControllerDependencies() {
        // Inject module switch methods
        VoidNoArgMethod switchToMenu = () -> {
            switchScene(Modules.MENU);
        };
        VoidNoArgMethod switchToTodo = () -> {
            ((TodoController) todoDisplayVo.getController()).switchedToTodo();
            switchScene(Modules.TODO);
        };

        // Inject todo controller dependencies
        TodoController todoController = (TodoController) todoDisplayVo.getController();
        todoController.setBackToMenuMethod(switchToMenu);
        todoController.setSwitchLanguageMethod(this::switchLanguage);
        TodoService todoService = new CachingTodoService();
        todoController.setService(todoService);
        todoController.setLocaleManager(localeManager);
        // Category sub controller
        ListView listView = todoController.getCategoryListView();
        Set<String> allCategories = todoService.getAllCategories();
        CategorySubController catSubController = new CategorySubController(listView, allCategories);
        todoController.setTaskManager(new TaskManager(todoService, todoController.getTaskBox().getChildren()));
        todoController.setCatSubController(catSubController);
        todoController.startUp();

        // Inject menu controller dependencies
        MenuController menuController = (MenuController) menuDisplayVo.getController();
        menuController.setSwitchToTodoMethod(switchToTodo);
        menuController.setSwitchLanguageMethod(this::switchLanguage);

        // Inject service dependencies
    }

    private void switchScene(Modules module) {
        switch (module) {
            case MENU:
                primaryStage.setScene(menuDisplayVo.getDisplayScene());
                primaryStage.setTitle("");
                activeModule = Modules.MENU;
                break;
            case TODO:
                primaryStage.setScene(todoDisplayVo.getDisplayScene());
                primaryStage.setTitle("Task ~ Manager");
                activeModule = Modules.TODO;
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
                createReLocalizedDisplayVos();
            } catch (DisplayLoadException dle) {
                System.out.println("Failed to set language to " + lang);
                // TODO: handle exception with at least real logging
                return;
            }

            switchScene(activeModule);
        }
    }

    private void createReLocalizedDisplayVos() throws DisplayLoadException {
        // Create new, re-localized display vos
        DisplayVo newMenuDisplayVo = DisplayLoader.load(Modules.MENU, localeManager.getLocale());
        DisplayVo newTodoDisplayVo = DisplayLoader.load(Modules.TODO, localeManager.getLocale());

        // Inject dependencies of menu controller from the previous one
        MenuController oldMenuController = (MenuController) menuDisplayVo.getController();
        MenuController newMenuController = (MenuController) newMenuDisplayVo.getController();
        newMenuController.setSwitchLanguageMethod(oldMenuController.getSwitchLanguageMethod());
        newMenuController.setSwitchToTodoMethod(oldMenuController.getSwitchToTodoMethod());

        // Inject dependencies of todo controller from the previous one
        TodoController oldTodoController = (TodoController) todoDisplayVo.getController();
        TodoController newTodoController = (TodoController) newTodoDisplayVo.getController();
        newTodoController.setSwitchLanguageMethod(oldTodoController.getSwitchLanguageMethod());
        newTodoController.setBackToMenuMethod(oldTodoController.getBackToMenuMethod());
        newTodoController.setService(oldTodoController.getService());
        newTodoController.setLocaleManager(localeManager);
        newTodoController.setTaskManager(oldTodoController.getTaskManager());
        // Inject new category sub controller
        CategorySubController oldCatSubCtrl = oldTodoController.getCatSubController();
        CategorySubController newCatSubCrtl = new CategorySubController(newTodoController.getCategoryListView(), oldCatSubCtrl.getCategoryList());
        newCatSubCrtl.setSelectedCategory(oldCatSubCtrl.getSelectedCategory());
        newTodoController.setCatSubController(newCatSubCrtl);
        newTodoController.startUp();

        // Do the switch from old to new
        menuDisplayVo = newMenuDisplayVo;
        todoDisplayVo = newTodoDisplayVo;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
