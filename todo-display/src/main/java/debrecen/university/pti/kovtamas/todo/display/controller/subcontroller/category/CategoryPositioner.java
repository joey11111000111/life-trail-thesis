package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category;

import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.CategorySaveFailureException;
import debrecen.university.pti.kovtamas.display.utils.VoidNoArgMethod;
import debrecen.university.pti.kovtamas.todo.service.api.TodoService;
import debrecen.university.pti.kovtamas.todo.service.vo.CategoryVo;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javafx.scene.control.ListView;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    private final TodoService service;
    private final ListView<String> categoryListView;
    private final List<CategoryVo> customCategories;
    private final LogicalCategoryNames logicalCategoryNames;
    private final VoidNoArgMethod blockSelectionActions;
    private final VoidNoArgMethod releaseSelectionActions;

    public void moveSelectedCategoryIfPossible(Directions direction) {
        final int currentIndex = getSelectedCategoryIndex();
        final int newIndex = getNewIndexOfSelectedCategory(direction);
        if (!bothCategoriesCanMove(currentIndex, newIndex)) {
            return;
        }

        moveSelectedCategoryToWithoutActions(newIndex);
    }

    private boolean bothCategoriesCanMove(int currentIndex, int newIndex) {
        return isValidCategoryIndex(newIndex) && !willLogicalCategoryMove(currentIndex, newIndex);
    }

    private boolean willLogicalCategoryMove(int currentIndex, int newIndex) {
        String categoryAtCurrentPosition = categoryListView.getItems().get(currentIndex);
        String categoryAtNewPosition = categoryListView.getItems().get(newIndex);
        return logicalCategoryNames.isOneOfThemLogicalCategory(categoryAtCurrentPosition, categoryAtNewPosition);
    }

    private void moveSelectedCategoryToWithoutActions(int toIndex) {
        blockSelectionActions.execute();
        try {
            moveSelectedCategoryTo(toIndex);
        } catch (CategorySaveFailureException csfe) {
            log.warn("Could modify category display index", csfe);
        }
        releaseSelectionActions.execute();
    }

    private void moveSelectedCategoryTo(int toIndex) throws CategorySaveFailureException {
        final int fromIndex = getSelectedCategoryIndex();
        List<String> categoryList = categoryListView.getItems();
        switchDisplayIndexes(categoryList.get(fromIndex), categoryList.get(toIndex));

        String movingCategoryName = categoryList.get(fromIndex);
        categoryList.remove(fromIndex);
        categoryList.add(toIndex, movingCategoryName);

        categoryListView.getSelectionModel().select(toIndex);
    }

    private void switchDisplayIndexes(String customCat1, String customCat2) throws CategorySaveFailureException {
        CategoryVo vo1 = getVoFromListByName(customCat1);
        CategoryVo vo2 = getVoFromListByName(customCat2);

        int tempDisplayIndex = vo1.getDisplayIndex();
        vo1.setDisplayIndex(vo2.getDisplayIndex());
        vo2.setDisplayIndex(tempDisplayIndex);

        service.saveOrUpdateCategory(vo1);
        service.saveOrUpdateCategory(vo2);

        Comparator<CategoryVo> displayIndexOrder = (cat1, cat2) -> cat1.getDisplayIndex() - cat2.getDisplayIndex();
        Collections.sort(customCategories, displayIndexOrder);
    }

    private CategoryVo getVoFromListByName(String categoryName) {
        return customCategories.stream()
                .filter(cusCat -> Objects.equals(cusCat.getName(), categoryName))
                .findAny().get();
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
