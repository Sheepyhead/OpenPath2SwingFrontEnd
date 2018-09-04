import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import javax.swing.tree.DefaultMutableTreeNode;

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
    public void getRootReturnsRoot() {
        DefaultMutableTreeNode obj = model.getRoot();

        assertThat(obj.getUserObject(), instanceOf(JSONObject.class));

        DefaultMutableTreeNode general = model.getChild(obj,"Ancestries");
        assertThat(general.getUserObject(), instanceOf(JSONArray.class));
    }

    @Test
    public void getChildCanReturnObjectsFromRoot() {
        DefaultMutableTreeNode firstRootChild = model.getChild(model.getRoot(), 0);
        DefaultMutableTreeNode secondRootChild = model.getChild(model.getRoot(), 1);

        assertThat(firstRootChild.getUserObject(), instanceOf(KeyValuePair.class));
        assertThat(secondRootChild.getUserObject(), instanceOf(KeyValuePair.class));
    }

    @Test
    public void getChildCanReturnSpecificObjects() {
        DefaultMutableTreeNode actionObject = model.getChild("Actions", 0);
        assertThat(actionObject.getUserObject(), instanceOf(JSONObject.class));
        JSONObject action = (JSONObject) actionObject.getUserObject();

        assertThat(action.get("Type"), is("Action"));
        assertThat(action.get("Name"), is("Quick Alchemy"));
        assertThat(action.get("Action"), is(1L));

        DefaultMutableTreeNode classFeatureObject = model.getChild("Class Features", 0);
        assertThat(classFeatureObject.getUserObject(), instanceOf(JSONObject.class));
        JSONObject classFeature = (JSONObject) classFeatureObject.getUserObject();

        assertThat(classFeature.get("Type"), is("Class Feature"));
        assertThat(classFeature.get("Name"), is("Advanced Alchemy"));
        assertThat(classFeature.get("Info"), instanceOf(JSONObject.class));
        assertThat(((JSONObject) classFeature.get("Info")).get("Actions"), instanceOf(JSONArray.class));
        assertThat(((JSONArray) ((JSONObject) classFeature.get("Info")).get("Actions")).size(), is(1));
    }

    @Test
    public void getChildCanReturnSpecificObjectsFromJSONObject() {
        DefaultMutableTreeNode actionObject = model.getChild("Actions", 0);
        assertThat(actionObject.getUserObject(), instanceOf(JSONObject.class));
        JSONObject action = (JSONObject) actionObject.getUserObject();

        DefaultMutableTreeNode specificActionObject = model.getChild(action.get("Cost"), 0);

        assertThat(specificActionObject.getUserObject(), instanceOf(JSONObject.class));
        JSONObject specificAction = (JSONObject) specificActionObject.getUserObject();
        assertThat(specificAction.get("Type"), is("Resonance"));
        assertThat(specificAction.get("Amount"), is(1L));

        DefaultMutableTreeNode randomObjectInAction = model.getChild(action, 2);

        assertThat(((KeyValuePair)randomObjectInAction.getUserObject()).getKey(), instanceOf(String.class));
    }

    @Test
    public void getChildCountCanCountChildrenOfObject() {
        int specificClassCount = model.getChildCount(model.getChild(model.getChild(model.getRoot(), "Classes"), 0)),
                specificLeaf = model.getChildCount(model.getChild(model.getChild(model.getChild(model.getRoot(), "Classes"), 0), "Type"));

        assertThat(specificClassCount, is(3));
        assertThat(specificLeaf, is(0));
    }

    @Test
    public void isLeaf() {
        boolean rootIsLeaf = model.isLeaf(model.getRoot()),
                actionsIsLeaf = model.isLeaf(model.getChild(model.getRoot(),"Actions")),
                classLeafIsLeaf = model.isLeaf(model.getChild(model.getChild(model.getRoot(),"Class Features"),"Type"));

        assertThat(rootIsLeaf, is(false));
        assertThat(actionsIsLeaf, is(false));
        assertThat(classLeafIsLeaf, is(true));
    }

    @Test
    public void getIndexOfChild() {
        int index = model.getIndexOfChild(model.getChild(model.getRoot(), "General"),model.getChild(model.getChild(model.getRoot(),"General"),0)),
                backgroundIndex = model.getIndexOfChild(model.getChild(model.getRoot(),"Backgrounds"), model.getChild(model.getChild(model.getRoot(), "Backgrounds"), 6));

        assertThat(index, is(0));
        assertThat(backgroundIndex, is(6));
    }
}