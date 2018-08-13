import org.json.simple.JSONValue;

/**
 * Created by Troels "Sheepyhead" Jessen on 2018-08-13.
 * Email: kairyuka@live.dk
 */
public class JSONKeyValuePair {

    String key;
    Object value;

    public JSONKeyValuePair(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }
}
