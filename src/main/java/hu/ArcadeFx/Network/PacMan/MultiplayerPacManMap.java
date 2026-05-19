package hu.ArcadeFx.Network.PacMan;

import hu.ArcadeFx.Models.Games.PacMan.GameModel;

public class MultiplayerPacManMap {

    private int[][] map;

    public MultiplayerPacManMap() {
        reset();
    }

    public void reset() {
        String[] tileMap = GameModel.getTILE_MAP();
        map = new int[tileMap.length][tileMap[0].length()];
        for (int row = 0; row < tileMap.length; row++) {
            for (int col = 0; col < tileMap[row].length(); col++) {
                char tile = tileMap[row].charAt(col);
                switch (tile) {
                    case 'X' -> map[row][col] = 1;
                    case ' ' -> map[row][col] = 2;
                    default -> map[row][col] = 0;
                }
            }
        }
    }

    public boolean eatPelletAt(int pixelX, int pixelY) {
        int col = pixelX / GameModel.TILE_SIZE;
        int row = pixelY / GameModel.TILE_SIZE;
        if (row >= 0 && row < map.length && col >= 0 && col < map[0].length) {
            if (map[row][col] == 2) {
                map[row][col] = 0;
                return true;
            }
        }
        return false;
    }

    public boolean allPelletsEaten() {
        for (int[] row : map) {
            for (int tile : row) {
                if (tile == 2) {
                    return false;
                }
            }
        }
        return true;
    }

    public String mapToString() {
        StringBuilder builder = new StringBuilder();
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                builder.append(map[row][col]);
            }
            if (row < map.length - 1) {
                builder.append("/");
            }
        }
        return builder.toString();
    }
}