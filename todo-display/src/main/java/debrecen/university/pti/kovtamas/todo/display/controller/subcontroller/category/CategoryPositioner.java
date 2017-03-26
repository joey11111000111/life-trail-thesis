package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category;

import debrecen.university.pti.kovtamas.display.utils.VoidNoArgMethod;
import java.util.List;
import javafx.scene.control.ListView;
import lombok.Builder;

@Builder
public class CategoryPositioner {

    public static enum Directions {
        UP(-1), DOWN(1);

        private final int directionModifier;

        private Directions(int modifier) {
            this.directionModifier = modifier;
        }

        public int getDirectionModifier() {
            return directionModifier;
        }
    }

    private final ListView<String> categoryListView;
    private final VoidNoArgMethod blockSelectionActions;
    private final VoidNoArgMethod releaseSelectionActions;

    public void moveSelectedCategoryIfPossible(Directions direction) {
        final int newIndex = getNewIndexOfSelectedCategory(direction);
        if (!isValidCategoryIndex(newIndex)) {
            return;
        }

        moveSelectedCategoryToWithoutActions(newIndex);
    }

    private void moveSelectedCategoryToWithoutActions(int toIndex) {
        blockSelectionActions.execute();
        moveSelectedCategoryTo(toIndex);
        releaseSelectionActions.execute();
    }

    private void moveSelectedCategoryTo(int toIndex) {
        final int fromIndex = getSelectedCategoryIndex();

        List<String> categoryList = categoryListView.getItems();
        String movingCategoryName = categoryList.get(fromIndex);

        categoryList.remove(fromIndex);
        categoryList.add(toIndex, movingCategoryName);

        categoryListView.getSelectionModel().select(toIndex);
    }

    private int getSelectedCategoryIndex() {
        return categoryListView.getSelectionModel().getSelectedIndex();
    }

    private int getNewIndexOfSelectedCategory(Directions direction) {
        final int selectionIndex = categoryListView.getSelectionModel().getSelectedIndex();
        return selectionIndex + direction.getDirectionModifier();
    }

    private boolean isValidCategoryIndex(int index) {
        return index >= 0 && index < categoryListView.getItems().size();
    }

}
