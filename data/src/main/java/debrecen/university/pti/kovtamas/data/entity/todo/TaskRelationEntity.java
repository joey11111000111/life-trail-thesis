package debrecen.university.pti.kovtamas.data.entity.todo;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class TaskRelationEntity implements Serializable {

    static private final long serialVersionUID = 5000L;

    private Integer id = null;
    private int parentId;
    private int childId;

    public TaskRelationEntity(int parentId, int childId) {
        this.parentId = parentId;
        this.childId = childId;
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

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + this.parentId;
        hash = 97 * hash + this.childId;
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
        final TaskRelationEntity other = (TaskRelationEntity) obj;
        if (this.parentId != other.parentId) {
            return false;
        }
        if (this.childId != other.childId) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TaskRelationEntity{" + "id=" + id + ", parentId=" + parentId + ", childId=" + childId + '}';
    }

}
