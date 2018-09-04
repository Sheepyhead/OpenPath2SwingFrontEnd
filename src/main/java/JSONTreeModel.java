import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.FileReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Troels "Sheepyhead" Jessen on 2018-08-13.
 * Email: kairyuka@live.dk
 */
public class JSONTreeModel implements TreeModel {

    private JSONObject rootObject;

    private String root;

    private List<TreeModelListener> listeners;

    public JSONTreeModel(String path) {
        super();
        listeners = new ArrayList<>();

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(path));

            rootObject = (JSONObject) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (rootObject.keySet().size() == 1) {
            root = (String) rootObject.keySet().iterator().next();
        } else {
            root = "Database";
        }

        notifyListeners(new TreeModelEvent(this, new Object[]{rootObject}), ChangeType.StructureChanged);
    }

    @Override
    public DefaultMutableTreeNode getRoot() {
        return new JSONMutableNode(rootObject);
    }

    public DefaultMutableTreeNode getChild(Object parentObject, String key) {
        if (parentObject instanceof JSONMutableNode) parentObject = ((JSONMutableNode) parentObject).getUserObject();
        if (parentObject instanceof KeyValuePair) parentObject = ((KeyValuePair) parentObject).getValue();
        if (parentObject instanceof JSONMutableNode) parentObject = ((JSONMutableNode) parentObject).getUserObject();
        if (!(parentObject instanceof JSONObject)) return null;
        JSONObject parent = (JSONObject)parentObject;

        return new JSONMutableNode(parent.get(key));
    }

    @Override
    public DefaultMutableTreeNode getChild(Object parentObject, int index) {
        if (parentObject instanceof JSONMutableNode) parentObject = ((JSONMutableNode) parentObject).getUserObject();
        if (parentObject instanceof KeyValuePair) parentObject = ((KeyValuePair) parentObject).getValue();
        if (parentObject instanceof JSONMutableNode) parentObject = ((JSONMutableNode) parentObject).getUserObject();

        if (parentObject instanceof String) {
            String parent = (String) parentObject;

            if (parent.equals(root)) {
                if (index < 0 || index >= rootObject.keySet().size()) return null;
                int currentIndex = 0;
                for (Object keyObject : rootObject.keySet()) {
                    if (currentIndex != index) {
                        currentIndex++;
                        continue;
                    }

                    return new JSONMutableNode(keyObject);
                }
            } else {
                Object currentObject = rootObject.getOrDefault(parent, null);

                if (currentObject instanceof JSONObject) {
                    JSONObject current = (JSONObject) currentObject;
                    int currentIndex = 0;
                    for (Object keyObject : current.keySet()) {
                        if (currentIndex != index) {
                            currentIndex++;
                            continue;
                        }

                        return new JSONMutableNode(keyObject);
                    }
                } else if (currentObject instanceof JSONArray) {
                    JSONArray current = (JSONArray) currentObject;

                    return new JSONMutableNode(current.get(index));

                } else {
                    throw new UnsupportedOperationException("Final child found is not JSONObject but instead " + currentObject.getClass().getName());

                }
            }
        } else if (parentObject instanceof JSONArray) {
            JSONArray parent = (JSONArray) parentObject;
            if (index < 0 || index >= parent.size()) return null;

            return new JSONMutableNode(parent.get(index));
        } else if (parentObject instanceof JSONObject) {
            JSONObject parent = (JSONObject) parentObject;

            int currentIndex = 0;
            for (Object keyObject : parent.keySet()) {
                if (currentIndex != index) {
                    currentIndex++;
                    continue;
                }

                return new JSONMutableNode(new KeyValuePair<>((String)keyObject, new JSONMutableNode(parent.get(keyObject))));
            }
        } else {
            throw new UnsupportedOperationException("Finding child of type " + parentObject.getClass().getName() + " not implemented!");
        }
        return null;
    }

    public DefaultMutableTreeNode getChild(Object parentObject, String key) {
        if (parentObject instanceof JSONMutableNode) parentObject = ((JSONMutableNode) parentObject).getUserObject();
        if (parentObject instanceof KeyValuePair) parentObject = ((KeyValuePair) parentObject).getValue();
        if (parentObject instanceof JSONMutableNode) parentObject = ((JSONMutableNode) parentObject).getUserObject();
        if (!(parentObject instanceof JSONObject)) return null;
        JSONObject parent = (JSONObject)parentObject;

        return new JSONMutableNode(parent.get(key));
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof JSONMutableNode) parent = ((JSONMutableNode) parent).getUserObject();
        if (parent instanceof KeyValuePair) parent = ((KeyValuePair) parent).getValue();
        if (parent instanceof JSONMutableNode) parent = ((JSONMutableNode) parent).getUserObject();
        if (parent instanceof JSONObject) {
            return ((JSONObject) parent).size();
        } else if (parent instanceof JSONArray) {
            return ((JSONArray) parent).size();
        } else {
            if (parent == null) System.out.println("Tried to get child count of null");
            else System.out.println("Tried to get child count of type " + parent.getClass().getName());
            return 0;
        }
    }

    @Override
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public int getIndexOfChild(Object parentObject, Object child) {
        JSONObject parent = null;

        if (parentObject instanceof JSONMutableNode) parentObject = ((JSONMutableNode) parentObject).getUserObject();
        if (parentObject instanceof KeyValuePair) parentObject = ((KeyValuePair) parentObject).getValue();
        if (parentObject instanceof JSONMutableNode) parentObject = ((JSONMutableNode) parentObject).getUserObject();

        if (child instanceof JSONMutableNode) child = ((JSONMutableNode) child).getUserObject();
        if (child instanceof KeyValuePair) child = ((KeyValuePair) child).getKey();
        if (parentObject instanceof JSONObject) {
            parent = (JSONObject) parentObject;
        }
        if (parent != null) {
            int currentIndex = 0;
            for (Object keyObject : parent.keySet()) {
                if (child.equals(keyObject) || child.equals(parent.get(keyObject))) return currentIndex;
                currentIndex++;
            }
        }


        JSONArray parentArray = null;
        if (parentObject instanceof JSONArray) {
            parentArray = (JSONArray) parentObject;
        }
        if (parentArray != null) {
            int currentIndex = 0;
            for (Object object : parentArray) {
                if (object.equals(child)) return currentIndex;
                currentIndex++;
            }
        }

        throw new UnsupportedOperationException("Finding index of child not supported for type " + parentObject.getClass());
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    private enum ChangeType {
        Changed,
        Inserted,
        Removed,
        StructureChanged
    }

    private void notifyListeners(TreeModelEvent event, ChangeType type) {
        for (TreeModelListener listener : listeners) {
            switch (type) {
                case Changed:
                    listener.treeNodesChanged(event);
                    break;
                case Inserted:
                    listener.treeNodesInserted(event);
                    break;
                case Removed:
                    listener.treeNodesRemoved(event);
                    break;
                case StructureChanged:
                    listener.treeStructureChanged(event);
                    break;
            }
        }
    }
}
