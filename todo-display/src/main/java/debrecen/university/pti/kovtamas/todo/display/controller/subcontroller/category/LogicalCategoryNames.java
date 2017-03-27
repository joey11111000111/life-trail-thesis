package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category;

import debrecen.university.pti.kovtamas.display.utils.Modules;
import debrecen.university.pti.kovtamas.display.utils.locale.Localizer;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

import static debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.LogicalCategoryNames.LogicalCategories.COMPLETED;
import static debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.LogicalCategoryNames.LogicalCategories.THIS_WEEK;
import static debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.LogicalCategoryNames.LogicalCategories.TODAY;
import static debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.LogicalCategoryNames.LogicalCategories.TOMORROW;
import static debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.LogicalCategoryNames.LogicalCategories.UNCATEGORIZED;

public class LogicalCategoryNames {

    public static enum LogicalCategories {
        TODAY("category_today"),
        THIS_WEEK("category_this_week"),
        TOMORROW("category_tomorrow"),
        UNCATEGORIZED("category_uncategorized"),
        COMPLETED("category_completed");

        private static final List<LogicalCategories> allLogicalCategories;

        static {
            allLogicalCategories = new ArrayList<>();
            allLogicalCategories.add(TODAY);
            allLogicalCategories.add(TOMORROW);
            allLogicalCategories.add(THIS_WEEK);
            allLogicalCategories.add(UNCATEGORIZED);
            allLogicalCategories.add(COMPLETED);
        }

        public static int getLogicalCategoryCount() {
            return allLogicalCategories.size();
        }

        public static List<LogicalCategories> getAllLogicalCategories() {
            return allLogicalCategories;
        }

        private final String localizationKey;

        private LogicalCategories(String localizationKey) {
            this.localizationKey = localizationKey;
        }

        public String getLocalizationKey() {
            return localizationKey;
        }

    }

    private static final LogicalCategoryNames INSTANCE;

    static {
        INSTANCE = new LogicalCategoryNames();
    }

    public static LogicalCategoryNames getInstance() {
        return INSTANCE;
    }

    private Localizer localizer;
    private List<String> currentCategoryNames;

    private LogicalCategoryNames() {
        initFields();
    }

    public List<String> getLocalizedNames() {
        return new ArrayList<>(currentCategoryNames);
    }

    public boolean isLogicalCategory(String categoryName) {
        return currentCategoryNames.contains(categoryName);
    }

    public LogicalCategories whichLogicalCategory(@NonNull final String categoryName) {
        int listIndex = currentCategoryNames.indexOf(categoryName);
        if (listIndex == -1) {
            return null;
        }

        return LogicalCategories.getAllLogicalCategories().get(listIndex);
    }

    private void initFields() {
        localizer = Localizer.getInstance();
        localizeCategoryNames();
    }

    private void localizeCategoryNames() {
        currentCategoryNames = getLocalizedLogicalCategoryNames();
    }

    private List<String> getLocalizedLogicalCategoryNames() {
        List<LogicalCategories> allCategories = LogicalCategories.getAllLogicalCategories();
        List<String> allLocalizedCategories = new ArrayList<>();

        // Localize all categories into the localized list
        final int categoryCount = LogicalCategories.getLogicalCategoryCount();
        for (int i = 0; i < categoryCount; i++) {
            LogicalCategories currentCategory = allCategories.get(i);
            String localizedCategoryName = localizeCategory(currentCategory);
            allLocalizedCategories.add(localizedCategoryName);
        }

        return allLocalizedCategories;
    }

    private String localizeCategory(LogicalCategories logicalCategorie) {
        String key = logicalCategorie.getLocalizationKey();
        return localizer.localize(key, Modules.TODO);
    }

}
