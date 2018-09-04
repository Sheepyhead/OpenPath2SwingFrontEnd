import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.tree.DefaultMutableTreeNode;

public class JSONMutableNode extends DefaultMutableTreeNode {
    public JSONMutableNode(Object object) {
        super(object);
    }

    @Override
    public String toString() {
        if (userObject instanceof KeyValuePair && ((KeyValuePair)userObject).getKey() instanceof String) {
            KeyValuePair pair = (KeyValuePair) userObject;
            String key = (String)pair.getKey();
            Object value = pair.getValue();
            if (value instanceof JSONMutableNode) value = ((JSONMutableNode) value).getUserObject();

            if (value instanceof JSONArray || value instanceof JSONObject) return key;

            if (value instanceof String) return key + ": " + (String)value;
            if (value instanceof Long) return key + ": " + Long.toString((Long)value);
        }
        if (userObject instanceof JSONArray) return "[Array]";
        if (userObject instanceof JSONObject)  {
            JSONObject object = (JSONObject)userObject;

            String name = "";
            Object nameObject = object.getOrDefault("Name", null);
            if (nameObject instanceof String) name = (String)nameObject;
            else nameObject = object.getOrDefault("Source",null);
            if (nameObject instanceof String) name = (String)nameObject;

            return name;
        }
        return super.toString();
    }
}
