package debrecen.university.pti.kovtamas.data.test.category;

import debrecen.university.pti.kovtamas.data.entity.todo.CategoryEntity;
import java.util.ArrayList;
import java.util.List;

public class CategoryTestDataGenerator {

    static public List<CategoryEntity> generateUnsavedEntitiesInAscDisplayOrder(final int numOfEntities) {
        if (numOfEntities < 0) {
            throw new IllegalArgumentException("Cannot generate less than 0 category entities!");
        }

        List<CategoryEntity> generatedEntities = new ArrayList<>(numOfEntities);
        String nameBase = "Category ";
        for (int i = 0; i < numOfEntities; i++) {
            CategoryEntity newEntity = new CategoryEntity(nameBase + i, i);
            generatedEntities.add(newEntity);
        }

        return generatedEntities;
    }

    static public List<CategoryEntity> generateUnsavedEntitiesInDescDisplayOrder(final int numOfEntities) {
        List<CategoryEntity> generatedEntities = generateUnsavedEntitiesInAscDisplayOrder(numOfEntities);
        List<CategoryEntity> orderedEntities = new ArrayList<>(numOfEntities);

        for (int i = numOfEntities - 1; i >= 0; i--) {
            orderedEntities.add(generatedEntities.get(i));
        }

        return orderedEntities;
    }

    static public CategoryEntity generateOneUnsavedEntity() {
        return new CategoryEntity("Category -1", 0);
    }

}
