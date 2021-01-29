package unittests.board;

import org.junit.jupiter.api.Test;
import takeiteasy.board.*;


public class BoardVanillaTest {
    @Test
    public void getTileInvalidCoordinates() throws BadHexCoordinatesException {
        BoardVanilla b = new BoardVanilla();
        HexCoordinates badcoords = new HexCoordinates(100, 100, -200);
        try {
            b.getTile(badcoords);
        }
        catch (OutOfBoardCoordinatesException ignored) {
        }
    }

}
