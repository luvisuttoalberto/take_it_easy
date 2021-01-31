package unittests.player;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import takeiteasy.board.BoardVanilla;
import takeiteasy.player.Player;
import takeiteasy.tilepool.Tile;

public class PlayerTest {
    @Test
    public void testGetSetName() {
        Player p = new Player("Casagrande");
        p.setName("Medvet");
        Assertions.assertEquals("Medvet", p.getName());
    }

    @Test
    public void testPlaceTile() {
        Player p = new Player("Tornatore");
        Tile tile = new Tile(2, 3, 4);
        BoardVanilla b = new BoardVanilla();
        Assertions.assertEquals(1,1);

    }
}
