package unittests.board;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        Integer score = 54;
        try {
            ArrayList<Pair<Tile, HexCoordinates>> list = getTilesAndCoordinatesBoard11(score);
            for (int i = 0; i < list.size(); ++i) {
                board.placeTile(list.get(i).tile, list.get(i).coordinate);
            }
            JSONObject boardData = board.getData();
            IntStream.range(0, list.size()).forEach(i -> {
                JSONObject tile = boardData.getJSONObject(list.get(i).coordinate.getX() + " " + list.get(i).coordinate.getY() + " " + list.get(i).coordinate.getZ());
                assertEquals(list.get(i).tile.getTop(), tile.get("top"));
                assertEquals(list.get(i).tile.getLeft(), tile.get("left"));
                assertEquals(list.get(i).tile.getRight(), tile.get("right"));
            });
            Assertions.assertEquals(score, board.computeScore());
        }
        catch(Exception e){
            fail();
        }
    }
}
