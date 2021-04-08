package unittests.board;

import org.json.JSONObject;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import takeiteasy.board.*;
import takeiteasy.tilepool.Tile;

import unittests.utility.Pair;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static unittests.utility.Utility.*;

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
            assertNull(b.getTile(coords));
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
            assertEquals(tile, b.getTile(coords));
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
        Integer score = 54;
        try {
            ArrayList<Pair<Tile, HexCoordinates>> list = getTilesAndCoordinatesBoard11(score);
            for (Pair<Tile, HexCoordinates> tileHexCoordinatesPair : list) {
                board.placeTile(tileHexCoordinatesPair.tile, tileHexCoordinatesPair.coordinate);
            }
            JSONObject boardData = board.getData();
            //TODO: move to getDataTesting and use JSONObject instead
            IntStream.range(0, list.size()).forEach(i -> {
                JSONObject tile = boardData.getJSONObject(list.get(i).coordinate.toString());
                Tile realTile = new Tile(tile.getInt("top"), tile.getInt("left"), tile.getInt("right"));
                assertEquals(list.get(i).tile, realTile);
            });
            assertEquals(score, board.computeScore());
        }
        catch(Exception e){
            fail();
        }
    }
}
