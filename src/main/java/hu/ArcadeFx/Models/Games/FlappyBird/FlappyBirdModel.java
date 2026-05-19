package hu.ArcadeFx.Models.Games.FlappyBird;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlappyBirdModel {

    private final int boardWidth;
    private final int boardHeight;
    private final double gravity = 0.4;
    private final double flapVelocity = -7;
    private final double pipeSpeedPerTick = 3;
    private final double pipeOriginalWidth = 128;
    private final double pipeOriginalHeight = 768;
    private final double pipeDrawWidth = 52;
    private final double pipeDrawHeight = (pipeDrawWidth / pipeOriginalWidth) * pipeOriginalHeight;
    private final double pipeGap = 150;
    private final Bird bird;
    private final ArrayList<Pipe> pipes = new ArrayList<>();
    private final Random rand = new Random();
    private boolean gameOver = false;
    private double score = 0;
    public FlappyBirdModel(int width, int height) {
        this.boardWidth = width;
        this.boardHeight = height;
        this.bird = new Bird(50, 200, 34, 24);
        spawnPipe();
    }

    public void onSpacePressed() {
        if (gameOver) {
            reset();
        }
        bird.flap(flapVelocity);
    }

    public void tick() {
        if (gameOver) return;
        bird.applyGravity(gravity);
        bird.move();

        for (Pipe p : pipes) {
            p.move(-pipeSpeedPerTick);
        }

        if (!pipes.isEmpty() && pipes.get(0).getX() < -pipeDrawWidth) {
            pipes.remove(0);
            pipes.remove(0);
            spawnPipe();
            score++;
        }

        for (Pipe p : pipes) {
            boolean overlap = bird.intersects(p.getX(), p.getY(), pipeDrawWidth, pipeDrawHeight);
            if (overlap) {
                gameOver = true;
                break;
            }
        }

        if (bird.getY() > boardHeight || bird.getY() < 0) {
            gameOver = true;
        }
    }

    private void spawnPipe() {
        double centerY = 150 + rand.nextInt(250);
        double topY = centerY - pipeGap / 2 - pipeDrawHeight;
        pipes.add(new Pipe(boardWidth, topY, true));
        double bottomY = centerY + pipeGap / 2;
        pipes.add(new Pipe(boardWidth, bottomY, false));
    }

    public void reset() {
        bird.reset();
        pipes.clear();
        spawnPipe();
        score = 0;
        gameOver = false;
    }

    public int getBoardWidth() { return boardWidth; }
    public int getBoardHeight() { return boardHeight; }
    public Bird getBird() { return bird; }
    public List<Pipe> getPipes() { return List.copyOf(pipes); }
    public boolean isGameOver() { return gameOver; }
    public int getScore() { return (int) score; }
    public double getPipeDrawWidth() { return pipeDrawWidth; }
    public double getPipeDrawHeight() { return pipeDrawHeight; }
}
