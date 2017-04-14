package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category;

import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.LogicalCategoryNames.LogicalCategories;
import debrecen.university.pti.kovtamas.todo.service.vo.CategoryVo;
import lombok.NonNull;

public class DisplayCategoryVo {

    public static DisplayCategoryVo customCategoryVo(@NonNull final CategoryVo categoryName) {
        return new DisplayCategoryVo(categoryName, null);
    }

    public static DisplayCategoryVo logicalCategoryVo(@NonNull final LogicalCategories logicalCategory) {
        return new DisplayCategoryVo(null, logicalCategory);
    }

    // A category is either custom or logical, never both.
    // So one of the fields is always null
    private final CategoryVo customCategory;
    private final LogicalCategories logicalCategory;

    private DisplayCategoryVo(CategoryVo customCategory, LogicalCategories logicalCategory) {
        this.customCategory = customCategory;
        this.logicalCategory = logicalCategory;
    }

    public CategoryVo getCustomCategory() {
        if (!isCustom()) {
            throw new UnsupportedOperationException("This CategoryVo instance represents a"
                    + "logical category, getCustomCategory() is not supported");
        }
        return customCategory;
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
        return customCategory != null;
    }

}
