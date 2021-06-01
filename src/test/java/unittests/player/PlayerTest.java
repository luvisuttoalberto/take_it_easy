package unittests.player;

import org.json.JSONObject;
import takeiteasy.core.JSONKeys;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import takeiteasy.core.board.*;
import takeiteasy.core.player.*;
import takeiteasy.core.tilepool.*;
import unittests.utility.Pair;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static unittests.utility.Utility.*;

public class PlayerTest {

    @Test
    public void testStartMatch(){
        Player player = new Player("Dario");
        assertDoesNotThrow(player::startMatch);
        assertEquals(IPlayer.State.PLACING, player.getState());
    }

    @Test
    public void testConsistentGetData(){
        Player player = new Player("Carlos");
        JSONObject playerData = player.getData();
        assertNotNull(playerData.opt(JSONKeys.PLAYER_SCORE));
        assertNotNull(playerData.opt(JSONKeys.PLAYER_STATE));
        assertNotNull(playerData.opt(JSONKeys.PLAYER_BOARD));
    }

    @Test
    public void testPlaceTile() {
        Player player = new Player("Dario");
        Tile expectedTile = new Tile(1, 2, 3);
        JSONObject expectedTileData = expectedTile.getData();
        try {
            player.startMatch();
            HexCoordinates coords = new HexCoordinates(0,0,0);
            assertDoesNotThrow(() -> player.placeTile(expectedTile, coords));

            JSONObject data = player.getData();
            JSONObject boardData = data.getJSONObject(JSONKeys.PLAYER_BOARD);
            JSONObject insertedTileData = boardData.getJSONObject(coords.toString());

            assertEquals(IPlayer.State.WAIT_OTHER, player.getState());
            JSONAssert.assertEquals(expectedTileData, insertedTileData, true);
        }
        catch (Exception ignored){
        }
    }

    @Test
    public void testTransitionFromWaitingPlayersToPlacing() {
        Player player = new Player("Dario");
        Tile tile = new Tile(1, 2, 3);
        try {
            HexCoordinates coordinates = new HexCoordinates(0,0,0);
            player.startMatch();
            player.placeTile(tile, coordinates);
        }
        catch (Exception ignored) {
        }
        assertDoesNotThrow(player::transitionFromWaitingPlayersToPlacing);
        assertEquals(IPlayer.State.PLACING, player.getState());
    }

    @Test
    public void testEndMatch() {
        Player player = new Player("Dario");
        ArrayList<Pair<Tile, HexCoordinates>> list = getTilesAndCoordinatesBoard11(54);
        try {player.startMatch();}
        catch (Exception ignored) {}
        IntStream.range(0, list.size()-1)
                .mapToObj(list::get)
                .forEach(tileHexCoordinatesPair -> {
                    try{
                        player.placeTile(tileHexCoordinatesPair.tile, tileHexCoordinatesPair.coordinate);
                        player.transitionFromWaitingPlayersToPlacing();
                    }
                    catch (Exception ignored){}
                });
        try{player.placeTile(list.get(list.size() - 1).tile, list.get(list.size() - 1).coordinate);}
        catch (Exception ignored){}
        assertDoesNotThrow(player::endMatch);
        assertEquals(IPlayer.State.WAIT_MATCH, player.getState());
    }

    @Test
    public void testGetData(){
        Player player = new Player("Carlos");
        Integer score = 54;
        ArrayList<Pair<Tile, HexCoordinates>> list = getTilesAndCoordinatesBoard11(score);

        try {player.startMatch();}
        catch (Exception e) {fail();}

        JSONObject playerData = player.getData();
        assertEquals("PLACING", playerData.get(JSONKeys.PLAYER_STATE));

        JSONObject boardData = new JSONObject();
        IntStream.range(0, list.size()-1)
                .mapToObj(list::get)
                .forEach(tileHexCoordinatesPair -> {
                    try {
                        player.placeTile(tileHexCoordinatesPair.tile, tileHexCoordinatesPair.coordinate);
                        boardData.put(tileHexCoordinatesPair.coordinate.toString(), tileHexCoordinatesPair.tile.getData());
                        assertEquals("WAIT_OTHER", player.getData().get(JSONKeys.PLAYER_STATE));
                        player.transitionFromWaitingPlayersToPlacing();
                    } catch (Exception e) {fail();}
                });
        try{player.placeTile(list.get(list.size()-1).tile, list.get(list.size() - 1).coordinate);}
        catch (Exception e) {fail();}
        boardData.put(list.get(list.size() - 1).coordinate.toString(), list.get(list.size() - 1).tile.getData());

        playerData = player.getData();
        JSONAssert.assertEquals(boardData, playerData.getJSONObject(JSONKeys.PLAYER_BOARD), true);

        try{player.endMatch();}
        catch (Exception e) {fail();}

        playerData = player.getData();

        assertEquals("WAIT_MATCH", playerData.get(JSONKeys.PLAYER_STATE));
        assertEquals(score, playerData.get(JSONKeys.PLAYER_SCORE));
    }

    @Test
    public void testResetBoard() {
        Player player = new Player("Dario");
        Tile tile = new Tile(1, 2, 3);

        try {
            HexCoordinates coordinates = new HexCoordinates(0, 0, 0);
            player.startMatch();
            player.placeTile(tile, coordinates);
        }
        catch (Exception e) {
            fail();
        }

        player.reset();
        JSONObject playerData = player.getData();
        assertEquals(IPlayer.State.WAIT_MATCH, player.getState());
        assertTrue(playerData.getJSONObject(JSONKeys.PLAYER_BOARD).isEmpty());
    }
}
