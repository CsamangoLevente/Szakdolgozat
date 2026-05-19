package hu.ArcadeFx.Controllers.Games.SnakeGame;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import javafx.scene.image.Image;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeGame extends Pane {

    private final int tileSize = 25;
    private int width;
    private int height;

    private Canvas canvas;
    private GraphicsContext gc;

    private AnimationTimer timer;

    private List<int[]> snake = new ArrayList<>();
    private int dirX = 1, dirY = 0;

    private int foodX, foodY;
    private Random rnd = new Random();

    private boolean gameOver = false;

    private Image backgroundTile, fenceStraight, fenceCorner, appleImage, headImg,
            tailImg, straightImg, cornerImg;

    public SnakeGame(int width, int height) {
        this.width = width;
        this.height = height;

        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        this.getChildren().add(canvas);
        backgroundTile = loadImage("grass.png");
        fenceStraight = loadImage("fence_straight.png");
        fenceCorner = loadImage("fence_corner.png");
        appleImage = loadImage("apple.png");
        headImg = loadImage("snake_head.png");
        tailImg = loadImage("snake_tail.png");
        straightImg = loadImage("snake_straight.png");
        cornerImg = loadImage("snake_corner.png");
        resetGame();
        startGameLoop();
    }

    private Image loadImage(String fileName) {
        URL url = getClass().getResource("/images/SnakeGame/" + fileName);
        if (url == null) {
            throw new IllegalStateException("Hiányzó resource: /images/SnakeGame/" + fileName);
        }
        return new Image(url.toExternalForm());
    }

    private void drawRotatedImage(GraphicsContext gc, Image img, double angle, double x, double y) {
        gc.save();
        gc.translate(x + tileSize / 2, y + tileSize / 2);
        gc.rotate(angle);
        gc.translate(-tileSize / 2, -tileSize / 2);
        gc.drawImage(img, 0, 0, tileSize, tileSize);
        gc.restore();
    }

    private void drawFence() {
        int tilesX = width / tileSize;
        int tilesY = height / tileSize;
        drawRotatedImage(gc, fenceCorner, 0, 0, 0, tileSize, tileSize);
        drawRotatedImage(gc, fenceCorner, 90, (tilesX - 1) * tileSize, 0, tileSize, tileSize);
        drawRotatedImage(gc, fenceCorner, 180, (tilesX - 1) * tileSize, (tilesY - 1) * tileSize, tileSize, tileSize);
        drawRotatedImage(gc, fenceCorner, 270, 0, (tilesY - 1) * tileSize, tileSize, tileSize);

        for (int x = 1; x < tilesX - 1; x++) {
            drawRotatedImage(gc, fenceStraight, 90, x * tileSize, 0, tileSize, tileSize);                 // TOP
            drawRotatedImage(gc, fenceStraight, 270, x * tileSize, (tilesY - 1) * tileSize, tileSize, tileSize); // BOTTOM
        }
        for (int y = 1; y < tilesY - 1; y++) {
            gc.drawImage(fenceStraight, 0, y * tileSize, tileSize, tileSize);   // LEFT
            drawRotatedImage(gc, fenceStraight, 180,
                    (tilesX - 1) * tileSize, y * tileSize, tileSize, tileSize); // RIGHT
        }
    }

    private void drawRotatedImage(GraphicsContext gc, Image img, double angle, double x, double y, double w, double h) {
        gc.save();
        gc.translate(x + w / 2, y + h / 2);
        gc.rotate(angle);
        gc.translate(-w / 2, -h / 2);
        gc.drawImage(img, 0, 0, w, h);
        gc.restore();
    }

    private void drawTiledBackground() {
        int tileW = (int) backgroundTile.getWidth();
        int tileH = (int) backgroundTile.getHeight();
        for (int y = 0; y < height; y += tileH) {
            for (int x = 0; x < width; x += tileW) {
                gc.drawImage(backgroundTile, x, y);
            }
        }
    }


    private void resetGame() {
        snake.clear();
        snake.add(new int[]{5, 5});
        snake.add(new int[]{4, 5});
        snake.add(new int[]{3, 5});
        placeFood();
        dirX = 1;
        dirY = 0;
        gameOver = false;
    }

    private void startGameLoop() {
        timer = new AnimationTimer() {
            long lastUpdate = 0;
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 150_000_000) {
                    update();
                    render();
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    private void placeFood() {
        int tilesX = width / tileSize;
        int tilesY = height / tileSize;
        foodX = rnd.nextInt(tilesX - 2) + 1;
        foodY = rnd.nextInt(tilesY - 2) + 1;
    }


    public void handleKeyPress(KeyCode code) {
        if (gameOver) {
            resetGame();
            return;
        }
        switch (code) {
            case UP:
                if (dirY != 1) { dirX = 0; dirY = -1; }
                break;
            case DOWN:
                if (dirY != -1) { dirX = 0; dirY = 1; }
                break;
            case LEFT:
                if (dirX != 1) { dirX = -1; dirY = 0; }
                break;
            case RIGHT:
                if (dirX != -1) { dirX = 1; dirY = 0; }
                break;
        }
    }

    private void update() {
        if (gameOver) return;
        int[] head = snake.get(0);
        int newX = head[0] + dirX;
        int newY = head[1] + dirY;
        if (newX <= 0 || newX >= (width / tileSize) - 1 || newY <= 0 || newY >= (height / tileSize) - 1) {
            gameOver = true;
            return;
        }
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(i)[0] == newX && snake.get(i)[1] == newY) {
                gameOver = true;
                return;
            }
        }
        snake.add(0, new int[]{newX, newY});
        if (newX == foodX && newY == foodY) {
            placeFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private void render() {
        drawTiledBackground();
        drawFence();
        gc.drawImage(appleImage, foodX * tileSize, foodY * tileSize, tileSize, tileSize);
        int[] head = snake.get(0);
        int[] neck = snake.get(1);
        int hx = head[0], hy = head[1];
        int nx = neck[0], ny = neck[1];
        double headAngle = 0;
        if (hx > nx) headAngle = 270;
        else if (hx < nx) headAngle = 90;
        else if (hy > ny) headAngle = 0;
        else if (hy < ny) headAngle = 180;
        drawRotatedImage(gc, headImg, headAngle, hx * tileSize, hy * tileSize);
        for (int i = 1; i < snake.size() - 1; i++) {
            int[] prev = snake.get(i - 1);
            int[] curr = snake.get(i);
            int[] next = snake.get(i + 1);
            int cx = curr[0], cy = curr[1];
            int dx1 = curr[0] - prev[0];
            int dy1 = curr[1] - prev[1];
            int dx2 = next[0] - curr[0];
            int dy2 = next[1] - curr[1];
            if (dx1 != dx2 || dy1 != dy2) {
                double angle = 0;
                if (dx1 == -1 && dy1 == 0 && dx2 == 0 && dy2 == 1) angle = 0;
                else if (dx1 == 0 && dy1 == -1 && dx2 == -1 && dy2 == 0) angle = 90;
                else if (dx1 == 1 && dy1 == 0 && dx2 == 0 && dy2 == -1) angle = 180;
                else if (dx1 == 0 && dy1 == 1 && dx2 == 1 && dy2 == 0) angle = 270;
                drawRotatedImage(gc, cornerImg, angle, cx * tileSize, cy * tileSize);
                return;
            }
            else {
                double angle = (dx1 != 0) ? 0 : 90;
                drawRotatedImage(gc, straightImg, angle, cx * tileSize, cy * tileSize);
            }
        }

        int[] tail = snake.get(snake.size() - 1);
        int[] beforeTail = snake.get(snake.size() - 2);
        int tx = tail[0], ty = tail[1];
        int bx = beforeTail[0], by = beforeTail[1];
        double tailAngle = 0;
        if (tx > bx) tailAngle = 270;
        else if (tx < bx) tailAngle = 90;
        else if (ty > by) tailAngle = 0;
        else if (ty < by) tailAngle = 180;
        drawRotatedImage(gc, tailImg, tailAngle, tx * tileSize, ty * tileSize);
        if (gameOver) {
            gc.setFill(Color.WHITE);
            gc.fillText("GAME OVER - nyomj egy gombot!", width / 2.0 - 80, height / 2.0);
        }
    }
}
