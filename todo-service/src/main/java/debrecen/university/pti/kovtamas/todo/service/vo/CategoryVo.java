package debrecen.university.pti.kovtamas.todo.service.vo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryVo implements Serializable, Comparable<CategoryVo> {

    static private final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private int displayIndex;

    public CategoryVo(String name, int displayIndex) {
        this.name = name;
        this.displayIndex = displayIndex;
    }

    @Override
    public int compareTo(CategoryVo otherCategory) {
        return displayIndex - otherCategory.getDisplayIndex();
    }

    public boolean hasId() {
        return id != null;
    }

}
