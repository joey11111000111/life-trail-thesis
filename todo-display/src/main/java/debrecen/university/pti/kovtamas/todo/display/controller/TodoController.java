package debrecen.university.pti.kovtamas.todo.display.controller;

import debrecen.university.pti.kovtamas.display.utils.ValueChangeAction;
import debrecen.university.pti.kovtamas.display.utils.VoidNoArgMethod;
import debrecen.university.pti.kovtamas.display.utils.locale.Localizer;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task.TaskSubController;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.CategorySubController;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.MotivationSubController;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.CategoryPositioner;
import debrecen.university.pti.kovtamas.todo.service.api.TodoService;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
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

    private VoidNoArgMethod backToMenuMethod;
    private Consumer<Localizer.SupportedLanguages> switchLanguageMethod;

    @FXML
    @Getter
    private VBox progressContainer;

    @FXML
    @Getter
    private Rectangle progressRect;

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
        connectTaskDisplayToCategoryChange();
    }

    private void initFields(TodoControllerDependencies dependencies) {
        initDependencies(dependencies);
        initSubControllers();
    }

    private void initDependencies(TodoControllerDependencies dependencies) {
        this.service = dependencies.getService();
        this.backToMenuMethod = dependencies.getBackToMenuMethod();
        this.switchLanguageMethod = dependencies.getSwitchLanguageMethod();
    }

    private void initSubControllers() {
        List<String> customCategories = new ArrayList<>(service.getCustomCategories());
        categorySubController = new CategorySubController(categoryListView, customCategories);
        motivationSubController = new MotivationSubController(motivationText);
        taskSubController = new TaskSubController(service, taskBox);
    }

    private void connectTaskDisplayToCategoryChange() {
        StringProperty chosenCategoryProperty = taskSubController.chosenCategoryProperty();
        ValueChangeAction<String> chosenCategoryChangedAction = (fromCategory, toCategory) -> {
            chosenCategoryProperty.set(toCategory);
        };
        categorySubController.registerCategoryChangeAction(chosenCategoryChangedAction);
    }

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
    }

    @FXML
    void setEnLocale(ActionEvent event) {
        switchLanguageMethod.accept(Localizer.SupportedLanguages.ENGLISH);
    }

    @FXML
    void setHuLocale(ActionEvent event) {
        switchLanguageMethod.accept(Localizer.SupportedLanguages.HUNGARIAN);
    }

    public void todoScreenActivatedAction() {
        motivationSubController.startMotivationTextChanger();
    }

    public void todoScreenDeactivatedAction() {
        motivationSubController.stopMotivationTextChanger();
    }

}
