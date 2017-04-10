package debrecen.university.pti.kovtamas.data.entity.todo;

import java.io.Serializable;
import java.util.Objects;

public class CategoryEntity implements Serializable {

    private Integer id;
    private String name;
    private int displayIndex;

    public CategoryEntity() {
    }

    public CategoryEntity(String name, int displayIndex) {
        this.name = name;
        this.displayIndex = displayIndex;
    }

    public CategoryEntity(Integer id, String name, int display_index) {
        this.id = id;
        this.name = name;
        this.displayIndex = display_index;
    }

    public boolean hasId() {
        return id != null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(int displayIndex) {
        if (displayIndex < 0) {
            throw new IllegalArgumentException("Display index must not be negative");
        }

        this.displayIndex = displayIndex;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.id);
        hash = 61 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CategoryEntity other = (CategoryEntity) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CategoryEntity{" + "id=" + id + ", name=" + name + ", displayIndex=" + displayIndex + '}';
    }

}
