package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category;

import debrecen.university.pti.kovtamas.display.utils.Modules;
import debrecen.university.pti.kovtamas.display.utils.locale.Localizer;
import java.util.ArrayList;
import java.util.List;

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

        private final String localizationKey;

        private LogicalCategories(String localizationKey) {
            this.localizationKey = localizationKey;
        }

        public String getLocalizationKey() {
            return localizationKey;
        }
    }

    private final Localizer localizer;
    private final List<String> currentNames;

    public LogicalCategoryNames() {
        localizer = Localizer.getInstance();
        currentNames = getLocalizedLogicalCategoryNames();
    }

    public List<String> getLocalizedNames() {
        return new ArrayList<>(currentNames);
    }

    public boolean isLogicalCategory(String categoryName) {
        return currentNames.contains(categoryName);
    }

    private List<String> getLocalizedLogicalCategoryNames() {
        List<String> localizedLogicalCategoryNames = new ArrayList<>();
        localizedLogicalCategoryNames.add(localizer.localize(TODAY.getLocalizationKey(), Modules.TODO));
        localizedLogicalCategoryNames.add(localizer.localize(TOMORROW.getLocalizationKey(), Modules.TODO));
        localizedLogicalCategoryNames.add(localizer.localize(THIS_WEEK.getLocalizationKey(), Modules.TODO));
        localizedLogicalCategoryNames.add(localizer.localize(UNCATEGORIZED.getLocalizationKey(), Modules.TODO));
        localizedLogicalCategoryNames.add(localizer.localize(COMPLETED.getLocalizationKey(), Modules.TODO));

        return localizedLogicalCategoryNames;
    }

}
