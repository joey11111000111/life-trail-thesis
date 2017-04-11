package debrecen.university.pti.kovtamas.general.util;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class SimpleTreeNode<T> implements TreeNode<T> {

    private T element;
    private TreeNode<T> parent;
    private final List<TreeNode<T>> children;

    public SimpleTreeNode() {
        this(null);
    }

    public SimpleTreeNode(T element) {
        this.element = element;
        this.parent = null;
        this.children = new ArrayList<>();
    }

    @Override
    public T getElement() {
        return element;
    }

    @Override
    public void setElement(T element) {
        this.element = element;
    }

    @Override
    public boolean hasParent() {
        return parent != null;
    }

    @Override
    public TreeNode<T> getParent() {
        return parent;
    }

    @Override
    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    @Override
    public boolean hasChildren() {
        return children != null;
    }

    @Override
    public List<TreeNode<T>> getChildren() {
        return children;
    }

    @Override
    public void addChild(TreeNode<T> child) {
        child.setParent(this);
        children.add(child);
    }

    @Override
    public void addChild(T childElement) {
        TreeNode<T> childNode = new SimpleTreeNode<>();
        childNode.setElement(element);
        addChild(childNode);
    }

    @Override
    public void removeChild(TreeNode<T> child) {
        // Check if child is not already in the tree in a higher level
        child.removeChild(child);
    }

    @Override
    public String toString() {
        return toStringIndented(0);
    }

    private String toStringIndented(int indentCount) {
        String str = "";
        for (int i = 0; i < indentCount; i++) {
            str += "  ";
        }

        str += "SimpleTreeNode: " + element + "\n";
        for (TreeNode<T> child : children) {
            str += ((SimpleTreeNode<T>) child).toStringIndented(indentCount + 1);
        }

        return str;

    }

}
