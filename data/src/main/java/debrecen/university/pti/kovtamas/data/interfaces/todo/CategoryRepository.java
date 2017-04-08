package debrecen.university.pti.kovtamas.data.interfaces.todo;

import debrecen.university.pti.kovtamas.data.entity.todo.CategoryEntity;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.CategoryNotFoundException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.CategorySaveFailureException;
import java.util.List;

public interface CategoryRepository {

    List<CategoryEntity> findAll();

    CategoryEntity findById(int id) throws CategoryNotFoundException;

    int idOf(String categoryName) throws CategoryNotFoundException;

    CategoryEntity saveOrUpdate(CategoryEntity newCategoryName) throws CategorySaveFailureException;

    void remove(Integer id);

    void remove(String categoryName);

    void clearTable();
}
