package unittests.utility;

import takeiteasy.board.HexCoordinates;
import takeiteasy.tilepool.Tile;

public class Pair<U, V> {
    public U tile;
    public V coordinate;

    public Pair(U tile, V coordinate) {
        this.tile = tile;
        this.coordinate = coordinate;
    }
}
