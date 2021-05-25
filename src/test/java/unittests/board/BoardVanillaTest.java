package unittests.board;

import org.json.JSONObject;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import takeiteasy.board.*;
import takeiteasy.board.exceptions.CoordinatesOccupiedException;
import takeiteasy.board.exceptions.OutOfBoardCoordinatesException;
import takeiteasy.tilepool.Tile;

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
        try{
            BoardVanilla board = new BoardVanilla();
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
        try {
            ArrayList<Pair<Tile, HexCoordinates>> list = getTilesAndCoordinatesBoard11(score);
            for (Pair<Tile, HexCoordinates> tileHexCoordinatesPair : list) {
                board.placeTile(tileHexCoordinatesPair.tile, tileHexCoordinatesPair.coordinate);
            }
            assertEquals(score, board.computeScore());
        }
        catch(Exception ignored){
        }
    }
}
