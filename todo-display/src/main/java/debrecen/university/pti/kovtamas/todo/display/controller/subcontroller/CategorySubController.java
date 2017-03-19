package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller;

import java.util.Collection;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import lombok.Data;

@Data
public class CategorySubController {

    public static enum Directions {
        UP(-1), DOWN(1);

        private final int modifier;

        private Directions(int modifier) {
            this.modifier = modifier;
        }

        public int getModifier() {
            return modifier;
        }

    }

    private final ListView<String> categoryListView;
    private boolean blockListEvent;
    private final ObservableList<String> categoryList;

    public CategorySubController(ListView<String> categoryListView, Collection<String> allCategories) {
        this.categoryListView = categoryListView;
        categoryList = FXCollections.observableArrayList(allCategories);

        categoryListView.setItems(categoryList);
        categoryListView.getSelectionModel().selectedItemProperty().addListener(this::categorySelectionAction);
    }

    public void moveSelectedCategory(Directions dir) {
        if (categoryList.size() < 2) {
            return;
        }
        int index = categoryListView.getSelectionModel().getSelectedIndex();
        int modifier = dir.getModifier();
        if ((modifier == -1 && index == 0) || (modifier == 1 && index == categoryList.size() - 1)) {
            return;
        }

        blockListEvent = true;
        String categoryName = categoryListView.getSelectionModel().getSelectedItem();
        categoryList.remove(index);
        categoryList.add(index + modifier, categoryName);
        categoryListView.getSelectionModel().select(categoryName);
        blockListEvent = false;
    }

    public void removeCategory(String category) {
        categoryList.remove(category);
    }

    public void addNewCategory(String category) {
        categoryList.add(category);
    }

    public String getSelectedCategory() {
        return categoryListView.getSelectionModel().getSelectedItem();
    }

    public void setSelectedCategory(String category) {
        categoryListView.getSelectionModel().select(category);
    }

    private void categorySelectionAction(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (!blockListEvent) {
            System.out.println("------- selection happened, old: " + oldValue + "\tnew: " + newValue + " -------");
        }
    }

}
