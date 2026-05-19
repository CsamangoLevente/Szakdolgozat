package hu.ArcadeFx.Models.Games.FlappyBird;

public class Bird {
    private double x;
    private double y;
    private double velocityY;
    private final double width;
    private final double height;
    private final double startX;
    private final double startY;

    public Bird(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.startX = x;
        this.startY = y;
        this.velocityY = 0;
    }

    public void flap(double flapVelocity) {
        this.velocityY = flapVelocity;
    }

    public void applyGravity(double gravity) {
        this.velocityY += gravity;
    }

    public void move() {
        this.y += this.velocityY;
    }

    public void reset() {
        this.x = startX;
        this.y = startY;
        this.velocityY = 0;
    }

    public boolean intersects(double rx, double ry, double rw, double rh) {
        return x < rx + rw && x + width > rx && y < ry + rh && y + height > ry;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getVelocityY() { return velocityY; }
    public void setY(double y) { this.y = y; }
}
