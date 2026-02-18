import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class Main extends JFrame {

    public Main() {
        WorldPanel worldPanel = new WorldPanel();
        add(worldPanel, BorderLayout.CENTER);
        worldPanel.setFocusable(true);
        worldPanel.requestFocusInWindow();
        worldPanel.addKeyListener(new MyKeyListener());

        Blackboard.getInstance().addPropertyChangeListener(worldPanel);
    }

    public static void main(String[] args) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        Player me = new Player(
                args.length > 0 ? args[0] : id,
                400,
                300,
                Color.BLUE
        );
        Blackboard.getInstance().setMe(me);

        SwingUtilities.invokeLater(() -> {
            Publisher publisher = new Publisher(Blackboard.BROKER, new String[]{Blackboard.TOPIC});
            Blackboard.getInstance().addPropertyChangeListener(publisher);
            
            Main main = new Main();
            main.setSize(800, 600);
            main.setResizable(false);
            main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            main.setVisible(true);
        });



        Subscriber subscriber = new Subscriber(Blackboard.BROKER, new String[]{Blackboard.TOPIC});
    }
}