package takeiteasy.utility;

import takeiteasy.board.HexCoordinates;

public class Utility {
    public static HexCoordinates[] generateCoordinateStandard(){
        int[][] coordinateSet = {
                {-2, 2, 0}, {-2, 1, 1}, {-2, 0, 2},
                {-1, 2, -1}, {-1, 1, 0}, {-1, 0, 1}, {-1, -1, 2},
                {0, 2, -2}, {0, 1, -1}, {0, 0, 0}, {0, -1, 1}, {0, -2, 2},
                {1, 1, -2}, {1, 0, -1}, {1, -1, 0}, {1, -2, 1},
                {2, 0, -2}, {2, -1, -1}, {2, -2, 0}
        };
        HexCoordinates[] coords = new HexCoordinates[19];
        try {
            for (int j = 0; j < 19; ++j) {
                coords[j] = new HexCoordinates(coordinateSet[j][0], coordinateSet[j][1], coordinateSet[j][2]);
            }
        } catch (Exception ignored) {
        }
        return coords;
    }
}
