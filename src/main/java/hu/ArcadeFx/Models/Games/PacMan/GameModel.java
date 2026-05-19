package hu.ArcadeFx.Models.Games.PacMan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameModel {

    public static final int ROW_COUNT = 21;
    public static final int COLUMN_COUNT = 19;
    public static final int TILE_SIZE = 32;
    public static final int BOARD_WIDTH = COLUMN_COUNT * TILE_SIZE;
    public static final int BOARD_HEIGHT = ROW_COUNT * TILE_SIZE;
    public static final int SPEED = TILE_SIZE / 4;

    private static final String[] TILE_MAP = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    private final Random random = new Random();

    private double ghostAggression = 0.20;

    private int score = 0;
    private int lives = 3;
    private boolean gameOver = false;
    private boolean started = false;

    private Direction requestedDirection = null;

    private final List<Wall> walls = new ArrayList<>();
    private final List<Food> foods = new ArrayList<>();
    private final List<Ghost> ghosts = new ArrayList<>();
    private Pacman pacman;

    public GameModel() {
        loadMap();
        resetPositions();
    }

    public void onDirectionInput(Direction dir) {
        started = true;
        requestedDirection = dir;
        applyRequestedDirection();
    }

    public void tick() {
        if (gameOver) return;
        if (!started) return;
        applyRequestedDirection();
        pacman.step();
        for (Wall wall : walls) {
            if (pacman.intersects(wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }
        applyRequestedDirection();
        for (Ghost ghost : ghosts) {
            if (ghost.intersects(pacman)) {
                lives -= 1;
                if (lives <= 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
                return;
            }

            if (alignedToGrid(ghost)) {
                Direction nd = chooseGhostDirection(ghost);
                ghost.setDirection(nd, SPEED);
            }

            ghost.step();
            if (ghost.x < 0 || ghost.x + ghost.width > BOARD_WIDTH ||
                    ghost.y < 0 || ghost.y + ghost.height > BOARD_HEIGHT ||
                    wouldHitWall(ghost, ghost.x, ghost.y)) {
                ghost.x -= ghost.velocityX;
                ghost.y -= ghost.velocityY;
                Direction nd = chooseGhostDirection(ghost);
                ghost.setDirection(nd, SPEED);
            }
        }

        Food eaten = null;
        for (Food food : foods) {
            if (pacman.intersects(food)) {
                eaten = food;
                score += 10;
                break;
            }
        }
        if (eaten != null) foods.remove(eaten);
        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    public void restart() {
        loadMap();
        resetPositions();
        lives = 3;
        score = 0;
        gameOver = false;
    }

    public void resetPositions() {
        if (pacman == null) throw new IllegalStateException("Nincs Pac-Man start (P) a map-ben!");
        pacman.reset();
        pacman.stop();
        pacman.direction = Direction.RIGHT;
        requestedDirection = null;
        started = false;
        for (Ghost ghost : ghosts) {
            ghost.reset();
            Direction[] dirs = Direction.values();
            ghost.setDirection(dirs[random.nextInt(dirs.length)], SPEED);
            if (alignedToGrid(ghost) && wouldHitWall(ghost, ghost.x + ghost.velocityX, ghost.y + ghost.velocityY)) {
                ghost.setDirection(chooseGhostDirection(ghost), SPEED);
            }
        }
    }

    public void loadMap() {
        walls.clear();
        foods.clear();
        ghosts.clear();
        pacman = null;
        for (int r = 0; r < ROW_COUNT; r++) {
            String row = TILE_MAP[r];
            for (int c = 0; c < COLUMN_COUNT; c++) {
                char ch = row.charAt(c);
                int x = c * TILE_SIZE;
                int y = r * TILE_SIZE;
                if (ch == 'X') {
                    walls.add(new Wall(x, y, TILE_SIZE, TILE_SIZE));
                } else if (ch == ' ') {
                    foods.add(new Food(x + 14, y + 14, 4, 4));
                } else if (ch == 'P') {
                    pacman = new Pacman(x, y, TILE_SIZE, TILE_SIZE);
                } else if (ch == 'b') {
                    ghosts.add(new Ghost(GhostType.BLUE, x, y, TILE_SIZE, TILE_SIZE));
                } else if (ch == 'o') {
                    ghosts.add(new Ghost(GhostType.ORANGE, x, y, TILE_SIZE, TILE_SIZE));
                } else if (ch == 'p') {
                    ghosts.add(new Ghost(GhostType.PINK, x, y, TILE_SIZE, TILE_SIZE));
                } else if (ch == 'r') {
                    ghosts.add(new Ghost(GhostType.RED, x, y, TILE_SIZE, TILE_SIZE));
                }
            }
        }

        if (pacman == null) throw new IllegalStateException("A map-ben nincs 'P' (Pac-Man start)!");
    }

    public boolean alignedToGrid(Entity e) {
        return e.x % TILE_SIZE == 0 && e.y % TILE_SIZE == 0;
    }

    public void applyRequestedDirection() {
        if (requestedDirection == null) return;
        if (requestedDirection == pacman.direction) {
            if (pacman.velocityX != 0 || pacman.velocityY != 0) return;
        }
        boolean canTryTurn =
                (pacman.velocityX == 0 && pacman.velocityY == 0) ||
                        alignedToGrid(pacman) ||
                        pacman.direction.isOpposite(requestedDirection);
        if (!canTryTurn) return;
        int vx = requestedDirection.dx * SPEED;
        int vy = requestedDirection.dy * SPEED;
        if (wouldHitWall(pacman, pacman.x + vx, pacman.y + vy)) return;
        pacman.direction = requestedDirection;
        pacman.velocityX = vx;
        pacman.velocityY = vy;
    }

    public boolean canMove(MovingEntity mover, Direction dir) {
        int nx = mover.x + dir.dx * SPEED;
        int ny = mover.y + dir.dy * SPEED;
        if (nx < 0 || nx + mover.width > BOARD_WIDTH) return false;
        if (ny < 0 || ny + mover.height > BOARD_HEIGHT) return false;
        return !wouldHitWall(mover, nx, ny);
    }

    public boolean wouldHitWall(Entity mover, int nextX, int nextY) {
        for (Wall wall : walls) {
            if (nextX < wall.x + wall.width &&
                    nextX + mover.width > wall.x &&
                    nextY < wall.y + wall.height &&
                    nextY + mover.height > wall.y) {
                return true;
            }
        }
        return false;
    }

    public Direction chooseGhostDirection(Ghost ghost) {
        if (ghost.y == TILE_SIZE * 9 &&
                ghost.direction != Direction.UP &&
                ghost.direction != Direction.DOWN &&
                canMove(ghost, Direction.UP)) {
            return Direction.UP;
        }
        List<Direction> options = new ArrayList<>();
        for (Direction d : Direction.values()) {
            if (canMove(ghost, d)) options.add(d);
        }
        if (options.isEmpty()) return ghost.direction;
        if (options.size() > 1) {
            Direction opp = ghost.direction.opposite();
            options.remove(opp);
            if (options.isEmpty()) options.add(opp);
        }

        double baseRandomness = (ghost.getType() == GhostType.BLUE) ? 0.25 : 0.12;
        double randomness = baseRandomness + (1.0 - ghostAggression) * 0.45;
        randomness = Math.min(0.90, Math.max(0.0, randomness));
        if (random.nextDouble() < randomness) {
            return options.get(random.nextInt(options.size()));
        }
        int[] tgt = ghostTarget(ghost);
        int bestDist = Integer.MAX_VALUE;
        Direction best = options.get(0);
        for (Direction d : options) {
            int nx = ghost.x + d.dx * SPEED;
            int ny = ghost.y + d.dy * SPEED;
            int dist = Math.abs(nx - tgt[0]) + Math.abs(ny - tgt[1]);
            if (dist < bestDist) {
                bestDist = dist;
                best = d;
            }
        }
        return best;
    }

    public int[] ghostTarget(Ghost ghost) {
        int tx = pacman.x;
        int ty = pacman.y;
        if (ghost.getType() == GhostType.PINK) {
            tx += pacman.direction.dx * TILE_SIZE * 4;
            ty += pacman.direction.dy * TILE_SIZE * 4;
        } else if (ghost.getType() == GhostType.BLUE) {
            tx += pacman.direction.dx * TILE_SIZE * 2;
            ty += pacman.direction.dy * TILE_SIZE * 2;
            int noiseTiles = (int) Math.round((1.0 - ghostAggression) * 2.0);
            int dx = random.nextInt(noiseTiles * 2 + 1) - noiseTiles;
            int dy = random.nextInt(noiseTiles * 2 + 1) - noiseTiles;
            tx += dx * TILE_SIZE;
            ty += dy * TILE_SIZE;
        } else if (ghost.getType() == GhostType.ORANGE) {
            int dist = Math.abs(ghost.x - pacman.x) + Math.abs(ghost.y - pacman.y);
            int fleeTiles = (int) Math.round(2 + (1.0 - ghostAggression) * 6.0);
            if (dist < TILE_SIZE * fleeTiles) {
                tx = 0;
                ty = BOARD_HEIGHT - TILE_SIZE;
            }
        }
        tx = Math.max(0, Math.min(tx, BOARD_WIDTH - TILE_SIZE));
        ty = Math.max(0, Math.min(ty, BOARD_HEIGHT - TILE_SIZE));
        return new int[]{tx, ty};
    }

    public List<Wall> getWalls() { return List.copyOf(walls); }
    public List<Food> getFoods() { return List.copyOf(foods); }
    public List<Ghost> getGhosts() { return List.copyOf(ghosts); }
    public Pacman getPacman() { return pacman; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public boolean isGameOver() { return gameOver; }
    public static String[] getTILE_MAP() {
        return TILE_MAP;
    }


    public void tickPacman(Pacman pacman, Direction requestedDirection) {
        applyRequestedDirectionMultiplayer(pacman, requestedDirection);
        pacman.step();
        for (Wall wall : walls) {
            if (pacman.intersects(wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }
    }

    public void applyRequestedDirectionMultiplayer(Pacman pacman, Direction requestedDirection) {
        if (requestedDirection == null) return;
        if (requestedDirection == pacman.getDirection()) {
            if (pacman.getVelocityX() != 0 || pacman.getVelocityY() != 0) {
                return;
            }
        }
        boolean canTryTurn = (pacman.getVelocityX() == 0 && pacman.getVelocityY() == 0) || alignedToGrid(pacman) || pacman.getDirection().isOpposite(requestedDirection);
        if (!canTryTurn) return;
        int vx = requestedDirection.dx * SPEED;
        int vy = requestedDirection.dy * SPEED;
        if (wouldHitWall(pacman, pacman.getX() + vx, pacman.getY() + vy)) {
            return;
        }
        pacman.direction = requestedDirection;
        pacman.velocityX = vx;
        pacman.velocityY = vy;
    }

    public void setPacman(Pacman pacman) {
        this.pacman = pacman;
    }
}
