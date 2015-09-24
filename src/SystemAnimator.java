import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class runs the animation itself.
 * Most of its methods are simple overrides of Runnable that keep the simulation running.
 * Creates a single SystemSimulator by default, but the addition of multiple could allow for comparison of initial conditions.
 */
public class SystemAnimator extends JPanel
        implements Runnable, ActionListener
{
    public static final int B_WIDTH = 1600;
    public static final int B_HEIGHT = 900;
    public static final int B_DEPTH = 900;
    private final int DELAY = 1;

    private Thread animator;

    //Create JPanels for each button
    JPanel resetPanel = new JPanel();

    //Create reset button
    JButton resetButton = new JButton("Reset");

    public SystemAnimator()
    {
        initBoard();
    }

    public SystemSimulator sysSim = new SystemSimulator();

    private void initBoard()
    {
        //Name the action message used by the reset button
        //Add SystemDrawer as the ActionListener of the button
        resetButton.setActionCommand("reset");
        resetButton.addActionListener(this);

        //Prepare window to draw GUI elements
        this.setLayout(null);

        //Set the size and location of the button, and add it to the screen
        resetPanel.setLayout(null);
        resetButton.setSize(100, 50);
        resetPanel.add(resetButton);
        add(resetPanel);
        resetPanel.setBounds(0, 0, 100, 50);

        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        setDoubleBuffered(true);
    }

    @Override
    public void addNotify()
    {
        super.addNotify();

        animator = new Thread(this);
        animator.start();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        sysSim.draw(g2);
    }

    @Override
    public void run()
    {

        long beforeTime, timeDiff, sleep;

        beforeTime = System.currentTimeMillis();

        while (true)
        {
            try
            {
                sysSim.updateSystem();
            }
            catch (Exception e)
            {
                System.out.println("SYSTEM DROPPED SIM STEP");
            }
            repaint();

            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = DELAY - timeDiff;

            if (sleep < 0)
            {
                sleep = 2;
            }

            try
            {
                Thread.sleep(sleep);
            } catch (InterruptedException e)
            {
                System.out.println("Interrupted: " + e.getMessage());
            }

            beforeTime = System.currentTimeMillis();
        }
    }

    //Event handling for reset button
    public void actionPerformed(ActionEvent e) {
        if ("reset".equals(e.getActionCommand()))
        {
            sysSim.reset();
        }
    }
}