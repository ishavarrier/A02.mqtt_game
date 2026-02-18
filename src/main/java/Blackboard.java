import java.awt.*;
import java.beans.PropertyChangeSupport;
import java.util.Vector;

public class Blackboard extends PropertyChangeSupport {

    public static final String BROKER = "tcp://broker.hivemq.com:1883";
    public static final String TOPIC  = "csc509/brokerverse";

    private Player me;
    private Vector<Player> players = new Vector<>();

    private static volatile Blackboard instance;

    private Blackboard() {
        super(new Object());
    }

    public static Blackboard getInstance() {
        if (instance == null) {
            synchronized (Blackboard.class) {
                if (instance == null) {
                    instance = new Blackboard();
                }
            }
        }
        return instance;
    }

    public Vector<Player> getPlayers() {
        return players;
    }

    public Player getMe() {
        return me;
    }

    public void setMe(Player me) {
        this.me = me;
        firePropertyChange("me", null, me);
    }

    public void addPlayerFromPayload(String payload) {
        if (payload == null || payload.isBlank()) return;
        String[] parts = payload.split(",");
        if (parts.length < 6) return;

        String id = parts[0].trim();
        if (me != null && id.equals(me.getId())) return;

        try {
            int x = Integer.parseInt(parts[1].trim());
            int y = Integer.parseInt(parts[2].trim());
            int r = Integer.parseInt(parts[3].trim());
            int g = Integer.parseInt(parts[4].trim());
            int b = Integer.parseInt(parts[5].trim());
            Color color = new Color(r, g, b);

            Player p = findOrCreate(id, x, y, color);
            p.setX(x);
            p.setY(y);
            p.setColor(color);

            firePropertyChange("players", null, players);
        } catch (NumberFormatException e) { return; }
    }

    /**
     * Finds a player by id, or creates and stores a new one if missing.
     */
    private Player findOrCreate(String id, int x, int y, Color color) {
        for (Player p : players) {
            if (p.getId().equals(id)) return p;
        }
        // use the color passed in from the payload, not a random one
        Player created = new Player(id, x, y, color);
        players.add(created);
        return created;
    }


    private static final int STEP = 20; // matches your grid cells

    public void up() {
        if (me == null) return;
        int oldY = me.getY();
        me.setY(oldY - STEP);
        firePropertyChange("position", oldY, me.getY());
    }

    public void down() {
        if (me == null) return;
        int oldY = me.getY();
        me.setY(oldY + STEP);
        firePropertyChange("position", oldY, me.getY());
    }

    public void left() {
        if (me == null) return;
        int oldX = me.getX();
        me.setX(oldX - STEP);
        firePropertyChange("position", oldX, me.getX());
    }

    public void right() {
        if (me == null) return;
        int oldX = me.getX();
        me.setX(oldX + STEP);
        firePropertyChange("position", oldX, me.getX());
    }
}
