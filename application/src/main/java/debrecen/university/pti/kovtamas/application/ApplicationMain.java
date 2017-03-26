package debrecen.university.pti.kovtamas.application;

import debrecen.university.pti.kovtamas.display.utils.Modules;
import debrecen.university.pti.kovtamas.application.controller.MenuController;
import debrecen.university.pti.kovtamas.display.utils.ValueChangeAction;
import debrecen.university.pti.kovtamas.display.utils.display.DisplayLoadException;
import debrecen.university.pti.kovtamas.display.utils.display.DisplayLoader;
import debrecen.university.pti.kovtamas.display.utils.display.DisplayLoader.FxmlFiles;
import debrecen.university.pti.kovtamas.display.utils.locale.Localizer;
import debrecen.university.pti.kovtamas.display.utils.display.DisplayVo;
import debrecen.university.pti.kovtamas.display.utils.locale.Localizer.ResourcePaths;
import debrecen.university.pti.kovtamas.todo.display.controller.TodoController;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationMain extends Application {

    private ControllerDependencyInjector injector;
    private Localizer localizer;
    private Stage primaryStage;
    private ViewBuffer viewBuffer;

    @Override
    public void start(Stage primaryStage) throws Exception {
        initFields(primaryStage);
        injectControllerDependencies();
        wireUpControllerActions();
        showMenu();
    }

    private void initFields(Stage primaryStage) throws DisplayLoadException {
        this.primaryStage = primaryStage;
        this.localizer = Localizer.getInstance();
        this.viewBuffer = new ViewBuffer(Modules.MENU);
        setupViewBuffer();
        initInjector();
    }

    private void wireUpControllerActions() {
        TodoController todoController = (TodoController) viewBuffer.getController(Modules.TODO);

        ValueChangeAction<Modules> activationDeactivationAction = (fromModule, toModule) -> {
            if (fromModule == Modules.TODO) {
                todoController.todoScreenDeactivatedAction();
                return;
            }
            if (toModule == Modules.TODO) {
                todoController.todoScreenActivatedAction();
            }
        };

        viewBuffer.registerActiveModuleChangeAction(activationDeactivationAction);
    }

    private void showMenu() {
        primaryStage.setScene(viewBuffer.getScene(Modules.MENU));
        primaryStage.show();
    }

    private void initInjector() {
        injector = ControllerDependencyInjector.builder()
                .switchLanguageMethod(this::switchLanguage)
                .switchSceneMethod(this::switchScene)
                .build();
    }

    private void setupViewBuffer() throws DisplayLoadException {
        DisplayVo menuDisplayVo = DisplayLoader.loadFxmlWithResource(FxmlFiles.MENU_FXML, ResourcePaths.MENU_RES_PATH);
        DisplayVo todoDisplayVo = DisplayLoader.loadFxmlWithResource(FxmlFiles.TODO_FXML, ResourcePaths.TODO_RES_PATH);

        viewBuffer.putScene(Modules.MENU, menuDisplayVo.createScene());
        viewBuffer.putScene(Modules.TODO, todoDisplayVo.createScene());
        viewBuffer.putController(Modules.MENU, menuDisplayVo.getController());
        viewBuffer.putController(Modules.TODO, todoDisplayVo.getController());
        viewBuffer.putTitle(Modules.MENU, "");
        viewBuffer.putTitle(Modules.TODO, "Task ~ Manager");
    }

    private void injectControllerDependencies() {
        TodoController todoController = (TodoController) viewBuffer.getController(Modules.TODO);
        MenuController menuController = (MenuController) viewBuffer.getController(Modules.MENU);
        injector.injectTodoControllerDependenciesInto(todoController);
        injector.injectMenuControllerDependenciesInto(menuController);
    }

    private void switchScene(Modules module) {
        primaryStage.setScene(viewBuffer.getScene(module));
        viewBuffer.setActiveModule(module);
        primaryStage.setTitle(viewBuffer.getTitle(module));
    }

    private void switchLanguage(@NonNull Localizer.SupportedLanguages language) {
        if (localizer.setLanguage(language)) {
            try {
                languageChangeAction();
            } catch (DisplayLoadException dle) {
                log.error("Failed to reload views! Keep language: " + language.name(), dle);
            }
        }
    }

    private void languageChangeAction() throws DisplayLoadException {
        Modules activeModule = viewBuffer.getActiveModule();
        recreateLanguageDependentComponents();
        switchScene(activeModule);
    }

    private void recreateLanguageDependentComponents() throws DisplayLoadException {
        recreateViews();
        injectControllerDependencies();
        wireUpControllerActions();
    }

    private void recreateViews() throws DisplayLoadException {
        viewBuffer.deactivateBeforeShutdown();
        setupViewBuffer();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
