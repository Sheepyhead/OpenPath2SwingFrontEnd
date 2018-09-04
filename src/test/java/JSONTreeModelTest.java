import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
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
    public void getChildCanReturnObjectsFromRoot() {
        Object firstRootChild = model.getChild(0);
        Object secondRootChild = model.getChild(1);

        assertThat(firstRootChild, instanceOf(JSONMutableTreeNode.class));
        assertThat(secondRootChild, instanceOf(JSONMutableTreeNode.class));
    }

    @Test
    public void getChildCanReturnSpecificObjects() {
        JSONMutableTreeNode action = model.getChild("Actions").getChild(0);

        assertThat(action.getChild("Type").getAsString(), is("Action"));
        assertThat(action.getChild("Name").getAsString(), is("Quick Alchemy"));
        assertThat(action.getChild("Action").getAsLong(), is(1L));

        JSONMutableTreeNode classFeature = model.getChild("Class Features").getChild(0);

        assertThat(classFeature.getChild("Type").getAsString(), is("Class Feature"));
        assertThat(classFeature.getChild("Name").getAsString(), is("Advanced Alchemy"));
        assertThat(classFeature.getChild("Info").getAsObject(), notNullValue());
        assertThat(classFeature.getChild("Info").getChild("Actions").getAsArray(), notNullValue());
        assertThat(classFeature.getChild("Info").getChild("Actions").getChildCount(), is(1));
    }

    @Test
    public void getChildCanReturnSpecificObjectsFromJSONObject() {
        JSONMutableTreeNode actionCost = model.getChild("Actions").getChild(0).getChild("Cost").getChild(0);

        assertThat(actionCost.getChild("Type").getAsString(), is("Resonance"));
        assertThat(actionCost.getChild("Amount").getAsLong(), is(1L));

    }

    @Test
    public void getChildCountCanCountChildrenOfString() {
        int rootCount = model.getChildCount(),
                classCount = model.getChild("Classes").getChildCount(),
                classFeatureCount = model.getChild("Class Features").getChildCount();

        assertThat(rootCount, is(14));
        assertThat(classCount, is(12));
        assertThat(classFeatureCount, is(115));
    }

    @Test
    public void getChildCountCanCountChildrenOfObject() {
        int specificClassCount = model.getChild("Classes").getChild(0).getChildCount(),
                specificLeaf = model.getChild("Classes").getChild(0).getChild("Type").getChildCount();

        assertThat(specificClassCount, is(3));
        assertThat(specificLeaf, is(0));
    }

    @Test
    public void isLeaf() {
        boolean rootIsLeaf = model.isLeaf(),
                actionsIsLeaf = model.getChild("Actions").isLeaf(),
                classLeafIsLeaf = model.getChild("Classes").getChild(0).getChild("Type").isLeaf();

        assertThat(rootIsLeaf, is(false));
        assertThat(actionsIsLeaf, is(false));
        assertThat(classLeafIsLeaf, is(true));
    }

    @Test
    public void getIndexOfChild() {
        int index = model.getIndexOfChild(model.getChild("General"), model.getChild("General").getChild(0)),
                backgroundIndex = model.getIndexOfChild(model.getChild("Backgrounds"), model.getChild("Backgrounds").getChild(6));

        assertThat(index, is(0));
        assertThat(backgroundIndex, is(6));
    }
}