/**
 * Created by Luke on 11/20/2014.
 */
import javax.swing.*;
import java.awt.*;

/**
 * Created by Luke on 9/17/2014.
 */
public class GUI extends JFrame
{
    public GUI() {

        initUI();
    }

    private void initUI() {

        add(new SystemAnimator());

        setResizable(false);
        pack();

        setTitle("Orbital Sim");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame ex = new GUI();
                ex.setVisible(true);
            }
        });
    }
}
