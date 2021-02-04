package unittests.player;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import takeiteasy.board.*;
import takeiteasy.player.*;
import takeiteasy.tilepool.*;
import unittests.utility.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static unittests.utility.Utility.PlaceTileInput;

public class PlayerTest {

    @Test
    public void testStartMatch(){
        Player player = new Player("Sadr");
        try {
            player.startMatch();
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
        try {
            Player player = new Player("Sadr");
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
    public void testLeaveTheMatchFromWaitMatchState() {
        try {
            Player player = new Player("Sadr");
            player.leaveTheMatch();
            Assertions.assertEquals(IPlayer.State.LEFT, player.getState());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testLeaveTheMatchFromPlacingState() {
        try {
            Player player = new Player("Sadr");
            player.startMatch();
            player.leaveTheMatch();
            Assertions.assertEquals(IPlayer.State.LEFT, player.getState());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testLeaveTheMatchFromWaitOtherState() {
        try {
            Player player = new Player("Sadr");
            Tile tile = new Tile(1, 2, 3);
            HexCoordinates coordinates = new HexCoordinates(0, 0, 0);
            player.startMatch();
            player.placeTile(tile, coordinates);
            player.leaveTheMatch();
            Assertions.assertEquals(IPlayer.State.LEFT, player.getState());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testEndMatch() {
        try {
            Player player = new Player("Sadr");
            TilePool tilepool = new TilePool(1);

            int[][] coordinateSet = {
                    {-1, 2, -1}, {1, 1, -2}, {0, 0, 0},
                    {-2, 2, 0}, {-1, 1, 0}, {-1, 0, 1}, {-1, -1, 2},
                    {0, 2, -2}, {0, 1, -1}, {-2, 0, 2}, {0, -1, 1}, {0, -2, 2},
                    {-2, 1, 1}, {1, 0, -1}, {1, -1, 0}, {1, -2, 1},
                    {2, 0, -2}, {2, -1, -1}, {2, -2, 0}
            };
            Random rand = new Random(10);
            Collections.shuffle(Arrays.asList(coordinateSet), rand);
            HexCoordinates[] hexCoordinates = new HexCoordinates[19];
            for (int i = 0; i < 19; ++i) {
                int x = coordinateSet[i][0];
                int y = coordinateSet[i][1];
                int z = coordinateSet[i][2];
                hexCoordinates[i] = new HexCoordinates(x, y, z);
            }

            player.startMatch();
            for (int i = 0; i < 19; ++i) {
                Tile tile = tilepool.getTile(i);
                player.placeTile(tile, hexCoordinates[i]);
                if (i == 18) break;
                player.transitionFromWaitingPlayersToPlacing();
            }
            player.computeScore();
            player.endMatch();
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
    public void testComputeScore() throws BadHexCoordinatesException, OutOfProperStateException, OutOfBoardCoordinatesException, CoordinatesOccupidedException {

        Player player = new Player("Sadr");

        ArrayList<Pair<Tile, HexCoordinates>> list = new ArrayList<>();
        PlaceTileInput(list);
        player.startMatch();
        for(int i = 0; i < 19; ++i) {
            player.placeTile(list.get(i).tile, list.get(i).coordinate);
            if (i == 18) break;
            player.transitionFromWaitingPlayersToPlacing();
        }
        IBoard board = player.getBoard();
        Assertions.assertEquals(54, board.computeScore());
    }
}
