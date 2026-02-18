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
        // optional: tell UI something changed
        firePropertyChange("me", null, me);
    }

    /**
     * payload is coming from MQTT like: "id,x,y"
     * Example: "bob,420,300"
     */
    public void addPlayerFromPayload(String payload) {
        if (payload == null || payload.isBlank()) return;
        String[] parts = payload.split(",");
        if (parts.length < 3) return;

        String id = parts[0].trim();
        if (me != null && id.equals(me.getId())) return; // ignore self

        int x, y;
        try {
            x = Integer.parseInt(parts[1].trim());
            y = Integer.parseInt(parts[2].trim());
        } catch (NumberFormatException e) { return; }

        Player p = findOrCreate(id);
        p.setX(x);
        p.setY(y);

        firePropertyChange("players", null, players); // always fires since old=null
    }

    /**
     * Finds a player by id, or creates and stores a new one if missing.
     */
    private Player findOrCreate(String id) {
        for (Player p : players) {
            if (p.getId().equals(id)) {
                return p;
            }
        }

        // We don’t know your Player constructor exactly.
        // Based on what you showed earlier: Player(id, x, y, Color)
        // For remote players, pick a default starting position and color.
        Player created = new Player(id, 0, 0, java.awt.Color.RED);
        players.add(created);
        return created;
    }

    // --- Movement API used by MyKeyListener ---

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
