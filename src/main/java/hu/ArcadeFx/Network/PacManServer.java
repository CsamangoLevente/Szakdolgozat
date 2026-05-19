package hu.ArcadeFx.Network;

import hu.ArcadeFx.Models.Games.PacMan.*;
import hu.ArcadeFx.Network.PacMan.MultiplayerPacManMap;
import hu.ArcadeFx.Network.PacMan.MultiplayerPlayer;
import hu.ArcadeFx.Models.Games.PacMan.Pacman;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PacManServer {

    private static final int PORT = 5000;
    private static final int COLLISION_DISTANCE = 24;

    private static MultiplayerPacManMap pacManMap = new MultiplayerPacManMap();
    private static GameModel model = new GameModel();

    private static PrintWriter player1Writer;
    private static PrintWriter player2Writer;

    private static MultiplayerPlayer player1 = new MultiplayerPlayer(new Pacman(9 * GameModel.TILE_SIZE, 15 * GameModel.TILE_SIZE, GameModel.TILE_SIZE, GameModel.TILE_SIZE), Direction.RIGHT);
    private static MultiplayerPlayer player2 = new MultiplayerPlayer(new Pacman(10 * GameModel.TILE_SIZE, 15 * GameModel.TILE_SIZE, GameModel.TILE_SIZE, GameModel.TILE_SIZE), Direction.LEFT);

    private static final List<Ghost> ghosts = new ArrayList<>();

    private static boolean gameOver = false;
    private static boolean gameWon = false;

    private static boolean player1Ready = false;
    private static boolean player2Ready = false;
    private static boolean gameStarted = false;

    private static String statusMessage = "";

    public static void main(String[] args) {
        try {
            System.out.println("Szerver IP: " + java.net.InetAddress.getLocalHost().getHostAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("PacMan szerver elindult a " + PORT + " porton.");
        loadGhostsFromTileMap();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Várakozás játékosokra...");
            Socket socket1 = serverSocket.accept();
            System.out.println("Player 1 csatlakozott: " + socket1.getInetAddress());
            player1Writer = new PrintWriter(socket1.getOutputStream(), true);
            Socket socket2 = serverSocket.accept();
            System.out.println("Player 2 csatlakozott: " + socket2.getInetAddress());
            player2Writer = new PrintWriter(socket2.getOutputStream(), true);
            sendGameState();
            new Thread(() -> handlePlayer(socket1, 1)).start();
            new Thread(() -> handlePlayer(socket2, 2)).start();
            new Thread(PacManServer::gameLoop).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void gameLoop() {
        while (true) {
            try {
                Thread.sleep(50);
                if (gameStarted && !gameOver && !gameWon) {
                    autoMovePlayer(player1);
                    autoMovePlayer(player2);
                    moveGhosts();
                    checkGhostCollision();
                    sendGameState();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void handlePlayer(Socket socket, int playerNumber) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message;
            while ((message = reader.readLine()) != null) {
                if (message.equals("READY")) {
                    if (playerNumber == 1) {
                        player1Ready = true;
                    } else {
                        player2Ready = true;
                    }
                    if (player1Ready && player2Ready) {
                        gameStarted = true;
                        statusMessage = "";
                    } else {
                        statusMessage = "Várakozás a másik játékosra...";
                    }
                    sendGameState();
                    continue;
                }
                if (message.equals("EXIT")) {
                    statusMessage = "EXIT";
                    sendGameState();
                    Thread.sleep(500);
                    System.exit(0);
                }
                if (message.equals("ENTER")) {
                    if (gameOver || gameWon) {
                        resetGame();
                        sendGameState();
                    }
                    continue;
                }
                if (gameOver || gameWon) {
                    continue;
                }
                Direction direction = Direction.valueOf(message);
                movePlayer(playerNumber, direction);
                sendGameState();
            }
        } catch (Exception e) {
            System.out.println("Player " + playerNumber + " lecsatlakozott.");
        }
    }

    private static void movePlayer(int playerNumber, Direction direction) {
        MultiplayerPlayer player = playerNumber == 1 ? player1 : player2;
        if (!player.isAlive()) return;
        player.setDirection(direction);
    }

    private static void autoMovePlayer(MultiplayerPlayer player) {
        if(!player.isAlive()) return;
        model.tickPacman(player.getPacman(), player.getDirection());
        eatPellet(player);
    }

    private static void loadGhostsFromTileMap() {
        ghosts.clear();
        String[] tileMap = GameModel.getTILE_MAP();
        for (int row = 0; row < tileMap.length; row++) {
            for (int col = 0; col < tileMap[row].length(); col++) {
                char tile = tileMap[row].charAt(col);
                int x = col * GameModel.TILE_SIZE;
                int y = row * GameModel.TILE_SIZE;
                switch (tile) {
                    case 'r' -> ghosts.add(new Ghost(GhostType.RED, x, y, GameModel.TILE_SIZE, GameModel.TILE_SIZE));
                    case 'b' -> ghosts.add(new Ghost(GhostType.BLUE, x, y, GameModel.TILE_SIZE, GameModel.TILE_SIZE));
                    case 'p' -> ghosts.add(new Ghost(GhostType.PINK, x, y, GameModel.TILE_SIZE, GameModel.TILE_SIZE));
                    case 'o' -> ghosts.add(new Ghost(GhostType.ORANGE, x, y, GameModel.TILE_SIZE, GameModel.TILE_SIZE));
                }
            }
        }
    }

    private static void moveGhosts() {
        for (Ghost ghost : ghosts) {
            MultiplayerPlayer target = getNearestAlivePlayer(ghost);
            if (target == null) {
                continue;
            }
            model.setPacman(target.getPacman()
            );
            if (model.alignedToGrid(ghost)) {
                Direction newDirection = model.chooseGhostDirection(ghost);
                ghost.setDirection(newDirection, GameModel.SPEED);
            }
            ghost.step();
            if (model.wouldHitWall(ghost, ghost.getX(), ghost.getY())) {ghost.setPosition(ghost.getX() - ghost.getVelocityX(), ghost.getY() - ghost.getVelocityY());
                Direction newDirection = model.chooseGhostDirection(ghost);
                ghost.setDirection(newDirection, GameModel.SPEED);
            }
        }
    }

    private static MultiplayerPlayer getNearestAlivePlayer(Ghost ghost) {
        if (!player1.isAlive() && !player2.isAlive()) {
            return null;
        }
        if (!player1.isAlive()) return player2;
        if (!player2.isAlive()) return player1;
        int p1Distance = Math.abs(ghost.getX() - player1.getX()) + Math.abs(ghost.getY() - player1.getY());
        int p2Distance = Math.abs(ghost.getX() - player2.getX()) + Math.abs(ghost.getY() - player2.getY());
        return p1Distance <= p2Distance ? player1 : player2;
    }

    private static void eatPellet(MultiplayerPlayer player) {
        boolean eaten = pacManMap.eatPelletAt(player.getCenterX(), player.getCenterY());
        if (eaten) {
            player.addScore(10);
        }
        if (pacManMap.allPelletsEaten()) {
            gameWon = true;
            statusMessage = "Nyertetek! ENTER az újrakezdéshez";
        }
    }

    private static void checkGhostCollision() {
        for (Ghost ghost : ghosts) {
            checkPlayerGhost(player1, ghost, "Player 1");
            checkPlayerGhost(player2, ghost, "Player 2");
        }
        if (!player1.isAlive() && !player2.isAlive()) {
            gameOver = true;
            statusMessage = "A PacMan-ek meghaltak! ENTER újra";
        }
    }

    private static void checkPlayerGhost(MultiplayerPlayer player, Ghost ghost, String name) {
        if (player.isAlive() && isColliding(player.getX(), player.getY(), ghost.getX(), ghost.getY())) {
            player.loseLife();
            if (player.isAlive()) {
                statusMessage = name + " elkapva! Élet: " + player.getLives();
            } else {
                statusMessage = name + " kiesett!";
            }
            System.out.println(statusMessage);
        }
    }

    private static boolean isColliding(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy) < COLLISION_DISTANCE;
    }

    private static void sendGameState() {
        String state = "P1=" + player1.toNetworkString() + ";P2=" + player2.toNetworkString() + ";MAP=" + pacManMap.mapToString() + ";G=" + ghostsToNetworkString() + ";MSG=" + statusMessage
                + ";STARTED=" + gameStarted;
        System.out.println("Küldött állapot: " + state);
        if (player1Writer != null) {
            player1Writer.println(state);
        }
        if (player2Writer != null) {
            player2Writer.println(state);
        }
    }

    private static String ghostsToNetworkString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ghosts.size(); i++) {
            Ghost ghost = ghosts.get(i);
            builder.append(ghost.getType()).append(",").append(ghost.getX()).append(",").append(ghost.getY()).append(",").append(ghost.getDirection().name());
            if (i < ghosts.size() - 1) {
                builder.append("|");
            }
        }
        return builder.toString();
    }

    private static void resetGame() {
        pacManMap.reset();
        player1.resetFull();
        player2.resetFull();
        loadGhostsFromTileMap();
        gameOver = false;
        gameWon = false;
        statusMessage = "";
    }
}