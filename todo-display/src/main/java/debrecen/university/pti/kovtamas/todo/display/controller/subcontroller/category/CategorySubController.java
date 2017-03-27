package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category;

import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CategorySubController {

    private ListView<String> categoryListView;
    private LogicalCategoryNames logicalCategoryNames;
    private CategoryPositioner categoryPositioner;
    private CategoryActions categoryActions;

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
            categoryActions.categoryRemoved(selectedCategoryName);
        }
    }

    public void addNewCategory(String categoryName) {
        categoryListView.getItems().add(categoryName);
    }

    public void setSelectedCategory(String category) {
        categoryListView.getSelectionModel().select(category);
    }

    public CategoryActions getCategoryActions() {
        return categoryActions;
    }

    private String getSelectedCategory() {
        return categoryListView.getSelectionModel().getSelectedItem();
    }

    private void initFields(ListView<String> categoryListView) {
        // Init order is important between logicalCategoryNames, categoryActions and categoryPositioner
        this.categoryListView = categoryListView;
        this.logicalCategoryNames = LogicalCategoryNames.getInstance();
        this.categoryActions = new CategoryActions(logicalCategoryNames);
        this.categoryPositioner = buildCategoryPositioner();
    }

    private CategoryPositioner buildCategoryPositioner() {
        return CategoryPositioner.builder()
                .categoryListView(categoryListView)
                .blockSelectionActions(categoryActions::blockSelectionActions)
                .releaseSelectionActions(categoryActions::releaseSelectionActions)
                .build();
    }

    private void setupListView(Collection<String> customCategories) {
        ObservableList<String> allCategories = collectCategories(customCategories);
        categoryListView.setItems(allCategories);
        setupListViewSelectionEvent();
    }

    private void setupListViewSelectionEvent() {
        categoryListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, fromCategory, toCategory) -> {
                    categoryActions.selectedCategoryChangedFromTo(fromCategory, toCategory);
                });
    }

    private ObservableList<String> collectCategories(Collection<String> customCategories) {
        ObservableList<String> categories = FXCollections.observableArrayList();
        categories.addAll(logicalCategoryNames.getLocalizedNames());
        categories.addAll(customCategories);
        return categories;
    }

}
