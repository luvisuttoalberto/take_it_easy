package unittests.player;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import takeiteasy.board.*;
import takeiteasy.player.IPlayer;
import takeiteasy.player.OutOfProperStateException;
import takeiteasy.player.Player;
import takeiteasy.tilepool.Tile;
import takeiteasy.tilepool.TilePool;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class PlayerTest {

    @Test
    public void testStartMatch() throws OutOfProperStateException {
        Player p = new Player("Kafka");
        p.startMatch();
        Assertions.assertEquals(p.getState(), IPlayer.State.Placing);
    }

    @Test
    public void testPlaceTile() throws OutOfBoardCoordinatesException, CoordinatesOccupidedException, BadHexCoordinatesException, OutOfProperStateException {
        Player p = new Player("Tornatore");
        p.startMatch();
        Tile expectedTile = new Tile(1, 2, 3);
        HexCoordinates c = new HexCoordinates(0,0,0);
        p.placeTile(expectedTile, c);
        Tile realTile = p.showTileFromBoardAtCoordinates(c);
        Assertions.assertEquals(expectedTile,realTile);
    }

    @Test
    public void testTransitionFromWaitingPlayersToPlacing() throws OutOfProperStateException, BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException {
        Player p = new Player("Campagna");
        Tile tile = new Tile(1, 2, 3);
        HexCoordinates c = new HexCoordinates(0,0,0);
        p.startMatch();
        p.placeTile(tile, c);
        p.transitionFromWaitingPlayersToPlacing();
        Assertions.assertEquals(p.getState(),IPlayer.State.Placing);
    }

    @Test
    public void testLeaveTheMatchFromWaitMatchState() {
        Player p = new Player("Bortolussi");
        p.leaveTheMatch();
        Assertions.assertEquals(p.getState(),IPlayer.State.Leave);
    }

    @Test
    public void testLeaveTheMatchFromPlacingState() throws OutOfProperStateException {
        Player p = new Player("Bortolussi");
        p.startMatch();
        p.leaveTheMatch();
        Assertions.assertEquals(p.getState(),IPlayer.State.Leave);
    }

    @Test
    public void testLeaveTheMatchFromWaitOtherState() throws OutOfProperStateException, BadHexCoordinatesException, OutOfBoardCoordinatesException, CoordinatesOccupidedException {
        Player p = new Player("Bortolussi");
        Tile tile = new Tile(1, 2, 3);
        HexCoordinates c = new HexCoordinates(0,0,0);
        p.startMatch();
        p.placeTile(tile, c);
        p.leaveTheMatch();
        Assertions.assertEquals(p.getState(),IPlayer.State.Leave);
    }

    @Test
    public void testEndMatch() throws OutOfProperStateException {
        Player p = new Player("Sartori");
        p.startMatch();
    }

    @Test
    public void testResetBoard() throws BadHexCoordinatesException, OutOfProperStateException, OutOfBoardCoordinatesException, CoordinatesOccupidedException {
        Player p = new Player("Cozzini");
        Tile tile = new Tile(1, 2, 3);
        HexCoordinates c = new HexCoordinates(0,0,0);
        p.startMatch();
        Tile expectedTile = p.showTileFromBoardAtCoordinates(c);
        p.placeTile(tile, c);
        p.resetBoard();
        Tile realTile = p.showTileFromBoardAtCoordinates(c);
        Assertions.assertEquals(expectedTile, realTile);
    }

    @Test
    public void testComputeScore() throws BadHexCoordinatesException, OutOfProperStateException, OutOfBoardCoordinatesException, CoordinatesOccupidedException {
        Player p = new Player("Kavka");
        TilePool tp = new TilePool(1);

        int[][] arr = {
                {-1, 2, -1},
                {1, 1, -2},
                {0,0,0},

                {-2, 2, 0},
                {-1, 1, 0},
                {-1, 0, 1},
                {-1, -1, 2},

                {0, 2, -2},
                {0, 1, -1},
                {-2, 0, 2},
                {0, -1, 1},
                {0,-2, 2},


                {-2, 1, 1},
                {1, 0, -1},
                {1, -1, 0},
                {1, -2, 1},

                {2, 0, -2},
                {2, -1, -1},
                {2, -2, 0}
        };
        Random rand = new Random(10);
        Collections.shuffle(Arrays.asList(arr), rand);
        HexCoordinates[] coords = new HexCoordinates[19];
        for (int i=0; i<19; ++i) {
            int x = arr[i][0];
            int y = arr[i][1];
            int z = arr[i][2];
            coords[i] = new HexCoordinates(x, y, z);
            }

        p.startMatch();
        for (int i=0; i<19; ++i) {
            Tile tile = tp.getTile(i);
            p.placeTile(tile, coords[i]);
            p.transitionFromWaitingPlayersToPlacing();
        }
        BoardVanilla b = p.PlayerBoard();
        b.printBoard();
        Assertions.assertEquals(27,p.computeScore());
    }
}
