package unittests.board;

import org.json.JSONObject;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import takeiteasy.core.board.*;
import takeiteasy.core.board.exceptions.CoordinatesOccupiedException;
import takeiteasy.core.board.exceptions.OutOfBoardCoordinatesException;
import takeiteasy.core.tilepool.Tile;

import unittests.utility.Pair;

import java.util.ArrayList;

import static unittests.utility.Utility.*;

public class BoardVanillaTest {

    @Test
    public void getTileOutOfBoardCoordinates() {
        BoardVanilla board = new BoardVanilla();
        try{
            HexCoordinates badcoords = new HexCoordinates(100, 100, -200);
            assertThrows(OutOfBoardCoordinatesException.class, ()-> board.getTile(badcoords));
        }
        catch(Exception ignored){
        }
    }

    @Test
    public void getTileNoTileAtValidCoordinates(){
        BoardVanilla board = new BoardVanilla();
        try{
            HexCoordinates coords = new HexCoordinates(0, 0, 0);
            assertDoesNotThrow(()-> board.getTile(coords));
        }
        catch (Exception ignored){
        }
    }

    @Test
    public void placeTileValidTileAndGetItBack(){
        BoardVanilla board = new BoardVanilla();
        Tile tile = new Tile(1, 2, 3);
        try {
            HexCoordinates coords = new HexCoordinates(0, 0, 0);
            board.placeTile(tile, coords);
            assertEquals(tile, board.getTile(coords));
        }
        catch(Exception e){
            fail();
        }
    }

    @Test
    public void placeTileAtOutOfRangeCoordinates(){
        BoardVanilla board = new BoardVanilla();
        try{
            HexCoordinates badcoords = new HexCoordinates(100, 100, -200);
            Tile tile = new Tile(1, 2, 3);
            assertThrows(OutOfBoardCoordinatesException.class, ()-> board.placeTile(tile, badcoords));
        }
        catch(Exception ignored){
        }
    }

    @Test
    public void placeTileAtOccupiedPosition() {
        BoardVanilla board = new BoardVanilla();
        try{
            HexCoordinates coords = new HexCoordinates(0, 0, 0);
            Tile tile = new Tile(1,2,3);
            board.placeTile(tile,coords);
            assertThrows(CoordinatesOccupiedException.class, ()-> board.placeTile(tile,coords));
        }
        catch (Exception ignored){
        }
    }

    @Test
    public void testGetData(){
        BoardVanilla board = new BoardVanilla();
        try {
            HexCoordinates coords = new HexCoordinates(0, 0, 0);
            Tile tile = new Tile(9, 2,3);
            board.placeTile(tile, coords);
            JSONObject boardData = board.getData();
            JSONObject tileData = boardData.getJSONObject(coords.toString());
            JSONAssert.assertEquals(tile.getData(), tileData, true);
        }
        catch(Exception ignored){
        }
    }

    @Test
    public void testComputeScore() {
        BoardVanilla board = new BoardVanilla();
        Integer score = 54;
        ArrayList<Pair<Tile, HexCoordinates>> list = getTilesAndCoordinatesBoard11(score);
        list.forEach(tileHexCoordinatesPair -> {
            try {board.placeTile(tileHexCoordinatesPair.tile, tileHexCoordinatesPair.coordinate);}
            catch (Exception ignored) {}
        });
        assertEquals(score, board.computeScore());
    }
}
