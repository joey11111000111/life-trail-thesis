package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category;

import debrecen.university.pti.kovtamas.display.utils.ValueChangeAction;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CategoryActions {

    private final Set<ValueChangeAction<String>> selectionChangeActions;
    private final Set<Consumer<String>> newCategoryActions;
    private final Set<Consumer<String>> removeCategoryActions;
    private boolean isSelectionActionBlocked;

    public CategoryActions() {
        selectionChangeActions = new HashSet<>();
        newCategoryActions = new HashSet<>();
        removeCategoryActions = new HashSet<>();
        isSelectionActionBlocked = false;
    }

    // Register actions -----------------------------
    public void registerSelectionChangeAction(@NonNull final ValueChangeAction<String> selectionChangeAction) {
        selectionChangeActions.add(selectionChangeAction);
    }

    public void registerNewCategoryAction(@NonNull final Consumer<String> newCategoryAction) {
        newCategoryActions.add(newCategoryAction);
    }

    public void registerRemoveCategoryAction(@NonNull final Consumer<String> removeAction) {
        removeCategoryActions.add(removeAction);
    }

    // Invoke actions -------------------------------
    void selectedCategoryChangedFromTo(String fromCategory, String toCategory) {
        if (!isSelectionActionBlocked) {
            log.info("Selected category changed from '" + fromCategory + "' to '" + toCategory + "'");
            selectionChangeActions.forEach(action -> action.accept(fromCategory, toCategory));
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
}
