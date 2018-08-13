import javax.swing.*;
import java.awt.*;

/**
 * Created by Troels "Sheepyhead" Jessen on 2018-08-13.
 * Email: kairyuka@live.dk
 */
public class MainWindow {

    private JPanel mainPanel;

    public MainWindow() {
        createUIComponents();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        mainPanel = new JPanel();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainWindow");
        MainWindow main = new MainWindow();
        frame.setContentPane(main.getMainPanel());
        frame.setIconImage(new ImageIcon(System.getProperty("user.dir") + "/Resources/icon.png").getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
