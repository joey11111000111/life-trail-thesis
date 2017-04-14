package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category;

import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.CategorySaveFailureException;
import debrecen.university.pti.kovtamas.todo.service.api.TodoService;
import debrecen.university.pti.kovtamas.todo.service.vo.CategoryVo;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CategorySubController {

    private TodoService service;

    private ListView<String> categoryListView;
    private List<CategoryVo> customCategories;
    private LogicalCategoryNames logicalCategoryNames;
    private CategoryPositioner categoryPositioner;
    private CategoryActions categoryActions;

    public CategorySubController(ListView<String> categoryListView, TodoService service) {
        initFields(categoryListView, service);
        setupListView();
    }

    public void moveSelectedCategoryIfPossible(CategoryPositioner.Directions direction) {
        categoryPositioner.moveSelectedCategoryIfPossible(direction);
    }

    public void removeSelectedCategory() {
        String selectedCategoryName = getSelectedCategory();
        if (logicalCategoryNames.isLogicalCategory(selectedCategoryName)) {
            return;
        }

        try {
            removeCustomCategory(selectedCategoryName);
        } catch (CategorySaveFailureException csfe) {
            log.warn("Could not remove custom category: " + selectedCategoryName, csfe);
        }
    }

    private void removeCustomCategory(String categoryName) throws CategorySaveFailureException {
        CategoryVo removeCatVo = getCustomCategoryVoFromList(categoryName);
        removeDisplayedCustomCategory(removeCatVo);
        removeCategoryFromListAndUpdateDisplayIndexes(removeCatVo);
    }

    private void removeDisplayedCustomCategory(CategoryVo category) {
        service.deleteCategory(category);
        categoryListView.getItems().remove(category.getName());
        categoryActions.categoryRemoved(category);
    }

    private CategoryVo getCustomCategoryVoFromList(String categoryName) {
        Optional<CategoryVo> categoryOpt = customCategories.stream()
                .filter(catVo -> Objects.equals(catVo.getName(), categoryName))
                .findFirst();

        if (categoryOpt.isPresent()) {
            return categoryOpt.get();
        } else {
            throw new IllegalArgumentException("Custom category: '" + categoryName
                    + "' is not present in the customCategories list");
        }
    }

    private void removeCategoryFromListAndUpdateDisplayIndexes(CategoryVo removeCatVo) throws CategorySaveFailureException {
        int catIndex = customCategories.indexOf(removeCatVo);
        customCategories.remove(catIndex);
        modifyDisplayIndexesFromListIndex(-1, catIndex);
    }

    private void modifyDisplayIndexesFromListIndex(int displayIndexModifier, int startListIndex) throws CategorySaveFailureException {
        for (int i = startListIndex; i < customCategories.size(); i++) {
            CategoryVo modifiedCategory = customCategories.get(i);
            int currentDisplayIndex = modifiedCategory.getDisplayIndex();

            modifiedCategory.setDisplayIndex(currentDisplayIndex + displayIndexModifier);
            service.saveOrUpdateCategory(modifiedCategory);
        }
    }

    public void addNewCategory(String categoryName) {
        int highestDisplayIndex = customCategories
                .get(customCategories.size() - 1)
                .getDisplayIndex();

        CategoryVo newCategoryVo = new CategoryVo(categoryName, highestDisplayIndex + 1);
        try {
            CategoryVo savedNewCategoryVo = service.saveOrUpdateCategory(newCategoryVo);
            customCategories.add(savedNewCategoryVo);
            categoryListView.getItems().add(categoryName);
        } catch (CategorySaveFailureException csfe) {
            log.warn("Failed to create save new category", csfe);
        }
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

    private void initFields(ListView<String> categoryListView, TodoService service) {
        // Init order is important between logicalCategoryNames, categoryActions and categoryPositioner
        this.service = service;
        this.categoryListView = categoryListView;
        this.customCategories = service.getAllCategoriesInDisplayOrder();
        this.logicalCategoryNames = LogicalCategoryNames.getInstance();
        this.categoryActions = new CategoryActions(logicalCategoryNames, customCategories);
        this.categoryPositioner = buildCategoryPositioner();
    }

    private CategoryPositioner buildCategoryPositioner() {
        return CategoryPositioner.builder()
                .service(service)
                .categoryListView(categoryListView)
                .customCategories(customCategories)
                .logicalCategoryNames(logicalCategoryNames)
                .blockSelectionActions(categoryActions::blockSelectionActions)
                .releaseSelectionActions(categoryActions::releaseSelectionActions)
                .build();
    }

    private void setupListView() {
        ObservableList<String> allCategories = getCustomCategoryNamesInOrder();
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

    private ObservableList<String> getCustomCategoryNamesInOrder() {
        ObservableList<String> categories = FXCollections.observableArrayList();
        categories.addAll(logicalCategoryNames.getLocalizedNames());

        List<String> customCategoryNames = customCategories.stream()
                .map(CategoryVo::getName)
                .collect(Collectors.toList());
        categories.addAll(customCategoryNames);

        return categories;
    }

}
