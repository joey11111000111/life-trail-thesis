package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category;

import debrecen.university.pti.kovtamas.display.utils.ValueChangeAction;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CategorySubController {

    private ListView<String> categoryListView;
    private LogicalCategoryNames logicalCategoryNames;
    private CategoryPositioner categoryPositioner;
    private boolean isListEventBlocked;
    private Set<ValueChangeAction<String>> registeredCategoryChangeActions;

    public CategorySubController(ListView<String> categoryListView, Collection<String> customCategories) {
        initFields(categoryListView);
        setupListView(customCategories);
    }

    public void moveSelectedCategoryIfPossible(CategoryPositioner.Directions direction) {
        categoryPositioner.moveSelectedCategoryIfPossible(direction);
    }

    public void removeSelectedCategory() {
        String selectedCategoryName = getSelectedCategory();
        if (!logicalCategoryNames.isLogicalCategory(selectedCategoryName)) {
            categoryListView.getItems().remove(selectedCategoryName);
        }
    }

    public void addNewCategory(String categoryName) {
        categoryListView.getItems().add(categoryName);
    }

    public void setSelectedCategory(String category) {
        categoryListView.getSelectionModel().select(category);
    }

    public void registerCategoryChangeAction(ValueChangeAction<String> action) {
        registeredCategoryChangeActions.add(action);
    }

    private String getSelectedCategory() {
        return categoryListView.getSelectionModel().getSelectedItem();
    }

    private void initFields(ListView<String> categoryListView) {
        this.categoryListView = categoryListView;
        this.categoryPositioner = buildCategoryPositioner();
        this.logicalCategoryNames = LogicalCategoryNames.getInstance();
        this.registeredCategoryChangeActions = new HashSet<>();
        this.isListEventBlocked = false;
    }

    private CategoryPositioner buildCategoryPositioner() {
        return CategoryPositioner.builder()
                .categoryListView(categoryListView)
                .blockListEvent(this::blockListEvent)
                .releaseListEvent(this::releaseListEvent)
                .build();
    }

    private void setupListView(Collection<String> customCategories) {
        ObservableList<String> allCategories = collectCategories(customCategories);
        setupListView(allCategories);
    }

    private void setupListView(ObservableList<String> allCategories) {
        categoryListView.setItems(allCategories);
        categoryListView.getSelectionModel()
                .selectedItemProperty()
                .addListener(this::categorySelectionAction);
    }

    private ObservableList<String> collectCategories(Collection<String> customCategories) {
        ObservableList<String> categories = FXCollections.observableArrayList();
        categories.addAll(logicalCategoryNames.getLocalizedNames());
        categories.addAll(customCategories);
        return categories;
    }

    private void blockListEvent() {
        isListEventBlocked = true;
    }

    private void releaseListEvent() {
        isListEventBlocked = false;
    }

    private void categorySelectionAction(ObservableValue<? extends String> observable,
            String fromCategory, String toCategory) {

        if (!isListEventBlocked) {
            log.info("Selected category changed from '" + fromCategory + "' to '" + toCategory + "'");
            registeredCategoryChangeActions.forEach(action -> action.accept(fromCategory, toCategory));
        }
    }

}
