package unittests.board;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import takeiteasy.board.*;
import takeiteasy.tilepool.Tile;


public class BoardVanillaTest {
    @Test
    public void getTileOutOfBoardCoordinates() throws BadHexCoordinatesException {
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
    public void placeTileValidTileAndGetItBack() throws BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException {
        BoardVanilla b = new BoardVanilla();
        HexCoordinates coords = new HexCoordinates(0, 0, 0);
        Tile tile = new Tile(1,2,3);
        b.placeTile(tile,coords);
        Assertions.assertEquals(tile,b.getTile(coords));
    }

    @Test
    public void placeTileAtOutOfRangeCoordinates() throws BadHexCoordinatesException, CoordinatesOccupidedException {
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
    public void placeTileAtOccupiedPosition() throws BadHexCoordinatesException, OutOfBoardCoordinatesException {
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

    @Test
    public void computeScore123Board() throws BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException {
        BoardVanilla board = new BoardVanilla();
        Tile tile = new Tile(1,2,3);
        Tile otherTile = new Tile(5,6,4);
        for (Integer iii=-2;iii<3;++iii) {
            if (iii==0) {
                continue;
            }
            board.placeTile(tile, new HexCoordinates(0, iii, -iii));
            board.placeTile(tile, new HexCoordinates(iii, 0, -iii));
            board.placeTile(tile, new HexCoordinates(iii, -iii, 0));
            if (iii==1 || iii==-1) {
                board.placeTile(otherTile,new HexCoordinates(2*iii,-iii,-iii));
                board.placeTile(otherTile,new HexCoordinates(-iii,2*iii,-iii));
                board.placeTile(otherTile,new HexCoordinates(-iii,-iii,2*iii));
            }
            board.printBoard();
        }
        board.placeTile(tile, new HexCoordinates(0, 0, 0));
        board.printBoard();
        Assertions.assertEquals(5*(1+2+3),board.computeScore());
    }
}
