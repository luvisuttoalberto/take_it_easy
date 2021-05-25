package takeiteasy.utility;

import takeiteasy.board.HexCoordinates;
import takeiteasy.board.exceptions.BadHexCoordinatesException;

import java.util.stream.IntStream;

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
        IntStream.range(0, coords.length)
                .forEach(iii -> {
                    try {
                        coords[iii] = new HexCoordinates(  coordinateSet[iii][0],
                                                            coordinateSet[iii][1],
                                                            coordinateSet[iii][2]
                                                    );
                    } catch (BadHexCoordinatesException ignored) {}
                });
        return coords;
    }
}
