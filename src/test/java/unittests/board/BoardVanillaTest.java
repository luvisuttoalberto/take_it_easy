package unittests.board;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import takeiteasy.board.*;
import takeiteasy.tilepool.Tile;


public class BoardVanillaTest {
    @Test
    public void getTileInvalidCoordinates() throws BadHexCoordinatesException {
        BoardVanilla b = new BoardVanilla();
        HexCoordinates badcoords = new HexCoordinates(100, 100, -200);
        try {
            b.getTile(badcoords);
        }
        catch (OutOfBoardCoordinatesException ignored) {
        }
    }

    @Test
    public void getTileNoTileAtValidCoordinates() throws BadHexCoordinatesException, OutOfBoardCoordinatesException {
        BoardVanilla b = new BoardVanilla();
        HexCoordinates coords = new HexCoordinates(0, 0, 0);
        Assertions.assertNull(b.getTile(coords));
    }

    @Test
    public void placeTileValidTileAndGetItBack() throws BadHexCoordinatesException, OutOfBoardCoordinatesException {
        BoardVanilla b = new BoardVanilla();
        HexCoordinates coords = new HexCoordinates(0, 0, 0);
        Tile tile = new Tile(1,2,3);
        b.placeTile(tile,coords);
        Assertions.assertEquals(tile,b.getTile(coords));
    }

    @Test
    public void placeTileAtOutOfRangeCoordinates() throws BadHexCoordinatesException {
        BoardVanilla b = new BoardVanilla();
        HexCoordinates badcoords = new HexCoordinates(100, 100, -200);
        Tile tile = new Tile(1,2,3);
        try {
            b.placeTile(tile,badcoords);
        }
        catch (OutOfBoardCoordinatesException ignored) {
        }
    }

    @Test
    public void placeTileAtOccupiedPosition() throws BadHexCoordinatesException {
        BoardVanilla b = new BoardVanilla();
        HexCoordinates coords = new HexCoordinates(0, 0, 0);
        Tile tile = new Tile(1,2,3);
        try {
            b.placeTile(tile,coords);
            b.placeTile(tile,coords);
        }
        catch (CoordinatesOccupidedException ignored) {
        }
    }
}
