package debrecen.university.pti.kovtamas.todo.service.mapper;

import debrecen.university.pti.kovtamas.data.entity.todo.CategoryEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.todo.category.JdbcCategoryRepository;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.CategoryNotFoundException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.CategoryRepository;
import debrecen.university.pti.kovtamas.todo.service.vo.CategoryVo;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryEntityVoMapper {

    static private final CategoryRepository categoryRepo;

    static {
        categoryRepo = JdbcCategoryRepository.getInstance();
    }

    static public CategoryVo toVo(CategoryEntity entity) {
        return CategoryVo.builder()
                .id(entity.getId())
                .name(entity.getName())
                .displayIndex(entity.getDisplayIndex())
                .build();
    }

    static public List<CategoryVo> allToVo(List<CategoryEntity> entities) {
        return entities.stream()
                .map(CategoryEntityVoMapper::toVo)
                .collect(Collectors.toList());
    }

    static public CategoryEntity toEntity(CategoryVo vo) {
        return new CategoryEntity(vo.getId(), vo.getName(), vo.getDisplayIndex());
    }

    static public List<CategoryEntity> allToEntiy(List<CategoryVo> vos) {
        return vos.stream()
                .map(CategoryEntityVoMapper::toEntity)
                .collect(Collectors.toList());
    }

    static public CategoryEntity entityFromId(int id) throws CategoryNotFoundException {
        return categoryRepo.findById(id);
    }

    static public CategoryVo voFromId(int id) throws CategoryNotFoundException {
        CategoryEntity entity = entityFromId(id);
        return toVo(entity);
    }
}
