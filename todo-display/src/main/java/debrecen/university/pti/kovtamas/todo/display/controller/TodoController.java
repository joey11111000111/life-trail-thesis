package debrecen.university.pti.kovtamas.todo.display.controller;

import debrecen.university.pti.kovtamas.display.utils.VoidNoArgMethod;
import debrecen.university.pti.kovtamas.display.utils.locale.Localizer;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task.TaskSubController;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.CategorySubController;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.motivation.MotivationSubController;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.CategoryActions;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.CategoryPositioner;
import debrecen.university.pti.kovtamas.todo.service.api.TodoService;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import lombok.Getter;

public class TodoController {

    private TodoService service;

    // Sub controllers
    private TaskSubController taskSubController;
    private CategorySubController categorySubController;
    private MotivationSubController motivationSubController;

    // Methods from higher layers
    private VoidNoArgMethod backToMenuMethod;
    private Consumer<Localizer.SupportedLanguages> switchLanguageMethod;

    @FXML
    @Getter
    private VBox progressContainer;

    @FXML
    @Getter
    private Rectangle progressRect;

    @FXML
    private HBox motivationContainer;

    @FXML
    @Getter
    private Text motivationText;

    @FXML
    @Getter
    private VBox taskBox;

    @FXML
    @Getter
    private ListView<String> categoryListView;

    public void startUp(TodoControllerDependencies dependencies) {
        initFields(dependencies);
        makeBindingsBetweenSubControllers();
    }

    private void initFields(TodoControllerDependencies dependencies) {
        initDependencies(dependencies);
        createSubControllers();
    }

    private void initDependencies(TodoControllerDependencies dependencies) {
        this.service = dependencies.getService();
        this.backToMenuMethod = dependencies.getBackToMenuMethod();
        this.switchLanguageMethod = dependencies.getSwitchLanguageMethod();
    }

    private void createSubControllers() {
        categorySubController = new CategorySubController(categoryListView, service);
        motivationSubController = new MotivationSubController(motivationContainer, motivationText);
        taskSubController = TaskSubController.builder()
                .service(service)
                .taskBox(taskBox)
                .progressContainer(progressContainer)
                .progressIndicator(progressRect)
                .build();
    }

    private void makeBindingsBetweenSubControllers() {
        CategoryActions categoryActions = categorySubController.getCategoryActions();
        categoryActions.registerSelectionChangeAction(taskSubController::selectedCategoryChangedAction);
        categoryActions.registerNewCategoryAction(taskSubController::newCategoryAddedAction);
        categoryActions.registerRemoveCategoryAction(taskSubController::categoryRemovedAction);
    }

    @FXML
    void addNewCategory(ActionEvent event) {
    }

    @FXML
    void addNewTask(ActionEvent event) {
        taskSubController.addNewTask();
    }

    @FXML
    void editSelectedTask(ActionEvent event) {
        taskSubController.toggleDisableForSelectedRow();
    }

    @FXML
    void goBack(ActionEvent event) {
        backToMenuMethod.execute();
    }

    @FXML
    void moveCategoryDown(ActionEvent event) {
        categorySubController.moveSelectedCategoryIfPossible(CategoryPositioner.Directions.DOWN);
    }

    @FXML
    void moveCategoryUp(ActionEvent event) {
        categorySubController.moveSelectedCategoryIfPossible(CategoryPositioner.Directions.UP);
    }

    @FXML
    void removeCategory(ActionEvent event) {
        categorySubController.removeSelectedCategory();
    }

    @FXML
    void removeSelectedTask(ActionEvent event) {
        taskSubController.removeSelectedTask();
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

    public void todoScreenActivatedAction() {
        motivationSubController.startMotivationTextChanger();
    }

    public void todoScreenDeactivatedAction() {
        motivationSubController.stopMotivationTextChanger();
    }

}
