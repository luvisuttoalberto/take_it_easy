package unittests.player;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import takeiteasy.board.*;
import takeiteasy.player.IPlayer;
import takeiteasy.player.OutOfProperStateException;
import takeiteasy.player.Player;
import takeiteasy.tilepool.Tile;

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
}
