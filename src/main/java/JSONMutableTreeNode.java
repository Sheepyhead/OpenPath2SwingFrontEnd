import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.FileReader;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by Troels "Sheepyhead" Jessen on 2018-09-04.
 * Email: kairyuka@live.dk
 */
public class JSONMutableTreeNode implements Serializable, Cloneable, MutableTreeNode, TreeNode {

    private String key;
    private Object userObject;

    private MutableTreeNode genericParent;
    private JSONMutableTreeNode parent;

    public JSONMutableTreeNode(Object userObject, JSONMutableTreeNode parent, String key) {
        if (userObject instanceof JSONObject ||
                userObject instanceof JSONArray ||
                userObject instanceof JSONValue ||
                userObject instanceof String ||
                userObject instanceof Long) {
            this.key = key;
            setParent(parent);
            genericParent = null;
            setUserObject(userObject);
        } else {
            throw new UnsupportedOperationException("Cannot construct JSONMutableTreeNode from object of type " + userObject.getClass().getName());
        }
    }

    public JSONMutableTreeNode(String path) {
        parent = null;
        genericParent = null;
        JSONParser parser = new JSONParser();

        try {
            userObject = parser.parse(new FileReader(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject getAsObject() {
        if (userObject instanceof JSONObject) return (JSONObject) userObject;
        return null;
    }

    public JSONArray getAsArray() {
        if (userObject instanceof JSONArray) return (JSONArray) userObject;
        return null;
    }

    public String getAsString() {
        if (userObject instanceof String) return (String) userObject;
        return null;
    }

    public long getAsLong() {
        if (userObject instanceof Long) return (Long) userObject;
        return 0;
    }

    public Class getDataType() {
        return userObject.getClass();
    }

    public JSONMutableTreeNode(JSONObject userObject, JSONMutableTreeNode parent) {
        setParent(parent);
        setUserObject(userObject);
    }

    public JSONMutableTreeNode(JSONArray userObject, JSONMutableTreeNode parent) {
        setParent(parent);
        setUserObject(userObject);
    }

    public JSONMutableTreeNode(JSONValue userObject, JSONMutableTreeNode parent) {
        setParent(parent);
        setUserObject(userObject);
    }

    @Override
    public void insert(MutableTreeNode child, int index) {
        throw new NotImplementedException();
    }

    @Override
    public void remove(int index) {
        throw new NotImplementedException();

    }

    @Override
    public void remove(MutableTreeNode node) {
        throw new NotImplementedException();

    }

    @Override
    public void setUserObject(Object object) {
        userObject = object;
    }

    @Override
    public void removeFromParent() {
        parent = null;

    }

    @Override
    public void setParent(MutableTreeNode newParent) {
        genericParent = newParent;
    }

    public void setParent(JSONMutableTreeNode newParent) {
        parent = newParent;
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        if (userObject instanceof JSONValue) {
            return null;
        }

        if (userObject instanceof JSONArray) {
            JSONArray array = (JSONArray) userObject;

            return new JSONMutableTreeNode(array.get(childIndex), this, "" + childIndex);
        }

        if (userObject instanceof JSONObject) {
            JSONObject object = (JSONObject) userObject;

            int i = 0;
            Iterator it = object.keySet().iterator();
            for (Object childObject : object.keySet()) {

                if (i == childIndex) {
                    return new JSONMutableTreeNode(object.get(childObject), this, (String)it.next());
                }

                it.next();
                i++;
            }

            return null;
        }

        return null;
    }

    public JSONMutableTreeNode getChild(String key) {
        if (!(userObject instanceof JSONObject))
            throw new UnsupportedOperationException("Attempted to find key of non-object type " + userObject.getClass().getName());

        JSONObject object = (JSONObject) userObject;

        return new JSONMutableTreeNode(object.get(key), this, key);

    }

    public JSONMutableTreeNode getChild(int index) {
        return (JSONMutableTreeNode) getChildAt(index);
    }

    @Override
    public int getChildCount() {
        if (userObject instanceof JSONValue) return 0;

        if (userObject instanceof JSONArray) {
            JSONArray array = (JSONArray) userObject;

            return array.size();
        }

        if (userObject instanceof JSONObject) {
            JSONObject object = (JSONObject) userObject;

            return object.size();
        }

        return 0;
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        if (userObject instanceof JSONValue) return -1;

        if (userObject instanceof JSONArray) {
            JSONArray array = (JSONArray) userObject;

            int i = 0;
            for (Object child : array) {
                TreeNode childNode = new JSONMutableTreeNode(child, this, "" + i);

                if (childNode.equals(node)) {
                    return i;
                }

                i++;
            }
        }
        if (userObject instanceof JSONObject) {
            JSONObject object = (JSONObject) userObject;

            int i = 0;
            Iterator it = object.keySet().iterator();
            for (Object child : object.values()) {
                TreeNode childNode = new JSONMutableTreeNode(child, this,(String)it.next());

                if (childNode.equals(node)) {
                    return i;
                }

                i++;
            }
        }

        return -1;
    }

    @Override
    public boolean getAllowsChildren() {
        return userObject instanceof JSONObject || userObject instanceof JSONArray;
    }

    @Override
    public boolean isLeaf() {
        return !getAllowsChildren();
    }

    @Override
    public Enumeration children() {
        if (userObject instanceof JSONValue) return null;

        if (userObject instanceof JSONArray) {
            JSONArray array = (JSONArray) userObject;
            Vector v = new Vector(array);
            return v.elements();
        }

        if (userObject instanceof JSONObject) {
            JSONObject object = (JSONObject) userObject;
            Vector v = new Vector(object.values());

            return v.elements();
        }

        throw new RuntimeException("This exception should never be thrown");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JSONMutableTreeNode) {
            JSONMutableTreeNode node = ((JSONMutableTreeNode) obj);
            if (node.getDataType().equals(getDataType())) {
                return node.userObject.equals(userObject);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (parent == null) {
            return "[NULLPARENT]";
        }

        String returnString = "";

        if (userObject instanceof String) return key + ": " +(String)userObject;
        if (userObject instanceof Long) return key + ": " + ((Long)userObject).toString();
        if (userObject instanceof JSONObject ||userObject instanceof JSONArray) return key;

        return "[FAILURE]";
    }
}
