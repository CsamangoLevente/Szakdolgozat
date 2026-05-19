package hu.ArcadeFx.Network.PacMan;

import hu.ArcadeFx.Models.Games.PacMan.Direction;
import hu.ArcadeFx.Models.Games.PacMan.GameModel;
import hu.ArcadeFx.Models.Games.PacMan.Pacman;

public class MultiplayerPlayer {

    private final Pacman pacman;

    private final int spawnX;
    private final int spawnY;
    private final Direction spawnDirection;
    private Direction requestedDirection;

    private int score = 0;
    private int lives = 3;
    private boolean alive = true;

    public MultiplayerPlayer(Pacman pacman, Direction direction) {
        this.pacman = pacman;
        this.spawnX = pacman.getX();
        this.spawnY = pacman.getY();
        this.spawnDirection = direction;
        this.requestedDirection = direction;
        pacman.setDirection(direction, GameModel.SPEED);
    }

    public Pacman getPacman() {
        return pacman;
    }

    public Direction getDirection() {
        return requestedDirection;
    }

    public void setDirection(Direction direction) {
        this.requestedDirection = direction;
    }

    public Direction getCurrentDirection() {
        return pacman.getDirection();
    }

    public int getX() {
        return pacman.getX();
    }

    public int getY() {
        return pacman.getY();
    }

    public int getCenterX() {
        return getX() + GameModel.TILE_SIZE / 2;
    }

    public int getCenterY() {
        return getY() + GameModel.TILE_SIZE / 2;
    }

    public void addScore(int value) {
        score += value;
    }

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        lives--;
        if (lives <= 0) {
            lives = 0;
            alive = false;
        } else {
            resetPosition();
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public void resetPosition() {
        pacman.setPosition(spawnX, spawnY);
        pacman.setDirection(spawnDirection, GameModel.SPEED);
        requestedDirection = spawnDirection;
    }

    public void resetFull() {
        resetPosition();
        score = 0;
        lives = 3;
        alive = true;
    }

    public String toNetworkString() {
        return getX() + "," + getY() + "," + getCurrentDirection().name() + "," + score + "," + lives + "," + alive;
    }
}