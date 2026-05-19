package hu.ArcadeFx.Views.Games.PacMan;

import hu.ArcadeFx.Models.Games.PacMan.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

public class PacManView extends Pane {

    private final Canvas canvas = new Canvas(GameModel.BOARD_WIDTH, GameModel.BOARD_HEIGHT);
    private final GraphicsContext g = canvas.getGraphicsContext2D();
    private final Image wallImage;
    private final Map<GhostType, Image> ghostImages = new EnumMap<>(GhostType.class);
    private final Map<Direction, Image> pacmanImages = new EnumMap<>(Direction.class);

    public PacManView() {
        setPrefSize(GameModel.BOARD_WIDTH, GameModel.BOARD_HEIGHT);
        setMinSize(GameModel.BOARD_WIDTH, GameModel.BOARD_HEIGHT);
        setMaxSize(GameModel.BOARD_WIDTH, GameModel.BOARD_HEIGHT);
        setFocusTraversable(true);
        getChildren().add(canvas);
        wallImage = loadImage("wall.png");
        ghostImages.put(GhostType.BLUE, loadImage("blueGhost.png"));
        ghostImages.put(GhostType.ORANGE, loadImage("orangeGhost.png"));
        ghostImages.put(GhostType.PINK, loadImage("pinkGhost.png"));
        ghostImages.put(GhostType.RED, loadImage("redGhost.png"));
        pacmanImages.put(Direction.UP, loadImage("pacmanUp.png"));
        pacmanImages.put(Direction.DOWN, loadImage("pacmanDown.png"));
        pacmanImages.put(Direction.LEFT, loadImage("pacmanLeft.png"));
        pacmanImages.put(Direction.RIGHT, loadImage("pacmanRight.png"));
    }

    public void render(GameModel model) {
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, GameModel.BOARD_WIDTH, GameModel.BOARD_HEIGHT);


        Pacman p = model.getPacman();
        if (p != null) {
            Image pImg = pacmanImages.getOrDefault(p.getDirection(), pacmanImages.get(Direction.RIGHT));
            g.drawImage(pImg, p.getX(), p.getY(), p.getWidth(), p.getHeight());
        }


        for (Ghost ghost : model.getGhosts()) {
            Image img = ghostImages.get(ghost.getType());
            if (img != null) {
                g.drawImage(img, ghost.getX(), ghost.getY(), ghost.getWidth(), ghost.getHeight());
            }
        }


        for (Wall wall : model.getWalls()) {
            g.drawImage(wallImage, wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight());
        }

        g.setFill(Color.WHITE);
        for (Food food : model.getFoods()) {
            g.fillRect(food.getX(), food.getY(), food.getWidth(), food.getHeight());
        }

        g.setFont(Font.font("Arial", 18));
        g.setFill(Color.WHITE);
        if (model.isGameOver()) {
            g.fillText("Game Over: " + model.getScore(), GameModel.TILE_SIZE / 2.0, GameModel.TILE_SIZE * 0.8);
        } else {
            g.fillText("x" + model.getLives() + " Score: " + model.getScore(),
                    GameModel.TILE_SIZE / 2.0, GameModel.TILE_SIZE * 0.8);
        }
    }

    public Image loadImage(String fileName) {
        URL url = getClass().getResource("/images/" + fileName);
        if (url == null) {
            throw new IllegalStateException("Hiányzó resource: /images/" + fileName);
        }
        return new Image(url.toExternalForm());
    }
}
