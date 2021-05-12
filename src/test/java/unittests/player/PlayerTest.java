package unittests.player;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import takeiteasy.board.*;
import takeiteasy.player.*;
import takeiteasy.tilepool.*;
import unittests.utility.Pair;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import static unittests.utility.Utility.*;

public class PlayerTest {

    @Test
    public void testStartMatch(){
        Player player = new Player("Dario");
        try {
            assertDoesNotThrow(player::startMatch);
            assertEquals(IPlayer.State.PLACING, player.getState());
        }
        catch (Exception ignored) {
        }
    }

    @Test
    public void testConsistentGetData(){
        Player player = new Player("Carlos");
        JSONObject playerData = player.getData();
        assertNotNull(playerData.opt("playerScore"));
        assertNotNull(playerData.opt("playerState"));
        assertNotNull(playerData.opt("playerBoard"));
    }

    @Test
    public void testPlaceTile() {
        Player player = new Player("Dario");
        try {
            player.startMatch();
            Tile expectedTile = new Tile(1, 2, 3);
            JSONObject expectedTileData = expectedTile.getData();
            HexCoordinates coords = new HexCoordinates(0,0,0);
            assertDoesNotThrow(()->player.placeTile(expectedTile, coords));

            JSONObject data = player.getData();
            JSONObject boardData = data.getJSONObject("playerBoard");
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
        try {
            Tile tile = new Tile(1, 2, 3);
            HexCoordinates coordinates = new HexCoordinates(0,0,0);
            player.startMatch();
            player.placeTile(tile, coordinates);
            assertDoesNotThrow(player::transitionFromWaitingPlayersToPlacing);
            assertEquals(IPlayer.State.PLACING, player.getState());
        }
        catch (Exception ignored) {
        }
    }

    @Test
    public void testEndMatch() {
        Player player = new Player("Dario");
        try {
            ArrayList<Pair<Tile, HexCoordinates>> list = getTilesAndCoordinatesBoard11(54);
            player.startMatch();
            for(int i = 0; i < list.size(); ++i) {
                player.placeTile(list.get(i).tile, list.get(i).coordinate);
                if (i == list.size() - 1) break;
                player.transitionFromWaitingPlayersToPlacing();
            }
            assertDoesNotThrow(player::endMatch);
            assertEquals(IPlayer.State.WAIT_MATCH, player.getState());
        }
        catch (Exception ignored) {
        }
    }

    @Test
    public void testGetData(){
        Player player = new Player("Carlos");
        Integer score = 54;
        try {
            ArrayList<Pair<Tile, HexCoordinates>> list = getTilesAndCoordinatesBoard11(score);
            player.startMatch();
            JSONObject playerData = player.getData();
            assertEquals("PLACING", playerData.get("playerState"));
            JSONObject boardData = new JSONObject();
            for(int i = 0; i < list.size(); ++i) {
                player.placeTile(list.get(i).tile, list.get(i).coordinate);
                boardData.put(list.get(i).coordinate.toString(), list.get(i).tile.getData());
                if (i == list.size() - 1) break;
                playerData = player.getData();
                assertEquals("WAIT_OTHER", playerData.get("playerState"));
                player.transitionFromWaitingPlayersToPlacing();
            }
            playerData = player.getData();
            JSONAssert.assertEquals(boardData, playerData.getJSONObject("playerBoard"), true);
            player.endMatch();
            playerData = player.getData();
            assertEquals("WAIT_MATCH", playerData.get("playerState"));
            assertEquals(score, playerData.get("playerScore"));
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testResetBoard() {
        try {
            Player player = new Player("Dario");
            Tile tile = new Tile(1, 2, 3);
            HexCoordinates coordinates = new HexCoordinates(0, 0, 0);
            player.startMatch();
            player.placeTile(tile, coordinates);
            player.reset();
            JSONObject data = player.getData();
            assertEquals(IPlayer.State.WAIT_MATCH, player.getState());
            assertTrue(data.getJSONObject("playerBoard").isEmpty());
        }
        catch (Exception e) {
            fail();
        }
    }
}
