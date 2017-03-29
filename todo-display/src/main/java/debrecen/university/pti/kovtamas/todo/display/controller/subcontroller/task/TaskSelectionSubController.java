package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task;

import debrecen.university.pti.kovtamas.todo.display.controller.TaskRowController;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import lombok.NonNull;

public class TaskSelectionSubController {

    static private final String SELECTED_BACKGROUND_STYLE = "-fx-background-color: rgba(30,70,200, .5);";
    static private final String EDITABLE_BACKGROUND_STYLE = "-fx-background-color: rgba(0,0,0, .6);";
    static private final String REGUALR_BACKGROUND_STYLE = "-fx-background-color: rgba(30,70,200, 0);";

    private final Set<Consumer<TaskRowController>> registeredRowModificationActions;
    private TaskRowController selectedRow;

    public TaskSelectionSubController() {
        registeredRowModificationActions = new HashSet<>();
        selectedRow = null;
    }

    public void registerTaskRow(@NonNull final TaskRowController rowController) {
        rowController.getRootViewComponent().setOnMouseClicked((event) -> {
            rowSelectionAction(rowController);
        });
    }

    public void registerRowModificationAction(Consumer<TaskRowController> action) {
        registeredRowModificationActions.add(action);
    }

    public boolean hasSelectedRow() {
        return selectedRow != null;
    }

    public void toggleDisableForSelectedRow() {
        if (hasSelectedRow()) {
            selectedRow.toggleDisabled();
            adjustBackgroundByDisableValue();

            if (!isUserEditing()) {
                selectedRowWasModifiedAction();
            }
        }
    }

    public boolean finisedEditing() {
        return hasSelectedRow() && !isUserEditing();
    }

    public TaskDisplayState getDisplayStateOfLastModifiedRow() {
        if (hasSelectedRow()) {
            return selectedRow.getTaskStateDetached();
        } else {
            return null;
        }

    }

    private void selectedRowWasModifiedAction() {
        registeredRowModificationActions.forEach(action -> action.accept(selectedRow));
    }

    private void rowSelectionAction(final TaskRowController newSelectedRow) {
        if (isNewRowIsTheSameAsCurrent(newSelectedRow)) {
            rowUnselectionAction();
        } else {
            rowSelectionChangedAction(newSelectedRow);
        }
    }

    private void rowUnselectionAction() {
        if (!isUserEditing()) {
            removeAllBackgroundFrom(selectedRow);
            selectedRow.setDisable(true);
            selectedRow = null;
        }
    }

    private void rowSelectionChangedAction(final TaskRowController newSelectedRow) {
        cleanUpSelectedRow();
        selectRow(newSelectedRow);
    }

    private void cleanUpSelectedRow() {
        if (hasSelectedRow()) {
            if (isUserEditing()) {
                selectedRowWasModifiedAction();
            }

            removeAllBackgroundFrom(selectedRow);
            selectedRow.setDisable(true);
        }
    }

    private void selectRow(final TaskRowController newSelectedRow) {
        addSelectionBackgroundTo(newSelectedRow);
        this.selectedRow = newSelectedRow;
    }

    private boolean isUserEditing() {
        return selectedRow != null && selectedRow.isEditable();
    }

    private boolean isNewRowIsTheSameAsCurrent(final TaskRowController newSelectedRow) {
        return selectedRow != null && selectedRow.getRowId() == newSelectedRow.getRowId();
    }

    private void adjustBackgroundByDisableValue() {
        if (selectedRow.isDisabled()) {
            addSelectionBackgroundTo(selectedRow);
        } else {
            addEditableBackgroundTo(selectedRow);
        }
    }

    private void addSelectionBackgroundTo(final TaskRowController row) {
        row.getRootViewComponent().setStyle(SELECTED_BACKGROUND_STYLE);
    }

    private void addEditableBackgroundTo(final TaskRowController row) {
        row.getRootViewComponent().setStyle(EDITABLE_BACKGROUND_STYLE);
    }

    private void removeAllBackgroundFrom(final TaskRowController row) {
        row.getRootViewComponent().setStyle(REGUALR_BACKGROUND_STYLE);
    }
}
