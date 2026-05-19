package hu.ArcadeFx.Network;

import hu.ArcadeFx.Models.Games.PacMan.Direction;
import hu.ArcadeFx.Models.Games.PacMan.GameModel;
import hu.ArcadeFx.Models.Games.PacMan.Ghost;
import hu.ArcadeFx.Models.Games.PacMan.GhostType;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PacManClient extends Application {

    private int[][] map = new int[GameModel.ROW_COUNT][GameModel.COLUMN_COUNT];

    private Canvas canvas;
    private GraphicsContext gc;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    private int p1X = 50;
    private int p1Y = 50;

    private int p2X = 250;
    private int p2Y = 250;

    private Direction p1Direction = Direction.RIGHT;
    private Direction p2Direction = Direction.LEFT;

    private int p1Score = 0;
    private int p2Score = 0;

    private int p1Lives = 3;
    private int p2Lives = 3;

    private boolean p1Alive = true;
    private boolean p2Alive = true;

    private String statusMessage = "";

    private final List<Ghost> ghosts = new ArrayList<>();

    private final Map<GhostType, Image> ghostImages = new EnumMap<>(GhostType.class);

    private Image pacmanUp;
    private Image pacmanDown;
    private Image pacmanLeft;
    private Image pacmanRight;
    private Image wallImage;

    private static final int PORT = 5000;
    private String serverIp = "localhost";
    private boolean gameStarted = false;
    private Stage stage;
    private Runnable onGameStarted;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        loadImages();
        Parameters parameters = getParameters();
        if (parameters != null) {
            List<String> args = parameters.getRaw();
            if (!args.isEmpty()) {
                serverIp = args.getFirst();
            }
        }
        socket = new Socket(serverIp, PORT);
        writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println("READY");
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        canvas = new Canvas(GameModel.BOARD_WIDTH, GameModel.BOARD_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        Pane root = new Pane(canvas);
        Scene scene = new Scene(root, GameModel.BOARD_WIDTH, GameModel.BOARD_HEIGHT);
        scene.setOnKeyPressed(event -> {
            switch(event.getCode()){
                case W,UP -> writer.println("UP");
                case S,DOWN -> writer.println("DOWN");
                case A,LEFT -> writer.println("LEFT");
                case D,RIGHT -> writer.println("RIGHT");
                case ENTER -> writer.println("ENTER");
                case ESCAPE -> writer.println("EXIT");
            }
        });
        stage.setTitle("PacMan Multiplayer");
        stage.setScene(scene);
        root.requestFocus();
        drawGame();
        startListeningToServer();
    }

    public PacManClient() {
    }

    public PacManClient(String serverIp, Runnable onGameStarted) {
        this.serverIp = serverIp;
        this.onGameStarted = onGameStarted;
    }

    private void loadImages(){
        pacmanUp = loadImage("pacmanUp.png");
        pacmanDown = loadImage("pacmanDown.png");
        pacmanLeft = loadImage("pacmanLeft.png");
        pacmanRight = loadImage("pacmanRight.png");
        wallImage = loadImage("wall.png");
        ghostImages.put(GhostType.RED, loadImage("redGhost.png"));
        ghostImages.put(GhostType.BLUE, loadImage("blueGhost.png"));
        ghostImages.put(GhostType.PINK, loadImage("pinkGhost.png"));
        ghostImages.put(GhostType.ORANGE, loadImage("orangeGhost.png")
        );
    }

    private Image loadImage(String fileName){
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + fileName)));
    }

    private void startListeningToServer(){
        new Thread(() -> {
            try {
            String serverMessage;
            while((serverMessage= reader.readLine()) !=null) {
                String msg= serverMessage;
                Platform.runLater(() -> {
                    if(msg.contains("EXIT")) {
                        Stage stage= (Stage) canvas.getScene().getWindow();
                            stage.close();
                            Platform.exit();
                            return;
                        }
                        updateGameState(msg);
                        drawGame();
                        if (gameStarted && !stage.isShowing()) {
                            stage.show();
                        if (onGameStarted != null) {
                            onGameStarted.run();
                        }
                    }
                });
            }
            } catch (java.net.SocketException e) {
                System.out.println("Kapcsolat lezárva.");
            }catch(Exception e){
                e.printStackTrace();
            }

        }).start();
    }

    private void updateGameState(String state){
        try {
            String[] parts = state.split(";");
            String[] p1Data = parts[0].replace("P1=", "").split(",");
            String[] p2Data = parts[1].replace("P2=", "").split(",");
            p1X = Integer.parseInt(p1Data[0]);
            p1Y = Integer.parseInt(p1Data[1]);
            p1Direction = Direction.valueOf(p1Data[2]);
            p1Score = Integer.parseInt(p1Data[3]);
            p1Lives = Integer.parseInt(p1Data[4]);
            p1Alive= Boolean.parseBoolean(p1Data[5]);
            p2X = Integer.parseInt(p2Data[0]);
            p2Y = Integer.parseInt(p2Data[1]);
            p2Direction= Direction.valueOf(p2Data[2]);
            p2Score = Integer.parseInt(p2Data[3]);
            p2Lives = Integer.parseInt(p2Data[4]);
            p2Alive= Boolean.parseBoolean(p2Data[5]);
            if (parts.length >= 3) {
                String mapData = parts[2].replace("MAP=", "");
                updateMap(mapData);
            }
            if (parts.length >= 4) {
                String ghostData = parts[3].replace("G=", "");
                updateGhosts(ghostData);
            }
            if (parts.length >= 5) {
                statusMessage = parts[4].replace("MSG=", "");
            }
            if (parts.length >= 6) {
                gameStarted = Boolean.parseBoolean(parts[5].replace("STARTED=", ""));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void updateGhosts (String ghostData) {
        ghosts.clear();
        if (ghostData==null || ghostData.isBlank()) {
            return;
        }
        String[] ghostParts = ghostData.split("\\|");
        for(String ghostInfo : ghostParts){
            String[] data = ghostInfo.split(",");
            GhostType type = GhostType.valueOf(data[0]);
            int x = Integer.parseInt(data[1]);
            int y = Integer.parseInt(data[2]);
            Ghost ghost = new Ghost(type, x, y, GameModel.TILE_SIZE, GameModel.TILE_SIZE);
            ghosts.add(ghost);
        }
    }

    private void updateMap(String mapData){
        String[] rows = mapData.split("/");
        for (int row = 0; row < rows.length && row < map.length; row++) {
            for(int col = 0; col < rows[row].length() && col < map[row].length; col++){
                map[row][col] = Character.getNumericValue(rows[row].charAt(col));
            }
        }
    }

    private void drawGame(){
        drawMap();
        drawPlayers();
        drawGhosts();
        drawHud();
    }

    private void drawMap(){
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, GameModel.BOARD_WIDTH, GameModel.BOARD_HEIGHT);
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                int x = col * GameModel.TILE_SIZE;
                int y = row * GameModel.TILE_SIZE;
                if (map[row][col] == 1) {
                    gc.drawImage(wallImage, x, y, GameModel.TILE_SIZE, GameModel.TILE_SIZE);
                } else if (map[row][col] == 2){
                    gc.setFill(Color.WHITE);
                    gc.fillRect(x+14, y+14, 4, 4);
                }
            }
        }
    }

    private void drawPlayers(){
        if (p1Alive) {
            gc.drawImage(getPacmanImage(p1Direction), p1X, p1Y, GameModel.TILE_SIZE, GameModel.TILE_SIZE);
        }
        if (p2Alive) {
            gc.drawImage(getPacmanImage(p2Direction), p2X, p2Y, GameModel.TILE_SIZE, GameModel.TILE_SIZE);
        }
    }

    private void drawGhosts(){
        for (Ghost ghost : ghosts){
            Image image = ghostImages.get(ghost.getType());
            if(image != null){
                gc.drawImage(image, ghost.getX(), ghost.getY(), ghost.getWidth(), ghost.getHeight());
            }
        }
    }

    private void drawHud(){
        gc.setFont(Font.font("Arial", 18));
        gc.setFill(Color.WHITE);
        gc.fillText("P1 x" + p1Lives + " Score: " + p1Score, 16, 24);
        gc.fillText("P2 x" + p2Lives + " Score: " + p2Score, 16, 48);
        if (statusMessage!=null && !statusMessage.isBlank()) {
            gc.fillText(statusMessage, 16, GameModel.BOARD_HEIGHT-10);
        }
    }

    private Image getPacmanImage(Direction direction){
        return switch (direction) {
            case UP -> pacmanUp;
            case DOWN -> pacmanDown;
            case LEFT -> pacmanLeft;
            case RIGHT -> pacmanRight;
        };
    }

    public static void main(String[] args){
        launch(args);
    }
}