package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category;

import debrecen.university.pti.kovtamas.display.utils.ValueChangeAction;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.LogicalCategoryNames.LogicalCategories;
import debrecen.university.pti.kovtamas.todo.service.vo.CategoryVo;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CategoryActions {

    private final LogicalCategoryNames logicalCategoryNames;
    private final List<CategoryVo> customCategories;
    // Remove existing and add new category only applies to custom categories.
    // But both custom and logical categories are selectable,
    // that's why selection action is the only one with CategoryVo.
    private final Set<ValueChangeAction<DisplayCategoryVo>> selectionChangeActions;
    private final Set<Consumer<CategoryVo>> newCategoryActions;
    private final Set<Consumer<CategoryVo>> removeCategoryActions;

    private boolean isSelectionActionBlocked;

    public CategoryActions(LogicalCategoryNames logicalCategoryNames, List<CategoryVo> customCategories) {
        this.logicalCategoryNames = logicalCategoryNames;
        this.customCategories = customCategories;
        this.selectionChangeActions = new HashSet<>();
        this.newCategoryActions = new HashSet<>();
        this.removeCategoryActions = new HashSet<>();
        this.isSelectionActionBlocked = false;
    }

    // Register actions -----------------------------
    public void registerSelectionChangeAction(@NonNull final ValueChangeAction<DisplayCategoryVo> selectionChangeAction) {
        selectionChangeActions.add(selectionChangeAction);
    }

    public void registerNewCategoryAction(@NonNull final Consumer<CategoryVo> newCategoryAction) {
        newCategoryActions.add(newCategoryAction);
    }

    public void registerRemoveCategoryAction(@NonNull final Consumer<CategoryVo> removeAction) {
        removeCategoryActions.add(removeAction);
    }

    // Invoke actions -------------------------------
    void selectedCategoryChangedFromTo(String fromCategoryName, String toCategoryName) {
        if (!isSelectionActionBlocked) {
            log.info("Selected category changed from '" + fromCategoryName + "' to '" + toCategoryName + "'");
            DisplayCategoryVo fromCategoryVo = createPossiblyNullCategoryVoFromName(fromCategoryName);
            DisplayCategoryVo toCategoryVo = createPossiblyNullCategoryVoFromName(toCategoryName);
            selectionChangeActions.forEach(action -> action.accept(fromCategoryVo, toCategoryVo));
        }
    }

    void newCategoryAdded(CategoryVo newCategory) {
        log.info("New category added: " + newCategory);
        newCategoryActions.forEach(action -> action.accept(newCategory));
    }

    void categoryRemoved(CategoryVo removedCategory) {
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
    private DisplayCategoryVo createPossiblyNullCategoryVoFromName(String categoryName) {
        if (categoryName == null) {
            return null;
        }

        return createDisplayCategoryVoFromName(categoryName);
    }

    private DisplayCategoryVo createDisplayCategoryVoFromName(String categoryName) {
        boolean isLogicalCategory = logicalCategoryNames.isLogicalCategory(categoryName);
        if (isLogicalCategory) {
            return createLogicalCategoryVo(categoryName);
        }

        return createCustomCategoryVo(categoryName);
    }

    private DisplayCategoryVo createLogicalCategoryVo(String categoryName) {
        LogicalCategories logicalCategory = logicalCategoryNames.whichLogicalCategory(categoryName);
        return DisplayCategoryVo.logicalCategoryVo(logicalCategory);
    }

    private DisplayCategoryVo createCustomCategoryVo(String categoryName) {
        CategoryVo customCategoryVo = customCategories.stream()
                .filter(cusCat -> Objects.equals(cusCat.getName(), categoryName))
                .findAny()
                .get();

        return DisplayCategoryVo.customCategoryVo(customCategoryVo);
    }
}
