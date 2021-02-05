package unittests.board;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import takeiteasy.board.*;
import takeiteasy.tilepool.Tile;

import unittests.utility.Pair;

import java.util.ArrayList;

import static unittests.utility.Utility.PlaceTileInput;


public class BoardVanillaTest {

    @Test
    public void getTileOutOfBoardCoordinates() {
        BoardVanilla b = new BoardVanilla();
        try {
            HexCoordinates badcoords = new HexCoordinates(100, 100, -200);
            b.getTile(badcoords);
            fail();
        }
        catch (OutOfBoardCoordinatesException ignored) {
            //test passed
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    public void getTileNoTileAtValidCoordinates(){
        try {
            BoardVanilla b = new BoardVanilla();
            HexCoordinates coords = new HexCoordinates(0, 0, 0);
            Assertions.assertNull(b.getTile(coords));
        }
        catch(Exception e){
            fail();
        }
    }

    @Test
    public void placeTileValidTileAndGetItBack(){
        try {
            BoardVanilla b = new BoardVanilla();
            HexCoordinates coords = new HexCoordinates(0, 0, 0);
            Tile tile = new Tile(1, 2, 3);
            b.placeTile(tile, coords);
            Assertions.assertEquals(tile, b.getTile(coords));
        }
        catch(Exception e){
            fail();
        }
    }

    @Test
    public void placeTileAtOutOfRangeCoordinates(){
        try {
            BoardVanilla b = new BoardVanilla();
            HexCoordinates badcoords = new HexCoordinates(100, 100, -200);
            Tile tile = new Tile(1,2,3);

            b.placeTile(tile,badcoords);
            fail();
        }
        catch (OutOfBoardCoordinatesException ignored) {
            //test passed
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    public void placeTileAtOccupiedPosition() {
        try{
            BoardVanilla b = new BoardVanilla();
            HexCoordinates coords = new HexCoordinates(0, 0, 0);
            Tile tile = new Tile(1,2,3);

            b.placeTile(tile,coords);
            b.placeTile(tile,coords);
            fail();
        }
        catch (CoordinatesOccupidedException ignored) {
            //test passed
        }
        catch (Exception e){
            fail();
        }
    }

    @Test
    public void testComputeScore() {
        BoardVanilla board = new BoardVanilla();
        try {
            ArrayList<Pair<Tile, HexCoordinates>> list = new ArrayList<>();
            PlaceTileInput(list);
            for (int i = 0; i < 19; ++i) {
                board.placeTile(list.get(i).tile, list.get(i).coordinate);
            }
            Assertions.assertEquals(54, board.computeScore());
        }
        catch(Exception e){
            fail();
        }
    }
}
