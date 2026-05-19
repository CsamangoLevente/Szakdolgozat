package hu.ArcadeFx.Controllers.Games.FlappyBird;

import hu.ArcadeFx.Models.Games.FlappyBird.FlappyBirdModel;
import hu.ArcadeFx.Views.Games.FlappyBird.FlappyBirdView;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class FlappyBirdController {

    private final FlappyBirdModel model;
    private final FlappyBirdView view;

    public FlappyBirdController(FlappyBirdModel model, FlappyBirdView view) {
        this.model = model;
        this.view = view;
        view.render(model);
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                model.tick();
                view.render(model);
            }
        }.start();
    }

    public void attach(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                model.onSpacePressed();
            }
        });
    }
}
