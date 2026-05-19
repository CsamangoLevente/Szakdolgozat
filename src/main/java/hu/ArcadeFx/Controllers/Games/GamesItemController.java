package hu.ArcadeFx.Controllers.Games;

import hu.ArcadeFx.Models.Games.Gamess;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class GamesItemController {
    public VBox gamesItemVBox;
    public HBox gamesItemHBox;
    public Label gamesItemLabel;
    public ImageView gamesItemImgView;

    private Gamess games;

    public void setData(Gamess game) {
        this.games = game;
        gamesItemLabel.setText(game.getName());
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(game.getImgSrc())));
        gamesItemImgView.setImage(image);
    }
}
