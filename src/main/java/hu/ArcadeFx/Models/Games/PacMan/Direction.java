package hu.ArcadeFx.Models.Games.PacMan;

public enum Direction {
    UP('U', 0, -1),
    DOWN('D', 0, 1),
    LEFT('L', -1, 0),
    RIGHT('R', 1, 0);

    public final char code;
    public final int dx;
    public final int dy;

    Direction(char code, int dx, int dy) {
        this.code = code;
        this.dx = dx;
        this.dy = dy;
    }

    public Direction opposite() {
        switch (this) {
            case UP: return DOWN;
            case DOWN: return UP;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            default: throw new IllegalStateException("Unknown direction: " + this);
        }
    }

    public boolean isOpposite(Direction other) {
        return other != null && this.opposite() == other;
    }
}
