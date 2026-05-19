package hu.ArcadeFx.Controllers.Games;

import hu.ArcadeFx.Controllers.Games.FlappyBird.FlappyBirdController;
import hu.ArcadeFx.Controllers.Games.SnakeGame.SnakeGame;
import hu.ArcadeFx.Models.Games.Gamess;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import hu.ArcadeFx.Models.Games.FlappyBird.FlappyBirdModel;
import hu.ArcadeFx.Views.Games.FlappyBird.FlappyBirdView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;
import hu.ArcadeFx.Controllers.Games.PacMan.PacManController;
import hu.ArcadeFx.Models.Games.PacMan.GameModel;
import hu.ArcadeFx.Views.Games.PacMan.PacManView;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class GamesController implements Initializable {
    public ScrollPane gamesScrollPane;
    public GridPane gamesGridPane;
    public HBox gamesHBox;
    public List<Gamess> games = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        games.addAll(getData());
        int column = 0;
        int row = 1;
        try {
            for (int i = 0; i < games.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/Fxml/Games/GamesItem.fxml"));
                AnchorPane card = fxmlLoader.load();
                GamesItemController itemController = fxmlLoader.getController();
                itemController.setData(games.get(i));
                if (column == 3) {
                    column = 0;
                    row++;
                }
                gamesGridPane.add(card, column++, row);
                int index = i;
                card.setOnMouseClicked(event -> {
                    String gameName = games.get(index).getName();
                    gameMap.getOrDefault(gameName, () ->
                            System.out.println("Ismeretlen játék")
                    ).run();
                });
                GridPane.setMargin(card, new Insets(10));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Gamess> getData() {
        List<Gamess> games = new ArrayList<>();
        Gamess game;
        game = new Gamess();
        game.setName("FlappyBird");
        game.setImgSrc("/images/flappybirdK.png");
        games.add(game);
        game = new Gamess();
        game.setName("PacMan");
        game.setImgSrc("/images/pacmanK.png");
        games.add(game);
        game = new Gamess();
        game.setName("PacMan Multiplayer");
        game.setImgSrc("/images/pacmanK.png");
        games.add(game);
        game = new Gamess();
        game.setName("Snake");
        game.setImgSrc("/images/snakeK.png");
        games.add(game);
        return games;
    }

    private final Map<String, Runnable> gameMap = Map.of(
            "FlappyBird", this::startFlappyBird,
            "PacMan", this::startPacMan,
            "PacMan Multiplayer", this::startPacManMultiplayer,
            "Snake", this::startSnakeGame
    );

    private void startFlappyBird() {
        int w = 360;
        int h = 640;
        FlappyBirdModel model = new FlappyBirdModel(w, h);
        FlappyBirdView view = new FlappyBirdView(w, h);
        FlappyBirdController controller = new FlappyBirdController(model, view);
        Scene scene = new Scene(view, w, h);
        controller.attach(scene);
        Stage stage = new Stage();
        stage.setTitle("Flappy Bird");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        Platform.runLater(view::requestFocus);
    }

    private void startPacMan() {
        GameModel model = new GameModel();
        PacManView view = new PacManView();
        PacManController controller = new PacManController(model, view);
        Scene scene = new Scene(view, GameModel.BOARD_WIDTH, GameModel.BOARD_HEIGHT);
        controller.attach(scene);
        Stage stage = new Stage();
        stage.setTitle("Pac Man");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
        Platform.runLater(view::requestFocus);
    }

    private void startSnakeGame() {
        SnakeGame game = new SnakeGame(600, 600);
        Scene scene = new Scene(game);
        Stage stage = new Stage();
        stage.setTitle("Snake Game");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        game.requestFocus();
        scene.setOnKeyPressed(e -> game.handleKeyPress(e.getCode()));
    }

    private void startPacManMultiplayer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Network/PacManMultiplayerLauncher.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("PacMan Multiplayer indító");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
