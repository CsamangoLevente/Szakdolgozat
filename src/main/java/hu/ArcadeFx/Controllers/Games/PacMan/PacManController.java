package hu.ArcadeFx.Controllers.Games.PacMan;

import hu.ArcadeFx.Models.Games.PacMan.Direction;
import hu.ArcadeFx.Models.Games.PacMan.GameModel;
import hu.ArcadeFx.Views.Games.PacMan.PacManView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

public class PacManController {

    private final GameModel model;
    private final PacManView view;
    private final Timeline gameLoop;

    public PacManController(GameModel model, PacManView view) {
        this.model = model;
        this.view = view;
        Timeline loop = new Timeline();
        KeyFrame frame = new KeyFrame(Duration.millis(50), e -> {
            this.model.tick();
            this.view.render(this.model);

            if (this.model.isGameOver()) {
                loop.stop();
            }
        });
        loop.getKeyFrames().add(frame);
        loop.setCycleCount(Animation.INDEFINITE);
        this.gameLoop = loop;
        this.view.render(this.model);
        this.gameLoop.play();
    }


    public void attach(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (model.isGameOver()) {
                model.restart();
                view.render(model);
                gameLoop.playFromStart();
                return;
            }
            Direction dir = toDirection(e.getCode());
            if (dir != null) {
                model.onDirectionInput(dir);
            }
        });
    }

    public Direction toDirection(KeyCode code) {
        if (code == KeyCode.UP) return Direction.UP;
        if (code == KeyCode.DOWN) return Direction.DOWN;
        if (code == KeyCode.LEFT) return Direction.LEFT;
        if (code == KeyCode.RIGHT) return Direction.RIGHT;
        return null;
    }
}
