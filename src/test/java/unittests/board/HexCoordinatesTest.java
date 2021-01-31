package unittests.board;
import takeiteasy.board.BadHexCoordinatesException;
import takeiteasy.board.HexCoordinates;
import org.junit.jupiter.api.Test;

import javax.xml.catalog.Catalog;

public class HexCoordinatesTest {

    @Test
    public void badCoordinatesTest() throws Exception {
        try {
            HexCoordinates hc = new HexCoordinates(1, 2, 3);
        }
        catch (BadHexCoordinatesException ignored){
        }
    }
}