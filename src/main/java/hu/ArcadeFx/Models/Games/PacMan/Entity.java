package hu.ArcadeFx.Models.Games.PacMan;

public class Entity {
    protected int x;
    protected int y;
    protected final int width;
    protected final int height;
    protected final int startX;
    protected final int startY;

    public Entity(int x, int y, int width, int height) {
        this.x = x; this.y = y;
        this.width = width; this.height = height;
        this.startX = x; this.startY = y;
    }

    public void reset() {
        this.x = startX;
        this.y = startY;
    }

    public boolean intersects(Entity other) {
        return x < other.x + other.width &&
                x + width > other.x &&
                y < other.y + other.height &&
                y + height > other.y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
