import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.FileReader;
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
            root = "";
        }

        notifyListeners(new TreeModelEvent(this, new Object[]{rootObject}), ChangeType.StructureChanged);
    }

    @Override
    public String getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parentObject, int index) {
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

                    return new JSONKeyValuePair((String) keyObject, rootObject.get(keyObject));
                }
            } else {
                Object currentObject = getNode(parent);

                if (currentObject instanceof JSONObject) {
                    JSONObject current = (JSONObject) currentObject;
                    int currentIndex = 0;
                    for (Object keyObject : current.keySet()) {
                        if (currentIndex != index) {
                            currentIndex++;
                            continue;
                        }

                        return new JSONKeyValuePair((String) keyObject, current.get(keyObject));
                    }
                } else if (currentObject instanceof JSONArray) {
                    JSONArray current = (JSONArray) currentObject;

                    return current.get(index);

                } else {
                    throw new UnsupportedOperationException("Final child found is not JSONObject but instead " + currentObject.getClass().getName());

                }
            }
        } else if (parentObject instanceof JSONArray) {
            JSONArray parent = (JSONArray) parentObject;
            if (index < 0 || index >= parent.size()) return null;

            return parent.get(index);
        } else if (parentObject instanceof JSONObject) {
            JSONObject parent = (JSONObject) parentObject;

            int currentIndex = 0;
            for (Object keyObject : parent.keySet()) {
                if (currentIndex != index) {
                    currentIndex++;
                    continue;
                }

                return new JSONKeyValuePair((String) keyObject, parent.get(keyObject));
            }
        } else {
            throw new UnsupportedOperationException("Finding child of type " + parentObject.getClass().getName() + " not implemented!");
        }
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof String) {
            Object node = getNode((String) parent);
            if (node instanceof JSONArray) return ((JSONArray) node).size();
            if (node instanceof JSONObject) return ((JSONObject) node).size();
        } else if (parent instanceof JSONObject) {
            return ((JSONObject) parent).size();
        } else if (parent instanceof JSONArray) {
            return ((JSONArray) parent).size();
        } else {
            return 0;
        }
        return 0;
    }

    private Object getNode(String path) {
        if (path.equals(root)) {
            return rootObject;
        }

        String[] strings = path.split("\\.");
        Object currentObject = rootObject;
        for (int i = 0; i < strings.length; i++) {
            if (currentObject instanceof JSONObject) {
                JSONObject current = (JSONObject) currentObject;
                currentObject = current.getOrDefault(strings[i], null);
            } else {
                if (currentObject == null)
                    throw new UnsupportedOperationException("Value " + path + " not found! Child " + strings[i] + " not found under " + strings[i - 1]);

                throw new UnsupportedOperationException("Attempted to get value from non-rootObject");
            }
        }

        return currentObject;
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
        if (child instanceof JSONKeyValuePair) child = ((JSONKeyValuePair)child).value;

        if (parentObject instanceof JSONObject) {
            parent = (JSONObject) parentObject;
        }
        if (parentObject instanceof String && getNode((String)parentObject) instanceof JSONObject) {
            parent = (JSONObject)getNode((String) parentObject);
        }
        if (parent != null)
        {
            int currentIndex = 0;
            for (Object keyObject : parent.keySet()) {
                if (child.equals(keyObject) || child.equals(parent.get(keyObject))) return currentIndex;
                currentIndex++;
            }
        }


        JSONArray parentArray = null;
        if (parentObject instanceof JSONArray) {
            parentArray = (JSONArray)parentObject;
        }
        if (parentObject instanceof String && getNode((String)parentObject) instanceof JSONArray){
            parentArray = (JSONArray)getNode((String)parentObject);
        }
        if (parentArray != null) {
            int currentIndex = 0;
            for (Object object : parentArray) {
                if (object.equals(child)) return currentIndex;
                currentIndex++;
            }
        }

        if (parentObject instanceof String) throw new UnsupportedOperationException("Finding index of child not supported for type " + getNode((String) parentObject).getClass().getName());
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
