package unittests.player;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import takeiteasy.board.*;
import takeiteasy.player.*;
import takeiteasy.tilepool.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class PlayerTest {

    @Test
    public void testStartMatch() throws OutOfProperStateException {
        Player player = new Player("Sadr");
        player.startMatch();
        Assertions.assertEquals(IPlayer.State.Placing, player.getState());
    }

    @Test
    public void testPlaceTile() throws OutOfBoardCoordinatesException, CoordinatesOccupidedException, BadHexCoordinatesException, OutOfProperStateException {
        Player player = new Player("Sadr");
        player.startMatch();
        Tile expectedTile = new Tile(1, 2, 3);
        HexCoordinates coordinates = new HexCoordinates(0,0,0);
        player.placeTile(expectedTile, coordinates);
        Tile realTile = player.showTileFromBoardAtCoordinates(coordinates);
        Assertions.assertEquals(expectedTile,realTile);
    }

    @Test
    public void testTransitionFromWaitingPlayersToPlacing() throws OutOfProperStateException, BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException {
        Player player = new Player("Sadr");
        Tile tile = new Tile(1, 2, 3);
        HexCoordinates coordinates = new HexCoordinates(0,0,0);
        player.startMatch();
        player.placeTile(tile, coordinates);
        player.transitionFromWaitingPlayersToPlacing();
        Assertions.assertEquals(IPlayer.State.Placing, player.getState());
    }

    @Test
    public void testLeaveTheMatchFromWaitMatchState() {
        Player player = new Player("Sadr");
        player.leaveTheMatch();
        Assertions.assertEquals(IPlayer.State.Leave, player.getState());
    }

    @Test
    public void testLeaveTheMatchFromPlacingState() throws OutOfProperStateException {
        Player player = new Player("Sadr");
        player.startMatch();
        player.leaveTheMatch();
        Assertions.assertEquals(IPlayer.State.Leave, player.getState());
    }

    @Test
    public void testLeaveTheMatchFromWaitOtherState() throws OutOfProperStateException, BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException {
        Player player = new Player("Sadr");
        Tile tile = new Tile(1, 2, 3);
        HexCoordinates coordinates = new HexCoordinates(0,0,0);
        player.startMatch();
        player.placeTile(tile, coordinates);
        player.leaveTheMatch();
        Assertions.assertEquals(IPlayer.State.Leave, player.getState());
    }

    @Test
    public void testEndMatch() throws BadHexCoordinatesException, OutOfProperStateException, OutOfBoardCoordinatesException, CoordinatesOccupidedException {
        Player player = new Player("Sadr");
        TilePool tilepool = new TilePool(1);

        int[][] coordinateSet = {
                {-1, 2, -1}, {1, 1, -2}, {0, 0, 0},
                {-2, 2, 0}, {-1, 1, 0}, {-1, 0, 1}, {-1, -1, 2},
                {0, 2, -2}, {0, 1, -1}, {-2, 0, 2}, {0, -1, 1}, {0,-2, 2},
                {-2, 1, 1}, {1, 0, -1}, {1, -1, 0}, {1, -2, 1},
                {2, 0, -2}, {2, -1, -1}, {2, -2, 0}
        };
        Random rand = new Random(10);
        Collections.shuffle(Arrays.asList(coordinateSet), rand);
        HexCoordinates[] hexCoordinates = new HexCoordinates[19];
        for (int i=0; i<19; ++i) {
            int x = coordinateSet[i][0];
            int y = coordinateSet[i][1];
            int z = coordinateSet[i][2];
            hexCoordinates[i] = new HexCoordinates(x, y, z);
        }

        player.startMatch();
        for (int i=0; i<19; ++i) {
            Tile tile = tilepool.getTile(i);
            player.placeTile(tile, hexCoordinates[i]);
            if(i==18) break;
            player.transitionFromWaitingPlayersToPlacing();
        }
        player.computeScore();
        player.endMatch();
        Assertions.assertEquals(IPlayer.State.WaitMatch, player.getState());
    }

    @Test
    public void testResetBoard() throws BadHexCoordinatesException, OutOfProperStateException, OutOfBoardCoordinatesException, CoordinatesOccupidedException {
        Player player = new Player("Sadr");
        Tile tile = new Tile(1, 2, 3);
        HexCoordinates coordinates = new HexCoordinates(0,0,0);
        player.startMatch();
        player.placeTile(tile, coordinates);
        player.resetBoard();
        Tile realTile = player.showTileFromBoardAtCoordinates(coordinates);
        Assertions.assertNull(realTile);
    }

    @Test
    public void testComputeScore() throws BadHexCoordinatesException, OutOfProperStateException, OutOfBoardCoordinatesException, CoordinatesOccupidedException {
        Player player = new Player("Sadr");
        TilePool tilePool = new TilePool(1);

        int[][] coordinateSet = {
                {-1, 2, -1}, {1, 1, -2}, {0, 0, 0},
                {-2, 2, 0}, {-1, 1, 0}, {-1, 0, 1}, {-1, -1, 2},
                {0, 2, -2}, {0, 1, -1}, {-2, 0, 2}, {0, -1, 1}, {0,-2, 2},
                {-2, 1, 1}, {1, 0, -1}, {1, -1, 0}, {1, -2, 1},
                {2, 0, -2}, {2, -1, -1}, {2, -2, 0}
        };
        Random rand = new Random(10);
        Collections.shuffle(Arrays.asList(coordinateSet), rand);
        HexCoordinates[] hexCoordinates = new HexCoordinates[19];
        for (int i=0; i<19; ++i) {
            int x = coordinateSet[i][0];
            int y = coordinateSet[i][1];
            int z = coordinateSet[i][2];
            hexCoordinates[i] = new HexCoordinates(x, y, z);
            }

        player.startMatch();
        for (int i=0; i<19; ++i) {
            Tile tile = tilePool.getTile(i);
            player.placeTile(tile, hexCoordinates[i]);
            if(i==18) break;
            player.transitionFromWaitingPlayersToPlacing();
        }
        IBoard board =player.getBoard();
        int Score = board.computeScore();
        Assertions.assertEquals(27,Score);
    }
}
