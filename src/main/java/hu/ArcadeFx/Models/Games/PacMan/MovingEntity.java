package hu.ArcadeFx.Models.Games.PacMan;

public class MovingEntity extends Entity {

    protected Direction direction = Direction.RIGHT;
    protected int velocityX = 0;
    protected int velocityY = 0;

    public MovingEntity(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void setDirection(Direction dir, int speed) {
        this.direction = dir;
        this.velocityX = dir.dx * speed;
        this.velocityY = dir.dy * speed;
    }

    public void stop() {
        velocityX = 0;
        velocityY = 0;
    }

    public void step() {
        x += velocityX;
        y += velocityY;
    }

    public Direction getDirection() { return direction; }
    public int getVelocityX() { return velocityX; }
    public int getVelocityY() { return velocityY; }
}
