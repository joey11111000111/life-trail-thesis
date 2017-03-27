package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category;

import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.LogicalCategoryNames.LogicalCategories;
import lombok.NonNull;

public class CategoryVo {

    public static CategoryVo customCategoryVo(@NonNull final String categoryName) {
        return new CategoryVo(categoryName, null);
    }

    public static CategoryVo logicalCategoryVo(@NonNull final LogicalCategories logicalCategory) {
        return new CategoryVo(null, logicalCategory);
    }

    // A category is either custom or logical, never both.
    // So one of the fields is always null
    private final String customCategoryName;
    private final LogicalCategories logicalCategory;

    private CategoryVo(String customCategoryName, LogicalCategories logicalCategory) {
        this.customCategoryName = customCategoryName;
        this.logicalCategory = logicalCategory;
    }

    public String getCustomCategoryName() {
        if (!isCustom()) {
            throw new UnsupportedOperationException("This CategoryVo instance represents a"
                    + "logical category, getName() is not supported");
        }
        return customCategoryName;
    }

    public LogicalCategories getLogicalCategory() {
        if (!isLogical()) {
            throw new UnsupportedOperationException("This CategoryVo instance represents a"
                    + "custom category, getLogicalCategory() is not supported");
        }
        return logicalCategory;
    }

    public boolean isLogical() {
        return logicalCategory != null;
    }

    public boolean isCustom() {
        return customCategoryName != null;
    }

}
