package hu.ArcadeFx.Models.Games.PacMan;

public class Ghost extends MovingEntity {

    private final GhostType type;

    public Ghost(GhostType type, int x, int y, int w, int h) {
        super(x, y, w, h);
        this.type = type;
    }

    public GhostType getType() {
        return type;
    }
}
