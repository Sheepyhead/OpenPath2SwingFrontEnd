
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.FileReader;

/**
 * Created by Troels "Sheepyhead" Jessen on 2018-08-13.
 * Email: kairyuka@live.dk
 */
public class MainWindow extends JPanel {

    private JTree mainTree;
    private JScrollPane treeView;

    public MainWindow() {
        super(new GridLayout(1,0));
        createUIComponents();

        JSONTreeModel treeModel = new JSONTreeModel(System.getProperty("user.dir") + "/External/Open-Path-2-Project/Data/Master List.json");
        mainTree.setModel(treeModel);
        mainTree.setCellRenderer(new JSONTreeCellRenderer());
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainWindow");
        MainWindow main = new MainWindow();
        frame.setContentPane(main);
        frame.setIconImage(new ImageIcon(System.getProperty("user.dir") + "/Resources/icon.png").getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,800);
        frame.setVisible(true);
    }

    private void createUIComponents() {
        mainTree = new JTree(new DefaultMutableTreeNode("Test"));
        treeView = new JScrollPane(mainTree);
        add(treeView);

    }
}
