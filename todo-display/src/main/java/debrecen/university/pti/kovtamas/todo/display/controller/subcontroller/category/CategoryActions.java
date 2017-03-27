package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category;

import debrecen.university.pti.kovtamas.display.utils.ValueChangeAction;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.LogicalCategoryNames.LogicalCategories;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CategoryActions {

    private final LogicalCategoryNames logicalCategoryNames;
    // Remove existing and add new category only applies to custom categories.
    // But both custom and logical categories are selectable,
    // that's why selection action is the only one with CategoryVo.
    private final Set<ValueChangeAction<CategoryVo>> selectionChangeActions;
    private final Set<Consumer<String>> newCategoryActions;
    private final Set<Consumer<String>> removeCategoryActions;

    private boolean isSelectionActionBlocked;

    public CategoryActions(LogicalCategoryNames logicalCategoryNames) {
        this.logicalCategoryNames = logicalCategoryNames;
        this.selectionChangeActions = new HashSet<>();
        this.newCategoryActions = new HashSet<>();
        this.removeCategoryActions = new HashSet<>();
        this.isSelectionActionBlocked = false;
    }

    // Register actions -----------------------------
    public void registerSelectionChangeAction(@NonNull final ValueChangeAction<CategoryVo> selectionChangeAction) {
        selectionChangeActions.add(selectionChangeAction);
    }

    public void registerNewCategoryAction(@NonNull final Consumer<String> newCategoryAction) {
        newCategoryActions.add(newCategoryAction);
    }

    public void registerRemoveCategoryAction(@NonNull final Consumer<String> removeAction) {
        removeCategoryActions.add(removeAction);
    }

    // Invoke actions -------------------------------
    void selectedCategoryChangedFromTo(String fromCategoryName, String toCategoryName) {
        if (!isSelectionActionBlocked) {
            log.info("Selected category changed from '" + fromCategoryName + "' to '" + toCategoryName + "'");
            CategoryVo fromCategoryVo = createPossiblyNullCategoryVoFromName(fromCategoryName);
            CategoryVo toCategoryVo = createPossiblyNullCategoryVoFromName(toCategoryName);
            selectionChangeActions.forEach(action -> action.accept(fromCategoryVo, toCategoryVo));
        }
    }

    void newCategoryAdded(String newCategory) {
        log.info("New category added: " + newCategory);
        newCategoryActions.forEach(action -> action.accept(newCategory));
    }

    void categoryRemoved(String removedCategory) {
        log.info("Category removed: " + removedCategory);
        removeCategoryActions.forEach(action -> action.accept(removedCategory));
    }

    // Block and release actions --------------------
    void blockSelectionActions() {
        isSelectionActionBlocked = true;
    }

    void releaseSelectionActions() {
        isSelectionActionBlocked = false;
    }

    // private helper methods -----------------------
    private CategoryVo createPossiblyNullCategoryVoFromName(String categoryName) {
        if (categoryName == null) {
            return null;
        }

        return createNonNullCategoryVoFromName(categoryName);
    }

    private CategoryVo createNonNullCategoryVoFromName(String categoryName) {
        boolean isLogicalCategory = logicalCategoryNames.isLogicalCategory(categoryName);
        if (isLogicalCategory) {
            return createLogicalCategoryVo(categoryName);
        }

        return createCustomCategoryVo(categoryName);
    }

    private CategoryVo createLogicalCategoryVo(String categoryName) {
        LogicalCategories logicalCategory = logicalCategoryNames.whichLogicalCategory(categoryName);
        return CategoryVo.logicalCategoryVo(logicalCategory);
    }

    private CategoryVo createCustomCategoryVo(String categoryName) {
        return CategoryVo.customCategoryVo(categoryName);
    }
}
