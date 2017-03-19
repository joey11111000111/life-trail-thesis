package debrecen.university.pti.kovtamas.todo.display.controller;

import debrecen.university.pti.kovtamas.display.utils.VoidNoArgMethod;
import debrecen.university.pti.kovtamas.display.utils.locale.LocaleManager;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.CategorySubController;
import debrecen.university.pti.kovtamas.todo.service.api.TodoService;
import java.util.Set;
import java.util.function.Consumer;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.Data;

@Data
public class TodoController {

    private TodoService service;
    private CategorySubController catSubController;
    private LocaleManager localeManager;
    private boolean quoteSwitcherBlocked;

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
    private ListView<String> categoryListView;

    public void startUp() {
        startMotivationTextHandler();
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
        switchedFromTodo();
        backToMenuMethod.execute();
    }

    @FXML
    void moveCategoryDown(ActionEvent event) {
        catSubController.moveSelectedCategory(CategorySubController.Directions.DOWN);
    }

    @FXML
    void moveCategoryUp(ActionEvent event) {
        catSubController.moveSelectedCategory(CategorySubController.Directions.UP);
    }

    @FXML
    void removeCategory(ActionEvent event) {
        // TEMPORARY IMPLEMENTATION
        Set<String> fixCategories = service.getFixCategories();
        String category = catSubController.getSelectedCategory();
        if (!fixCategories.contains(category)) {
            catSubController.removeCategory(category);
        }
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

    private void startMotivationTextHandler() {
        Runnable quotesRunnable = () -> {
            while (true) {
                if (!quoteSwitcherBlocked) {
                    FadeTransition fadeOutText = createEffectAndQuoteChange();
                    fadeOutText.play();
                }

                try {
                    final int quoteOnScreenTime = 90000;
                    Thread.sleep(quoteOnScreenTime);
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted quotes thread!");
                    return;
                }
            }
        };

        Thread quotesThread = new Thread(quotesRunnable);
        quotesThread.setDaemon(true);
        quotesThread.start();
    }

    private FadeTransition createEffectAndQuoteChange() {
        FadeTransition fadeOutText = new FadeTransition(Duration.seconds(3), motivationText);
        fadeOutText.setFromValue(1);
        fadeOutText.setToValue(0);
        fadeOutText.setOnFinished((event) -> {
            String nextQuote = localeManager.getNextMotivationalQuote();
            motivationText.setText(nextQuote);

            FadeTransition fadeInText = new FadeTransition(Duration.seconds(3), motivationText);
            fadeInText.setFromValue(0);
            fadeInText.setToValue(1);
            fadeInText.play();
        });

        return fadeOutText;
    }

    public void switchedToTodo() {
        quoteSwitcherBlocked = false;
    }

    private void switchedFromTodo() {
        quoteSwitcherBlocked = true;
    }

}
