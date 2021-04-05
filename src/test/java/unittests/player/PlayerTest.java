package unittests.player;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
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
            player.startMatch();
            assertEquals("PLACING", player.getData().get("playerState"));
            assertEquals(IPlayer.State.PLACING, player.getState());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testPlaceTile() {
        Player player = new Player("Dario");
        try {
            player.startMatch();
            Tile expectedTile = new Tile(1, 2, 3);
            HexCoordinates coords = new HexCoordinates(0,0,0);
            player.placeTile(expectedTile, coords);

            JSONObject data = player.getData();
            JSONObject boardData = data.getJSONObject("playerBoard");
            JSONObject insertedTileData = boardData.getJSONObject(coords.toString());
            Tile insertedTile = new Tile(   insertedTileData.getInt("top"),
                                            insertedTileData.getInt("left"),
                                            insertedTileData.getInt("right")
                                        );
//            Tile realTile = player.showTileFromBoardAtCoordinates(coords);
            assertEquals("WAIT_OTHER", player.getData().get("playerState"));
            assertEquals(IPlayer.State.WAIT_OTHER, player.getState());
            assertEquals(expectedTile, insertedTile);
        }
        catch (Exception e){
            fail();
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
            player.transitionFromWaitingPlayersToPlacing();
            assertEquals("PLACING", player.getData().get("playerState"));
            assertEquals(IPlayer.State.PLACING, player.getState());
        }
        catch (Exception e) {
            fail();
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
                if (i == 18) break;
                player.transitionFromWaitingPlayersToPlacing();
            }
//            player.computeScore();
            player.endMatch();
            //TODO: should we test it twice?
            assertEquals("WAIT_MATCH", player.getData().get("playerState"));
            assertEquals(IPlayer.State.WAIT_MATCH, player.getState());
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
//            Tile realTile = player.showTileFromBoardAtCoordinates(coordinates);
            JSONObject data = player.getData();
            //TODO: should we test it twice?
            assertEquals("WAIT_MATCH", data.get("playerState"));
            assertEquals(IPlayer.State.WAIT_MATCH, player.getState());
            assertTrue(data.getJSONObject("playerBoard").isEmpty());
//            Assertions.assertNull(realTile);
        }
        catch (Exception e) {
            fail();
        }
    }

    //TODO:should we remove this? player doesn't have a computeScore method anymore
//    @Test
//    public void testComputeScore() {
//        Integer score = 54;
//        try {
//            Player player = new Player("Dario");
//            ArrayList<Pair<Tile, HexCoordinates>> list = getTilesAndCoordinatesBoard11(score);
//            player.startMatch();
//            for (int i = 0; i < list.size(); ++i) {
//                player.placeTile(list.get(i).tile, list.get(i).coordinate);
//                if (i == 18) break;
//                player.transitionFromWaitingPlayersToPlacing();
//            }
//            IBoard board = player.getBoard();
//            JSONObject playerData = player.getData();
//            assertEquals(score, playerData.get("playerScore"));
//            assertEquals(score, board.computeScore());
//        }
//        catch(Exception e){
//           fail();
//        }
//    }
}
