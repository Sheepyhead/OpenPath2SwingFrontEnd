import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by Troels "Sheepyhead" Jessen on 2018-08-13.
 * Email: kairyuka@live.dk
 */
public class JSONTreeModelTest {

    private JSONTreeModel model;

    @Before
    public void setUp() throws Exception {
        model = new JSONTreeModel("C:\\repo\\OpenPath2SwingFrontEnd\\src\\test\\resources\\Master List.json");
    }

    @Test
    public void getRootReturnsEmptyStringWithMultipleJSONKeys() {
        Object obj = model.getRoot();

        assertThat(obj, is(""));
    }

    @Test
    public void getChildCanReturnObjectsFromRoot() {
        Object firstRootChild = model.getChild(model.getRoot(), 0);
        Object secondRootChild = model.getChild(model.getRoot(), 1);

        assertThat(firstRootChild, instanceOf(JSONKeyValuePair.class));
        assertThat(secondRootChild, instanceOf(JSONKeyValuePair.class));

        JSONKeyValuePair firstRootPair = (JSONKeyValuePair) firstRootChild;
        JSONKeyValuePair secondRootPair = (JSONKeyValuePair) secondRootChild;

    }

    @Test
    public void getChildCanReturnSpecificObjects() {
        Object actionObject = model.getChild("Actions", 0);
        assertThat(actionObject, instanceOf(JSONObject.class));
        JSONObject action = (JSONObject) actionObject;

        assertThat(action.get("Type"), is("Action"));
        assertThat(action.get("Name"), is("Quick Alchemy"));
        assertThat(action.get("Action"), is(1L));

        Object classFeatureObject = model.getChild("Class Features", 0);
        assertThat(classFeatureObject, instanceOf(JSONObject.class));
        JSONObject classFeature = (JSONObject) classFeatureObject;

        assertThat(classFeature.get("Type"), is("Class Feature"));
        assertThat(classFeature.get("Name"), is("Advanced Alchemy"));
        assertThat(classFeature.get("Info"), instanceOf(JSONObject.class));
        assertThat(((JSONObject) classFeature.get("Info")).get("Actions"), instanceOf(JSONArray.class));
        assertThat(((JSONArray) ((JSONObject) classFeature.get("Info")).get("Actions")).size(), is(1));
    }

    @Test
    public void getChildCanReturnSpecificObjectsFromJSONObject() {
        Object actionObject = model.getChild("Actions", 0);
        assertThat(actionObject, instanceOf(JSONObject.class));
        JSONObject action = (JSONObject) actionObject;

        Object specificActionObject = model.getChild(action.get("Cost"), 0);

        assertThat(specificActionObject, instanceOf(JSONObject.class));
        JSONObject specificAction = (JSONObject) specificActionObject;
        assertThat(specificAction.get("Type"), is("Resonance"));
        assertThat(specificAction.get("Amount"), is(1L));

        Object randomObjectInAction = model.getChild(action, 2);

        assertThat(randomObjectInAction, instanceOf(JSONKeyValuePair.class));
    }

    @Test
    public void getChildCountCanCountChildrenOfString() {
        int rootCount = model.getChildCount(""),
                classCount = model.getChildCount("Classes"),
                classFeatureCount = model.getChildCount("Class Features");

        assertThat(rootCount, is(14));
        assertThat(classCount, is(12));
        assertThat(classFeatureCount, is(115));
    }

    @Test
    public void getChildCountCanCountChildrenOfObject() {
        int specificClassCount = model.getChildCount(model.getChild("Classes", 0)),
                specificLeaf = model.getChildCount(((JSONObject) model.getChild("Classes", 0)).get("Type"));

        assertThat(specificClassCount, is(3));
        assertThat(specificLeaf, is(0));
    }

    @Test
    public void isLeaf() {
        boolean rootIsLeaf = model.isLeaf(""),
                actionsIsLeaf = model.isLeaf("Actions"),
                classLeafIsLeaf = model.isLeaf(((JSONObject) model.getChild("Classes", 0)).get("Type"));

        assertThat(rootIsLeaf, is(false));
        assertThat(actionsIsLeaf, is(false));
        assertThat(classLeafIsLeaf, is(true));
    }

    @Test
    public void getIndexOfChild() {
        int index = model.getIndexOfChild("General", model.getChild("General", 0)),
                backgroundIndex = model.getIndexOfChild("Backgrounds", model.getChild("Backgrounds", 6));

        assertThat(index, is(0));
        assertThat(backgroundIndex, is(6));
    }
}