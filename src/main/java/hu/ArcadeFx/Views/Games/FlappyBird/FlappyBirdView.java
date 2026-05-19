package hu.ArcadeFx.Views.Games.FlappyBird;

import hu.ArcadeFx.Models.Games.FlappyBird.Bird;
import hu.ArcadeFx.Models.Games.FlappyBird.FlappyBirdModel;
import hu.ArcadeFx.Models.Games.FlappyBird.Pipe;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.InputStream;

public class FlappyBirdView extends Pane {

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Image backgroundImg;
    private final Image birdImg;
    private final Image topPipeImg;
    private final Image bottomPipeImg;

    public FlappyBirdView(int width, int height) {
        setPrefSize(width, height);
        setFocusTraversable(true);
        this.canvas = new Canvas(width, height);
        this.gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);
        backgroundImg = load("flappybirdbg.png");
        birdImg = load("flappybird.png");
        topPipeImg = load("toppipe.png");
        bottomPipeImg = load("bottompipe.png");
    }

    public void render(FlappyBirdModel model) {
        gc.clearRect(0, 0, model.getBoardWidth(), model.getBoardHeight());
        gc.drawImage(backgroundImg, 0, 0, model.getBoardWidth(), model.getBoardHeight());
        for (Pipe p : model.getPipes()) {
            if (p.isTop()) {
                gc.drawImage(topPipeImg, p.getX(), p.getY(), model.getPipeDrawWidth(), model.getPipeDrawHeight());
            } else {
                gc.drawImage(bottomPipeImg, p.getX(), p.getY(), model.getPipeDrawWidth(), model.getPipeDrawHeight());
            }
        }
        Bird b = model.getBird();
        gc.drawImage(birdImg, b.getX(), b.getY(), b.getWidth(), b.getHeight());
        gc.setFill(Color.BLACK);
        gc.fillText("Score: " + model.getScore(), 10, 20);
        if (model.isGameOver()) {
            gc.fillText("Game Over! Press SPACE", 80, model.getBoardHeight() / 2.0);
        }
    }

    private Image load(String fileName) {
        InputStream is = getClass().getResourceAsStream("/images/" + fileName);
        if (is == null) {
            throw new IllegalStateException("Hiányzó resource: /images/" + fileName);
        }
        return new Image(is);
    }
}
