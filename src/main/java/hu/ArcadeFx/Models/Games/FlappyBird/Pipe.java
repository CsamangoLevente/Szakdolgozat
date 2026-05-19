package hu.ArcadeFx.Models.Games.FlappyBird;

public class Pipe {
    private double x;
    private final double y;
    private final boolean top;

    public Pipe(double x, double y, boolean top) {
        this.x = x;
        this.y = y;
        this.top = top;
    }

    public void move(double dx) {
        this.x += dx;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isTop() { return top; }
}
