package unittests.utility;

import takeiteasy.board.*;
import takeiteasy.tilepool.Tile;
import takeiteasy.tilepool.TilePool;

import java.util.ArrayList;

public final class Utility {
    public static ArrayList<Pair<Tile, HexCoordinates>> PlaceTileInput(ArrayList < Pair<Tile,HexCoordinates>> l) throws BadHexCoordinatesException {
        TilePool tilepool = new TilePool(11);
        int[][] coordinateSet = {
                /*{-2, 2, 0}, {-2, 1, 1}, {-2, 0, 2},
                {-1, 2, -1}, {-1, 1, 0}, {-1, 0, 1}, {-1, -1, 2},
                {0, 2, -2}, {0, 1, -1}, {0, 0, 0}, {0, -1, 1}, {0, -2, 2},
                {1, 1, -2}, {1, 0, -1}, {1, -1, 0}, {1, -2, 1},
                {2, 0, -2}, {2, -1, -1}, {2, -2, 0}*/

                {-2, 2, 0}, {-2, 1, 1}, {-2, 0, 2},
                {-1, 2, -1}, {-1, 1, 0}, {-1, 0, 1}, {-1, -1, 2},
                {0, -2, 2}, {0, 1, -1}, {0, 0, 0}, {0, -1, 1}, {0, 2, -2},
                {1, 1, -2}, {2, -1, -1}, {1, -1, 0}, {1, -2, 1},
                {2, 0, -2}, {1, 0, -1}, {2, -2, 0}
        };
        for (int i = 0; i < tilepool.getSize(); ++i) {
            int x = coordinateSet[i][0];
            int y = coordinateSet[i][1];
            int z = coordinateSet[i][2];
            HexCoordinates coordinates = new HexCoordinates(x, y, z);
            Pair<Tile, HexCoordinates> pair = new Pair<Tile, HexCoordinates>(tilepool.getTile(i), coordinates);
            l.add(pair);
        }

        return l;
    }

    public static void main(String[] args) throws BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException {
        BoardVanilla b = new BoardVanilla();
        ArrayList<Pair<Tile, HexCoordinates>> list = new ArrayList<Pair<Tile, HexCoordinates>>();
        PlaceTileInput(list);

        for(int i = 0; i < 19; ++i){
            b.placeTile(list.get(i).tile, list.get(i).coordinate);
        }

        b.printBoard();
        System.out.println(b.computeScore());

    }
}
