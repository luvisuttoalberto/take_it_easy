package unittests.player;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import takeiteasy.board.*;
import takeiteasy.player.*;
import takeiteasy.tilepool.*;
import unittests.utility.Pair;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static unittests.utility.Utility.*;

public class PlayerTest {

    @Test
    public void testStartMatch(){
        Player player = new Player("Sadr");
        try {
            player.startMatch();
            JSONObject playerData = player.getData();
            assertEquals("PLACING", playerData.get("playerState"));
            Assertions.assertEquals(IPlayer.State.PLACING, player.getState());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testPlaceTile() {
        Player player = new Player("Sadr");
        try {
            player.startMatch();
            Tile expectedTile = new Tile(1, 2, 3);
            HexCoordinates coordinates = new HexCoordinates(0,0,0);
            player.placeTile(expectedTile, coordinates);
            Tile realTile = player.showTileFromBoardAtCoordinates(coordinates);
            Assertions.assertEquals(expectedTile,realTile);
        }
        catch (Exception e){
            fail();
        }
    }

    @Test
    public void testTransitionFromWaitingPlayersToPlacing() {
        Player player = new Player("Sadr");
        try {
            Tile tile = new Tile(1, 2, 3);
            HexCoordinates coordinates = new HexCoordinates(0,0,0);
            player.startMatch();
            player.placeTile(tile, coordinates);
            player.transitionFromWaitingPlayersToPlacing();
            Assertions.assertEquals(IPlayer.State.PLACING, player.getState());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testEndMatch() {
        Player player = new Player("Sadr");
        try {
            ArrayList<Pair<Tile, HexCoordinates>> list = getTilesAndCoordinatesBoard11(54);
            player.startMatch();
            for(int i = 0; i < list.size(); ++i) {
                player.placeTile(list.get(i).tile, list.get(i).coordinate);
                if (i == 18) break;
                player.transitionFromWaitingPlayersToPlacing();
            }
            player.computeScore();
            player.endMatch();
            JSONObject playerData = player.getData();
            assertEquals("WAIT_MATCH", playerData.get("playerState"));
            Assertions.assertEquals(IPlayer.State.WAIT_MATCH, player.getState());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testResetBoard() {
        try {
            Player player = new Player("Sadr");
            Tile tile = new Tile(1, 2, 3);
            HexCoordinates coordinates = new HexCoordinates(0, 0, 0);
            player.startMatch();
            player.placeTile(tile, coordinates);
            player.reset();
            Tile realTile = player.showTileFromBoardAtCoordinates(coordinates);
            Assertions.assertNull(realTile);
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testComputeScore() {
        Integer score = 54;
        try {
            Player player = new Player("Sadr");
            ArrayList<Pair<Tile, HexCoordinates>> list = getTilesAndCoordinatesBoard11(score);
            player.startMatch();
            for (int i = 0; i < list.size(); ++i) {
                player.placeTile(list.get(i).tile, list.get(i).coordinate);
                if (i == 18) break;
                player.transitionFromWaitingPlayersToPlacing();
            }
            IBoard board = player.getBoard();
            JSONObject playerData = player.getData();
            assertEquals(score, playerData.get("playerScore"));
            Assertions.assertEquals(score, board.computeScore());
        }
        catch(Exception e){
           fail();
        }
    }
}
