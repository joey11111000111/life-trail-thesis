package debrecen.university.pti.kovtamas.general.util;

import java.util.List;

public interface TreeNode<T> {

    T getElement();

    void setElement(T element);

    boolean hasParent();

    TreeNode<T> getParent();

    void setParent(TreeNode<T> parent);

    boolean hasChildren();

    List<TreeNode<T>> getChildren();

    void addChild(TreeNode<T> child);

    void addChild(T childElement);

    void removeChild(TreeNode<T> child);

}
