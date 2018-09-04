
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Troels "Sheepyhead" Jessen on 2018-08-13.
 * Email: kairyuka@live.dk
 */
public class JSONTreeModel implements TreeModel {

    JSONMutableTreeNode rootNode;

    private List<TreeModelListener> listeners;

    public JSONTreeModel(String path) {
        listeners = new ArrayList<>();
        rootNode = new JSONMutableTreeNode(path);
    }

    @Override
    public Object getRoot() {
        return rootNode;
    }

    public JSONMutableTreeNode getRootNode() {
        return rootNode;
    }

    public JSONMutableTreeNode getChild(String key) {
        return rootNode.getChild(key);
    }
    public JSONMutableTreeNode getChild(int index) {
        return rootNode.getChild(index);
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (!(parent instanceof JSONMutableTreeNode)) {
            throw new UnsupportedOperationException("Cannot get child of non-tree node type " + parent.getClass().getName());
        }

        return ((JSONMutableTreeNode)parent).getChildAt(index);
    }

    @Override
    public int getChildCount(Object parent) {
        if (!(parent instanceof JSONMutableTreeNode)) {
            throw new UnsupportedOperationException("Cannot get child of non-JMTN type " + parent.getClass().getName());
        }

        return ((JSONMutableTreeNode)parent).getChildCount();

    }

    public int getChildCount() {
        return rootNode.getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        if (!(node instanceof TreeNode)) {
            throw new UnsupportedOperationException("Cannot get leafiness of non-treenode type " + node.getClass().getName());
        }
        return ((TreeNode) node).isLeaf();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new NotImplementedException();
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (!(parent instanceof TreeNode)) {
            throw new UnsupportedOperationException("Cannot get index of non-treenode parent type " + parent.getClass().getName());
        }
        if (!(child instanceof TreeNode)) {
            throw new UnsupportedOperationException("Cannot get index of non-treenode child type " + child.getClass().getName());
        }
        return ((TreeNode)parent).getIndex((TreeNode)child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    public boolean isLeaf() {
        return rootNode.isLeaf();
    }
}
